package com.tgyuu.experiment.data.initializer

import com.tgyuu.common.initializer.Initializer
import com.tgyuu.common.initializer.Initializer.Companion.PRIORITY_LOW
import com.tgyuu.experiment.domain.repository.ExperimentRepository
import javax.inject.Inject

class ExperimentInitializer @Inject constructor(
    private val experimentRepository: ExperimentRepository,
) : Initializer {
    override val priority: Int
        get() = PRIORITY_LOW

    override suspend fun initialize() {
        experimentRepository.fetchAndAssignExperiments()
    }
}
