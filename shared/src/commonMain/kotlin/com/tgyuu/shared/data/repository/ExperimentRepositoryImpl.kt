package com.tgyuu.shared.data.repository

import com.tgyuu.shared.data.source.DefaultRemoteConfigDataSource
import com.tgyuu.shared.data.source.RemoteConfigDataSource
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.model.ExperimentVariant
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.platform.Settings

/**
 * Android ExperimentRepositoryImpl 대응: 메모리 → 로컬 → Remote Config → 기본값(CONTROL) 순.
 * Remote Config 키는 Android ExperimentRemoteDataSourceImpl 과 같은 "{experimentKey}_{buildType}" 형식이다.
 *
 * @param buildType "debug" / "release" (Android BuildConfig.BUILD_TYPE 대응, iOS 는 Swift 가 넘긴다)
 */
class ExperimentRepositoryImpl(
    private val settings: Settings,
    private val remoteConfigDataSource: RemoteConfigDataSource = DefaultRemoteConfigDataSource(),
    private val buildType: String = "release",
) : ExperimentRepository {

    // In-memory cache
    private val memoryCache = mutableMapOf<String, String>()

    override suspend fun fetchAndAssignExperiments() {
        if (Experiment.ALL.isEmpty()) return

        val remoteVariants = fetchRemoteVariants(Experiment.ALL.map { it.key })

        Experiment.ALL.forEach { experiment ->
            val variantToAssign = remoteVariants[experiment.key] ?: experiment.defaultVariant.key
            settings.putString(experimentKey(experiment.key), variantToAssign)
            memoryCache[experiment.key] = variantToAssign
        }
    }

    override suspend fun <V> getVariant(experiment: Experiment<V>): V where V : Enum<V>, V : ExperimentVariant {
        // 1. Memory cache
        memoryCache[experiment.key]?.let { return experiment.parseVariant(it) }

        // 2. Local storage
        var variantName = settings.getString(experimentKey(experiment.key), "").ifEmpty { null }

        // 3. Remote → 4. Default (CONTROL)
        if (variantName == null) {
            variantName = fetchRemoteVariants(listOf(experiment.key))[experiment.key]
                ?: experiment.defaultVariant.key
            settings.putString(experimentKey(experiment.key), variantName)
        }

        memoryCache[experiment.key] = variantName
        return experiment.parseVariant(variantName)
    }

    private suspend fun fetchRemoteVariants(experimentKeys: List<String>): Map<String, String> {
        runCatching { remoteConfigDataSource.fetchAndActivate() }
        return experimentKeys.mapNotNull { key ->
            remoteConfigDataSource.getString("${key}_$buildType")?.let { key to it }
        }.toMap()
    }

    private fun experimentKey(key: String) = "experiment_$key"
}
