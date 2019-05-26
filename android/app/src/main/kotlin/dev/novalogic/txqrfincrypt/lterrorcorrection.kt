package dev.novalogic.txqrfincrypt

import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.stream.Collectors
import java.util.zip.Deflater
import java.util.zip.Inflater
import kotlin.experimental.and
import kotlin.experimental.xor
import kotlin.math.*

fun genTau(s: Double, k: Int, delta: Double): MutableList<Double> {

    val pivot = floor(k / s).toInt()
    val distribution = mutableListOf<Double>()

    for (d in 1 until pivot) {
        distribution.add(s / k.toDouble() * 1 / d.toDouble())
    }

    distribution.add(s / k.toDouble() * log(s / delta, E))

    for (d in pivot until k) {
        distribution.add(0.0)
    }

    return distribution
}

fun genRho(k: Int): MutableList<Double> {
    val distribution = mutableListOf<Double>()

    distribution.add(1 / k.toDouble())

    for (d in 2 until k + 1) {
        distribution.add(1 / (d.toDouble() * (d.toDouble() - 1)))
    }

    return distribution
}

fun genMu(k: Int, delta: Double, c: Double): MutableList<Double> {
    val s = c * log(k.toDouble() / delta, E) * sqrt(k.toDouble())

    val tau = genTau(s, k, delta)
    val rho = genRho(k)
    val normalizer = rho.sum() + tau.sum()

    val distribution = mutableListOf<Double>()

    for (d in 0 until k) {
        distribution.add((rho[d] + tau[d]) / normalizer)
    }

    return distribution
}

fun genRsdCdf(k: Int, delta: Double, c: Double): MutableList<Double> {
    val mu = genMu(k, delta, c)
    val distribution = mutableListOf<Double>()

    for (d in 0 until k) {
        distribution.add(mu.slice(IntRange(0, d)).sum())
    }

    return distribution
}

const val PRNG_A = 16807L
const val PRNG_M = (1L shl 31) - 1L
const val PRNG_MAX_RAND = PRNG_M - 1L

class RobustSolitonDistributionPRNG(private val k: Int) {
    private var state: Long
    private val cdf: MutableList<Double>

    init {
        this.state = 0
        this.cdf = genRsdCdf(this.k, 0.5, 0.1)
    }

    private fun getNext(): Long {
        this.state = PRNG_A * this.state % PRNG_M
        return this.state
    }

    private fun sampleD(): Int {
        val p = this.getNext().toDouble() / PRNG_MAX_RAND.toDouble()
        for (i in 0 until this.cdf.size) {
            val v = this.cdf[i]
            if (v > p) {
                return i + 1
            }
        }
        return this.cdf.size
    }

    fun setSeed(seed: Long) {
        this.state = seed
    }

    fun getSourceBlocks(seed: Long?): Pair<Long, MutableSet<Int>> {
        if (seed != null) {
            this.state = seed
        }


        val blockseed = this.state

        val d = this.sampleD()

        var have = 0

        val nums = mutableSetOf<Int>()

        while (have < d) {
            val num = this.getNext() % this.k.toLong()

            if (!nums.contains(num.toInt())) {
                nums.add(num.toInt())
                have += 1
            }
        }

        return Pair(blockseed, nums)
    }
}

fun xorByteArray(a: ByteArray, b: ByteArray): ByteArray? {
    if (a.size == b.size) {
        return ByteArray(a.size) { index -> a[index] xor b[index] }
    }
    return null
}


fun split_file(data: ByteArray, blocksize: Int) : MutableList<ByteArray> {
    return data.toList().chunked(blocksize).map {
        it.toString().padStart(blocksize, '\u0000').toByteArray()
    }.toMutableList()
}

fun compressBytes(data: ByteArray) : ByteArray {
    val compressor = Deflater()

    compressor.setInput(data)
    compressor.finish()

    val result = ByteArrayOutputStream()

    val buf = ByteArray(2048)

    while (!compressor.finished()) {
        val count = compressor.deflate(buf)
        result.write(buf, 0, count)
    }

    return result.toByteArray()
}

fun encoder(file: ByteArray, blocksize: Int, extra: Int) : MutableList<ByteArray> {
    val seed = (0 until 1.shl(30) ).random()

    val processed: ByteArray
    val compressed = compressBytes(file)

    var magicByte = 0x00.toByte()

    if (compressed.size < file.size) {
        processed = compressed
        magicByte = 0x01
    }
    else {
        processed = file
    }

    val blocks = split_file(processed, blocksize)

    val filesize = processed.size

    val generate = filesize / blocksize + (filesize / blocksize * .5).toInt() + extra

    val k = blocks.size
    val prng = RobustSolitonDistributionPRNG(k)

    prng.setSeed(seed.toLong())

    val outBlocks = mutableListOf<ByteArray>()

    for (d in 0 until generate) {
        var blockData = ByteArray(blocksize)
        val data = prng.getSourceBlocks(null)
        val blockseed = data.first
        val blockNums = data.second

        for (block in blockNums) {
            blockData = xorByteArray(blockData, blocks[block])!!
        }

        val fullBlock = ByteBuffer.allocate(13 + blocksize)

        fullBlock.put(magicByte)
        fullBlock.putInt(filesize)
        fullBlock.putInt(blocksize)
        fullBlock.putInt(blockseed.toInt())

        fullBlock.put(blockData)

        outBlocks.add(fullBlock.toString().toByteArray())
    }

    return outBlocks.map { Base64.encode(it, Base64.DEFAULT)}.toMutableList()
}

class BlockNode(var src_nodes: MutableSet<Int>, var check: ByteArray)


class BlockGraph(private var k: Int) {
    private val checks: MutableMap<Int, MutableList<BlockNode>> = mutableMapOf()
    val eliminated: MutableMap<Int, ByteArray> = mutableMapOf()

    fun addBlock(in_nodes: MutableSet<Int>, in_data: ByteArray): Pair<Double, Boolean> {
        val nodes = mutableSetOf<Int>()

        for (item in in_nodes) {
            nodes.add(item)
        }

        var data = in_data
        if (nodes.size == 1) {
            val toEliminate = this.eliminate(nodes.sorted()[0], data)

            while (toEliminate.size != 0) {
                val element = toEliminate.removeAt(toEliminate.lastIndex)
                val other = element.first
                val check = element.second
                toEliminate.addAll(this.eliminate(other, check))
            }
        } else {
            val iter = nodes.iterator()
            iter.forEach {
                if (this.eliminated.keys.contains(it)) {
                    iter.remove()
                    data = xorByteArray(data,
                            this.eliminated.getOrDefault(it, ByteArray(0))
                    )!!
                }
            }

            if (nodes.size == 1) {
                return this.addBlock(nodes, data)
            } else {
                val check = BlockNode(nodes, data)
                nodes.forEach {
                    if (this.checks.containsKey(it)) {
                        this.checks[it]!!.add(check)
                    } else {
                        this.checks[it] = mutableListOf()
                        this.checks[it]!!.add(check)
                    }
                }
            }
        }

        return Pair(
                this.eliminated.size.toDouble() / this.k.toDouble(),
                this.eliminated.size >= this.k
        )
    }

    private fun eliminate(node: Int, data: ByteArray): MutableList<Pair<Int, ByteArray>> {
        this.eliminated[node] = data

        var others = this.checks[node]

        if (others.isNullOrEmpty()) {
            others = mutableListOf()
        }

        this.checks.remove(node)

        val additionalBlocks = mutableListOf<Pair<Int, ByteArray>>()

        others.forEach {
            it.check = xorByteArray(it.check, data)!!
            it.src_nodes.remove(node)

            if (it.src_nodes.size == 1) {
                val block = it.src_nodes.sorted()[0]
                val check = it.check

                additionalBlocks.add(Pair(block, check))
            }
        }
        return additionalBlocks
    }
}

class BlockData(val magic_byte: Byte, val filesize: Int, val blocksize: Int,
                val blockseed: Int, val block: ByteArray)

class NotDecodedException(message: String) : Exception(message)

class LTDecoder {
    private var k: Int
    private var filesize: Int
    private var blocksize: Int

    var done: Boolean
    var compressed: Boolean

    private var blockGraph: BlockGraph?
    private var prng: RobustSolitonDistributionPRNG?

    var initialized: Boolean

    init {
        this.k = 0
        this.filesize = 0
        this.blocksize = 0
        this.done = false
        this.compressed = false
        this.blockGraph = null
        this.prng = null
        this.initialized = false
    }


    private fun consumeBlock(lt_block: BlockData): Double {
        val magicByte = lt_block.magic_byte
        val filesize = lt_block.filesize
        val blocksize = lt_block.blocksize
        val blockseed = lt_block.blockseed
        val block = lt_block.block

        Log.v("QR_METADATA", "Filesize: ${filesize}, Blocksize: ${blocksize}")

        if (magicByte and 0x01.toByte() != 0.toByte()) {
            this.compressed = true
        }

        if (!this.initialized) {
            this.filesize = filesize
            this.blocksize = blocksize

            this.k = ceil(filesize.toFloat() / blocksize.toFloat()).toInt()
            this.blockGraph = BlockGraph(this.k)
            this.prng = RobustSolitonDistributionPRNG(this.k)
            this.initialized = true
        }

        val sourceBlocks = this.prng!!.getSourceBlocks(blockseed.toLong())

        val blockResult = this.handleBlock(sourceBlocks.second, block)

        this.done = blockResult.second

        return blockResult.first
    }

    private fun handleBlock(src_blocks: MutableSet<Int>, block: ByteArray): Pair<Double, Boolean> {
        return this.blockGraph!!.addBlock(src_blocks, block)
    }

    fun decodeBytes(block_bytes: ByteArray): Double {
        val magicByte = block_bytes[0]
        val header = block_bytes.slice(IntRange(1, 12))
        val rest = block_bytes.slice(IntRange(13, block_bytes.lastIndex))

        Log.v("QR_MAGIC", "%02x".format(magicByte))

        Log.v("QR_HEADER", header.joinToString("") { java.lang.String.format("%02x", it) } + ", Length: ${header.size}")

        val headerBuffer = ByteBuffer.allocate(12).put(header.toByteArray())

        val blockData = BlockData(
                magicByte,
                headerBuffer.getInt(0),
                headerBuffer.getInt(4),
                headerBuffer.getInt(8),
                rest.toByteArray()
        )

        return this.consumeBlock(blockData)
    }

    fun decodeDump(): ByteArray {
        val rawData = this.streamDump()

        Log.v("QR_RAW", "Raw: ${rawData.toList().joinToString(" ") {"%02x".format(it)} }")

        val decoded = if (this.compressed) {
            val decompresser = Inflater()
            decompresser.setInput(rawData)

            val result = ByteArrayOutputStream()

            while (!decompresser.finished() || decompresser.needsInput()) {
                val buf = ByteArray(2048)
                val count = decompresser.inflate(buf)
                result.write(buf, 0, count)
            }

            result.toByteArray()
        } else {
            rawData
        }

        return decoded
    }

    private fun streamDump(): ByteArray {
        val out = ByteArray(0).toMutableList()

        if (this.blockGraph!!.eliminated.size != this.k) {
            throw NotDecodedException("The decoded has not completed decoding the blocks!")
        }

        val eliminatedBlocks = this.blockGraph!!.eliminated
                .entries.stream().map { e -> Pair(e.key, e.value) }
                .collect(Collectors.toList())
                .sortedBy { a -> a.first }

        eliminatedBlocks.forEachIndexed { index, pair ->
            if ((pair.first < this.k - 1).or(this.filesize % this.blocksize == 0)) {
                out.addAll(pair.second.toList())
            } else {
                out.addAll(pair.second.slice(IntRange(0, (this.filesize % this.blocksize) - 1)))
            }
        }

        return out.toByteArray()
    }
}