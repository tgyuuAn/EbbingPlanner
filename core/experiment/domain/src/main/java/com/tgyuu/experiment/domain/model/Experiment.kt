package com.tgyuu.experiment.domain.model

import com.tgyuu.experiment.domain.model.Experiment.ExperimentKey.CONTROL_KEY
import com.tgyuu.experiment.domain.model.Experiment.ExperimentKey.TREATMENT_KEY

sealed class Experiment<V>(
    val key: String,
    val variants: Array<V>,
    val defaultVariant: V,
) where V : Enum<V>, V : ExperimentVariant {

    fun parseVariant(value: String): V =
        variants.find { it.key.equals(value, ignoreCase = true) } ?: defaultVariant

    data object SaveButtonPosition : Experiment<SaveButtonPosition.Variant>(
        key = "experiment_save_button_position",
        variants = Variant.entries.toTypedArray(),
        defaultVariant = Variant.CONTROL,
    ) {
        enum class Variant(override val key: String) : ExperimentVariant {
            CONTROL(CONTROL_KEY),
            TREATMENT(TREATMENT_KEY),
        }
    }

    companion object {
        val ALL: List<Experiment<*>> = listOf(
            SaveButtonPosition,
        )
    }

    object ExperimentKey {
        const val CONTROL_KEY = "CONTROL"
        const val TREATMENT_KEY = "TREATMENT"
    }
}
