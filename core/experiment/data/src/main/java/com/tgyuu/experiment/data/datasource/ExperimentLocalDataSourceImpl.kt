package com.tgyuu.experiment.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tgyuu.experiment.data.di.Experiment
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExperimentLocalDataSourceImpl @Inject constructor(
    @Experiment private val dataStore: DataStore<Preferences>,
) : ExperimentLocalDataSource {

    override suspend fun getAssignment(experimentKey: String): String? {
        val key = stringPreferencesKey("$EXPERIMENT_PREFIX$experimentKey")
        return dataStore.data.first()[key]
    }

    override suspend fun saveAssignment(
        experimentKey: String,
        variantName: String,
    ) {
        val key = stringPreferencesKey("$EXPERIMENT_PREFIX$experimentKey")
        dataStore.edit { prefs ->
            prefs[key] = variantName
        }
    }

    companion object {
        private const val EXPERIMENT_PREFIX = "exp_"
    }
}
