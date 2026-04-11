package com.tgyuu.experiment.domain.model

sealed class Experiment<V>(
    val key: String,
    val variants: Array<V>,
    val defaultVariant: V,
) where V : Enum<V>, V : ExperimentVariant {

    fun parseVariant(value: String): V = variants.find { it.key == value } ?: defaultVariant

    data object NotificationNudgeText : Experiment<NotificationNudgeText.Variant>(
        key = "experiment_notification_nudge_text",
        variants = Variant.entries.toTypedArray(),
        defaultVariant = Variant.CONTROL,
    ) {
        enum class Variant(override val key: String) : ExperimentVariant {
            CONTROL("control"),
            TREATMENT("treatment"),
        }
    }

    companion object {
        val ALL: List<Experiment<*>> = emptyList()
    }
}
