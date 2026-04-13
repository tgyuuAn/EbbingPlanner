package com.tgyuu.experiment.data.datasource

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExperimentMemoryDataSourceImpl @Inject constructor() : ExperimentMemoryDataSource {
    private val cache = ConcurrentHashMap<String, String>()

    override fun getAssignment(experimentKey: String): String? = cache[experimentKey]

    override fun saveAssignment(experimentKey: String, variantName: String) {
        cache[experimentKey] = variantName
    }
}
