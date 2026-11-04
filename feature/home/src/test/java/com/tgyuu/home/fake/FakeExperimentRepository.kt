package com.tgyuu.home.fake

import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.model.ExperimentVariant
import com.tgyuu.experiment.domain.repository.ExperimentRepository

class FakeExperimentRepository(
    private val variants: Map<String, String> = emptyMap(),
) : ExperimentRepository {
    override suspend fun fetchAndAssignExperiments() = Unit

    override suspend fun <V> getVariant(experiment: Experiment<V>): V where V : Enum<V>, V : ExperimentVariant =
        variants[experiment.key]?.let(experiment::parseVariant) ?: experiment.defaultVariant
}
