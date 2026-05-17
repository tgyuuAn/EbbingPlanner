package com.tgyuu.experiment.data.datasource

interface ExperimentLocalDataSource {
    suspend fun getAssignment(experimentKey: String): String?
    suspend fun saveAssignment(experimentKey: String, variantName: String)
}
