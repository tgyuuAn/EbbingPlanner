package com.tgyuu.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tgyuu.domain.model.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class LocalUserConfigDataSourceImpl @Inject constructor(
    @Named("config") private val dataStore: DataStore<Preferences>,
) : LocalUserConfigDataSource {
    override val sortType: Flow<SortType>
        get() = dataStore.data
            .map { prefs ->
                val name = prefs[SORT_TYPE] ?: SortType.CREATED.name
                SortType.create(name)
            }

    override suspend fun setSortType(sortType: SortType) {
        dataStore.edit { prefs -> prefs[SORT_TYPE] = sortType.name }
    }

    companion object {
        private val SORT_TYPE = stringPreferencesKey("SORT_TYPE")
    }
}
