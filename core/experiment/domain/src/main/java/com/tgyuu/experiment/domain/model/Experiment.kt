package com.tgyuu.experiment.domain.model

sealed class Experiment<V>(
    val key: String,
    val defaultVariant: V,
    val variants: Array<V>,
) where V : Enum<V>, V : ExperimentVariant {

    fun parseVariant(value: String): V = variants.find { it.key == value } ?: defaultVariant

    data object NotificationNudgeText : Experiment<NotificationNudgeText.Variant>(
        key = "experiment_notification_nudge_text",
        defaultVariant = Variant.CONTROL,
        variants = Variant.entries.toTypedArray(),
    ) {
        enum class Variant(override val key: String) : ExperimentVariant {
            CONTROL("control"),
            TREATMENT("treatment"),
        }
    }

    companion object {
        val ALL: List<Experiment<*>> = listOf(
            NotificationNudgeText,
        )
    }
}
