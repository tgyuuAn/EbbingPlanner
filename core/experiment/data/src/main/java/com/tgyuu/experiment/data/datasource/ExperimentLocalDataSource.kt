package com.tgyuu.experiment.data.datasource

interface ExperimentLocalDataSource {
    suspend fun getAssignment(experimentKey: String): String?
    suspend fun saveAssignmentIfNotExists(experimentKey: String, variantName: String)
}
