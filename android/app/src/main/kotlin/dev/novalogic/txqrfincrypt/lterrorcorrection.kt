package dev.novalogic.txqrfincrypt

import java.lang.Exception
import java.nio.ByteBuffer
import java.util.stream.Collectors
import java.util.zip.Inflater
import kotlin.math.*
import kotlin.experimental.xor
import kotlin.experimental.and

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
        distribution.add(mu.slice(IntRange(0, d + 1)).sum())
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
        val p = this.getNext() / PRNG_MAX_RAND
        for (i in 0 until this.cdf.size) {
            val v = this.cdf[i]
            if (v.toBigDecimal() > p.toBigDecimal()) {
                return i + 1
            }
        }
        return this.cdf.size
    }

    fun getSourceBlocks(seed: Long?): Pair<Long, MutableSet<Int>> {
        val blockseed = this.state

        if (seed != null) {
            this.state = seed
        }

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
        a.mapIndexed { index, byte ->
            return ByteArray(byte.xor(b[index]).toInt())
        }
    }
    return null
}

class BlockNode(var src_nodes: MutableSet<Int>, var check: ByteArray)


class BlockGraph(private var k: Int) {
    private val checks: MutableMap<Int, MutableList<BlockNode>> = mutableMapOf()
    val eliminated: MutableMap<Int, ByteArray> = mutableMapOf()

    fun addBlock(nodes: MutableSet<Int>, in_data: ByteArray): Pair<Double, Boolean> {
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
            nodes.toList().forEach {
                if (this.eliminated.keys.contains(it)) {
                    nodes.remove(it)
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
                    this.checks[it]?.add(check)
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

class NotDecodedException(message : String) : Exception(message)

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
        val header = block_bytes.slice(IntRange(1, 13))
        val rest = block_bytes.slice(IntRange(0, block_bytes.lastIndex))

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

        return if (this.compressed) {
            val decompresser = Inflater()
            decompresser.setInput(rawData)

            val outBuffer = ByteArray(decompresser.totalOut)
            decompresser.inflate(outBuffer)

            outBuffer
        } else {
            rawData
        }
    }

    private fun streamDump(): ByteArray {
        val out = ByteArray(this.filesize).toMutableList()

        if (this.blockGraph!!.eliminated.size != this.k) {
            throw NotDecodedException("The decoded has not completed decoding the blocks!")
        }

        val eliminatedBlocks = this.blockGraph!!.eliminated
                .entries.stream().map { e -> Pair(e.key, e.value) }
                .collect(Collectors.toList())
                .sortedBy { a -> a.first }

        eliminatedBlocks.forEachIndexed { index, pair ->
            if ((index < this.k).or(this.filesize % this.blocksize == 0)) {
                out.addAll(pair.second.toList())
            } else {
                out.addAll(pair.second.slice(IntRange(0, this.filesize % this.blocksize)))
            }
        }

        return out.toByteArray()
    }
}