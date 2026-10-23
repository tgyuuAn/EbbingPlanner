package com.tgyuu.shared.data.repository

import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.model.ExperimentVariant
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.platform.Settings

class ExperimentRepositoryImpl(
    private val settings: Settings,
) : ExperimentRepository {

    // In-memory cache
    private val memoryCache = mutableMapOf<String, String>()

    override suspend fun fetchAndAssignExperiments() {
        // TODO: Fetch from Firebase Remote Config when KMP Firebase SDK is available
        // For now, persist existing assignments (defaults to CONTROL for new users)
        Experiment.ALL.forEach { experiment ->
            if (!memoryCache.containsKey(experiment.key)) {
                val local = settings.getString(experimentKey(experiment.key), "")
                if (local.isNotEmpty()) {
                    memoryCache[experiment.key] = local
                }
            }
        }
    }

    override suspend fun <V> getVariant(experiment: Experiment<V>): V where V : Enum<V>, V : ExperimentVariant {
        // 1. Memory cache
        memoryCache[experiment.key]?.let { return experiment.parseVariant(it) }

        // 2. Local storage
        val local = settings.getString(experimentKey(experiment.key), "")
        if (local.isNotEmpty()) {
            memoryCache[experiment.key] = local
            return experiment.parseVariant(local)
        }

        // 3. Default (CONTROL)
        val defaultKey = experiment.defaultVariant.key
        settings.putString(experimentKey(experiment.key), defaultKey)
        memoryCache[experiment.key] = defaultKey
        return experiment.defaultVariant
    }

    private fun experimentKey(key: String) = "experiment_$key"
}
