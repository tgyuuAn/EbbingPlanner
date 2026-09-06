package com.tgyuu.shared.domain.repository

import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.model.ExperimentVariant

interface ExperimentRepository {
    suspend fun fetchAndAssignExperiments()

    suspend fun <V> getVariant(experiment: Experiment<V>): V where V : Enum<V>, V : ExperimentVariant
}
