package dev.novalogic.txqrfincrypt

import kotlin.math.*
import java.math.BigInteger
import kotlin.experimental.xor
import kotlin.experimental.and

fun gen_tau(s: Double, k: Int, delta: Double): MutableList<Double> {

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

fun gen_rho(k: Int): MutableList<Double> {
    val distribution = mutableListOf<Double>()

    distribution.add(1 / k.toDouble())

    for (d in 2 until k + 1) {
        distribution.add(1 / (d.toDouble() * (d.toDouble() - 1)))
    }

    return distribution
}

fun gen_mu(k: Int, delta: Double, c: Double): MutableList<Double> {
    val S = c * log(k.toDouble() / delta, E) * sqrt(k.toDouble())

    val tau = gen_tau(S, k, delta)
    val rho = gen_rho(k)
    val normalizer = rho.sum() + tau.sum()

    val distribution = mutableListOf<Double>()

    for (d in 0 until k) {
        distribution.add((rho[d] + tau[d]) / normalizer)
    }

    return distribution
}

fun gen_rsd_cdf(k: Int, delta: Double, c: Double): MutableList<Double> {
    val mu = gen_mu(k, delta, c)
    val distribution = mutableListOf<Double>()

    for (d in 0 until k) {
        distribution.add(mu.slice(IntRange(0, d + 1)).sum())
    }

    return distribution
}

val PRNG_A = 16807
val PRNG_M = (BigInteger("1") shl 31) - BigInteger("1")
val PRNG_MAX_RAND = PRNG_M - BigInteger("1")

class RSD_PRNG {
    private val k: Int
    private var state: BigInteger
    private val cdf: MutableList<Double>

    constructor(k: Int) {
        this.k = k
        this.state = BigInteger("0")
        this.cdf = gen_rsd_cdf(this.k, 0.5, 0.1)
    }

    private fun get_next(): BigInteger {
        this.state = PRNG_A.toBigInteger() * this.state % PRNG_M
        return this.state
    }

    private fun sample_d(): Int {
        val p = this.get_next() / PRNG_MAX_RAND
        for (i in 0 until this.cdf.size) {
            val v = this.cdf[i]
            if (v.toBigDecimal() > p.toBigDecimal()) {
                return i + 1
            }
        }
        return this.cdf.size
    }

    fun set_seed(seed: BigInteger) {
        this.state = seed
    }

    fun get_src_blocks(seed: BigInteger?): Map<String, Any> {
        val blockseed = this.state

        if (seed != null) {
            this.state = seed
        }

        val d = this.sample_d()

        var have = 0

        val nums = mutableSetOf<Int>()

        while (have < d) {
            val num = this.get_next() % this.k.toBigInteger()

            if (!nums.contains(num.toInt())) {
                nums.add(num.toInt())
                have += 1
            }
        }
        return mapOf("blockseed" to blockseed, "d" to d, "nums" to nums)
    }
}

fun xor_byte_array(a: ByteArray, b: ByteArray): ByteArray? {
    if (a.size == b.size) {
        a.mapIndexed { index, byte ->
            return ByteArray(byte.xor(b[index]).toInt())
        }
    }
    return null
}

class BlockNode(var src_nodes: MutableSet<Int>, var check: ByteArray)

class BlockGraph(var num_blocks: Int){
    val checks: MutableMap<Int, MutableList<BlockNode>> = mutableMapOf()
    val eliminated: MutableMap<Int, ByteArray>

    init {
        this.eliminated = mutableMapOf()
    }

    fun add_block(nodes: MutableSet<Int>, in_data: ByteArray): Pair<Double, Boolean> {
        var data = in_data
        if (nodes.size == 1) {
            val to_eliminate = this.eliminate(nodes.sorted()[0], data)

            while (to_eliminate.size != 0) {
                val element = to_eliminate.removeAt(to_eliminate.lastIndex)
                val other = element.first
                val check = element.second
                to_eliminate.addAll(this.eliminate(other, check))
            }
        } else {
            nodes.toList().forEach {
                if (this.eliminated.keys.contains(it)) {
                    nodes.remove(it)
                    data = xor_byte_array(data,
                            this.eliminated.getOrDefault(it, ByteArray(0))
                    )!!
                }
            }

            if (nodes.size == 1) {
                return this.add_block(nodes, data)
            } else:
            val check = BlockNode(nodes, data)
            nodes.forEach {
                this.checks[it]?.add(check)
            }
        }

        return Pair<Double, Boolean>(
                this.eliminated.size.toDouble() / this.num_blocks.toDouble(),
                this.eliminated.size >= this.num_blocks
            )
    }

    fun eliminate(node: Int, data: ByteArray): MutableList<Pair<Int, ByteArray>> {
        this.eliminated.put(node, data)

        var others = this.checks[node]

        if (others.isNullOrEmpty()) {
            others = mutableListOf<BlockNode>()
        }

        this.checks.remove(node)

        val extra_blocks = mutableListOf<Pair<Int, ByteArray>>()

        others.forEach {
            it.check = xor_byte_array(it.check, data)!!
            it.src_nodes.remove(node)

            if (it.src_nodes.size == 1) {
                val block = it.src_nodes.sorted()[0]
                val check = it.check

                extra_blocks.add(Pair<Int, ByteArray>(block, check))
            }
        }
        return extra_blocks
    }
}

class LTDecoder() {
    var k: Int
    var filesize: Int
    var blocksize: Int
    var done: Boolean
    var compressed: Boolean

    var block_graph: BlockGraph?
    var prng: RSD_PRNG?

    var initialized: Boolean

    init {
        this.k = 0
        this.filesize = 0
        this.blocksize = 0
        this.done = false
        this.compressed = false
        this.block_graph = null
        this.block_graph = null
        this.prng = null
        this.initialized = false
    }

    fun is_done(): Boolean {
        return this.done
    }

    fun consume_block(lt_block: Map<String, Any>) : Double {
        val magic_byte = lt_block["magic_byte"] as Byte
        val filesize = lt_block["filesize"] as Int
        val blocksize = lt_block["blocksize"] as Int
        val blockseed = lt_block["blockseed"] as Int
        val block = lt_block["block"] as ByteArray

        if (magic_byte and 0x01 != 0.toByte()) {
            this.compressed = true
        }

        if (!this.initialized) {
            this.filesize = filesize
            this.blocksize = blocksize

            this.k = ceil(filesize.toFloat() / blocksize.toFloat()).toInt()
            this.block_graph = BlockGraph(this.k)
            this.prng = RSD_PRNG(this.k)
            this.initialized = true
        }

        val src_blocks = this.prng!!.get_src_blocks(blockseed.toBigInteger())

        block_result = this.handle_block((src_blocks as MutableMap<String, Any>)["nums"] as MutableSet<Int>, block)

        return block_result.first
    }

    fun handle_block(src_blocks : MutableSet<Int>, block : ByteArray) : Pair<Double, Boolean> {
        return this.block_graph.add_block(src_blocks, block)
    }
}