package dev.novalogic.txqrfincrypt

import kotlin.math.*

fun gen_tau(s: Double, k: Int, delta: Double) : MutableList<Double> {

    val pivot = floor(k / s).toInt()
    val distribution = mutableListOf<Double>()

    for (d in 1 until pivot) {
        distribution.add(s / k.toDouble() * 1 / d.toDouble())
    }

    distribution.add(s / k * log(s / delta, E))

    for (d in pivot until k) {
        distribution.add(0.0)
    }

    return distribution
}

fun gen_rho(k: Int) : MutableList<Double> {
    val distribution = mutableListOf<Double>()

    distribution.add((1 / k).toDouble())

    for (d in 2 until k + 1) {
        distribution.add(1 / (d.toDouble() * (d.toDouble() - 1)))
    }

    return distribution
}

fun gen_mu(k: Int, delta: Double, c: Double) : MutableList<Double> {
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