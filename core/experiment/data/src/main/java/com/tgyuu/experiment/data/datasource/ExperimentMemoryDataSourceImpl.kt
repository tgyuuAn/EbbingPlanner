package com.tgyuu.experiment.data.datasource

import java.util.concurrent.ConcurrentHashMap

class ExperimentMemoryDataSourceImpl() : ExperimentMemoryDataSource {
    private val cache = ConcurrentHashMap<String, String>()

    override fun getAssignment(experimentKey: String): String? = cache[experimentKey]

    override fun saveAssignment(experimentKey: String, variantName: String) {
        cache[experimentKey] = variantName
    }
}
