package com.tgyuu.experiment.data.repository

import com.tgyuu.experiment.data.datasource.ExperimentLocalDataSource
import com.tgyuu.experiment.data.datasource.ExperimentRemoteDataSource
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.model.ExperimentVariant
import com.tgyuu.experiment.domain.repository.ExperimentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExperimentRepositoryImpl @Inject constructor(
    private val remoteDataSource: ExperimentRemoteDataSource,
    private val localDataSource: ExperimentLocalDataSource,
) : ExperimentRepository {

    override suspend fun fetchAndAssignExperiments() {
        val experimentKeys = Experiment.ALL.map { it.key }
        if (experimentKeys.isEmpty()) return

        val remoteVariants = remoteDataSource.fetchAllExperimentVariants(experimentKeys)

        Experiment.ALL.forEach { experiment ->
            val remoteVariant = remoteVariants[experiment.key]
            val variantToAssign = remoteVariant ?: experiment.defaultVariant.key

            localDataSource.saveAssignmentIfNotExists(
                experimentKey = experiment.key,
                variantName = variantToAssign,
            )
        }
    }

    override suspend fun <V> getVariant(experiment: Experiment<V>): V where V : Enum<V>, V : ExperimentVariant {
        val variantName = localDataSource.getAssignment(experiment.key)
            ?: experiment.defaultVariant.key
        return experiment.parseVariant(variantName)
    }
}
