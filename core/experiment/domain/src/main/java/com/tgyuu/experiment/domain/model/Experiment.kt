package com.tgyuu.experiment.domain.model

sealed class Experiment<V>(
    val key: String,
    val variants: Array<V>,
    val defaultVariant: V,
) where V : Enum<V>, V : ExperimentVariant {

    fun parseVariant(value: String): V =
        variants.find { it.key.equals(value, ignoreCase = true) } ?: defaultVariant

    companion object {
        val ALL: List<Experiment<*>> = emptyList()
    }

    object ExperimentKey {
        const val CONTROL_KEY = "CONTROL"
        const val TREATMENT_KEY = "TREATMENT"
    }
}
