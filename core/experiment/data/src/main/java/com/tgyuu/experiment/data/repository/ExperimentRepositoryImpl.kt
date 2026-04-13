package com.tgyuu.experiment.data.repository

import com.tgyuu.experiment.data.datasource.ExperimentLocalDataSource
import com.tgyuu.experiment.data.datasource.ExperimentMemoryDataSource
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
    private val memoryDataSource: ExperimentMemoryDataSource,
) : ExperimentRepository {

    override suspend fun fetchAndAssignExperiments() {
        val experimentKeys = Experiment.ALL.map { it.key }
        if (experimentKeys.isEmpty()) return

        val remoteVariants = remoteDataSource.fetchAllExperimentVariants(experimentKeys)

        Experiment.ALL.forEach { experiment ->
            val remoteVariant = remoteVariants[experiment.key]
            val variantToAssign = remoteVariant ?: experiment.defaultVariant.key

            // 로컬에 없으면 로컬 저장
            localDataSource.saveAssignmentIfNotExists(
                experimentKey = experiment.key,
                variantName = variantToAssign,
            )

            // 로컬에 있는 데이터를 메모리로 캐싱해둠
            val actualVariant = localDataSource.getAssignment(experiment.key)
                ?: experiment.defaultVariant.key
            memoryDataSource.saveAssignment(experiment.key, actualVariant)
        }
    }

    override suspend fun <V> getVariant(experiment: Experiment<V>): V where V : Enum<V>, V : ExperimentVariant {
        // 1. 메모리 먼저 체크
        val memoryVariant = memoryDataSource.getAssignment(experiment.key)
        if (memoryVariant != null) {
            return experiment.parseVariant(memoryVariant)
        }

        // 2. 디스크 체크
        var variantName = localDataSource.getAssignment(experiment.key)

        // 3. 그럼에도 없으면 리모트에서 가져옴
        if (variantName == null) {
            val remoteVariants = remoteDataSource.fetchAllExperimentVariants(listOf(experiment.key))
            val remoteVariant = remoteVariants[experiment.key]
            variantName = remoteVariant ?: experiment.defaultVariant.key

            // 가져온 데이터 로컬 저장
            localDataSource.saveAssignmentIfNotExists(
                experimentKey = experiment.key,
                variantName = variantName,
            )
        }

        // 4. 가져온 데이터 메모리 저장
        memoryDataSource.saveAssignment(experiment.key, variantName)

        return experiment.parseVariant(variantName)
    }
}
