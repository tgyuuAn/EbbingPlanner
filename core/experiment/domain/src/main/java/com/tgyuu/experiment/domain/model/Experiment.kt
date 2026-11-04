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

    /**
     * 일정 추가 화면의 다음 진도 추천 칩.
     * - CONTROL: 진도 없이 과목명만 추천해요. (TOEIC)
     * - TREATMENT: 다음 진도 시작점까지 추천해요. (TOEIC 101-)
     */
    data object NextProgressSuggestion : Experiment<NextProgressSuggestion.Variant>(
        key = "experiment_next_progress_suggestion",
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
            NextProgressSuggestion,
        )
    }

    object ExperimentKey {
        const val CONTROL_KEY = "CONTROL"
        const val TREATMENT_KEY = "TREATMENT"
    }
}
