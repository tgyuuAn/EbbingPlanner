package com.tgyuu.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class LocalUserConfigDataSourceImplTest {
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataSource: LocalUserConfigDataSourceImpl

    @Before
    fun setUp() {
        val testScope = CoroutineScope(Dispatchers.IO + Job())
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { File.createTempFile("test", ".preferences_pb") }
        )
        dataSource = LocalUserConfigDataSourceImpl(dataStore)
    }

    @Test
    fun UUID는_디바이스에_저장된_UUID가_없을_경우에만_생성한다() = runTest {
        // when
        dataSource.ensureUuidExists()

        // then
        val actual = dataStore.data.first()[LocalUserConfigDataSourceImpl.UUID]

        assertTrue(actual!!.matches(Regex("^[a-f0-9\\-]{36}$")))
    }

    @Test
    fun 디바이스에_저장된_UUID가_있을_경우_UUID를_생성하지_않는다() = runTest {
        // given
        val expected = "predefined-uuid-1234"
        dataStore.edit { prefs -> prefs[LocalUserConfigDataSourceImpl.UUID] = expected }

        // when
        dataSource.ensureUuidExists()

        // then
        val actual = dataStore.data.first()[LocalUserConfigDataSourceImpl.UUID]
        assertEquals(expected, actual)
    }
}
