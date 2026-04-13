package com.tgyuu.experiment.data.datasource

interface ExperimentMemoryDataSource {
    fun getAssignment(experimentKey: String): String?
    fun saveAssignment(experimentKey: String, variantName: String)
}
