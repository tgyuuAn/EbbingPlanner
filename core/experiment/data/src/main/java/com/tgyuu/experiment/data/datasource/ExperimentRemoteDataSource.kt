package com.tgyuu.experiment.data.datasource

interface ExperimentRemoteDataSource {
    suspend fun fetchAllExperimentVariants(experimentKeys: List<String>): Map<String, String>
    suspend fun getExperimentVariant(experimentKey: String): String?
}
