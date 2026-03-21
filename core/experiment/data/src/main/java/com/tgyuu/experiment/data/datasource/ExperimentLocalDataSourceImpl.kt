package com.tgyuu.experiment.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class ExperimentLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : ExperimentLocalDataSource {

    override suspend fun getAssignment(experimentKey: String): String? {
        val key = stringPreferencesKey("$EXPERIMENT_PREFIX$experimentKey")
        return dataStore.data.first()[key]
    }

    override suspend fun saveAssignmentIfNotExists(
        experimentKey: String,
        variantName: String,
    ) {
        val key = stringPreferencesKey("$EXPERIMENT_PREFIX$experimentKey")
        dataStore.edit { prefs ->
            if (prefs[key] == null) {
                prefs[key] = variantName
            }
        }
    }

    companion object {
        private const val EXPERIMENT_PREFIX = "exp_"
    }
}
