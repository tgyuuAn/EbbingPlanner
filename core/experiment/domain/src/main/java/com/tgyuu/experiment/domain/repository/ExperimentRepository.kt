package com.tgyuu.experiment.domain.repository

import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.model.ExperimentVariant

interface ExperimentRepository {
    suspend fun fetchAndAssignExperiments()

    suspend fun <V> getVariant(experiment: Experiment<V>): V where V : Enum<V>, V : ExperimentVariant
}
