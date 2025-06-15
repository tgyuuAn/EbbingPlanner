package com.tgyuu.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tgyuu.datastore.datasource.sync.LocalSyncDataSource
import com.tgyuu.datastore.datasource.sync.LocalSyncDataSourceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class LocalSyncConfigDataSourceTest {
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataSource: LocalSyncDataSource
    private lateinit var tempFile: File

    @Before
    fun setUp() {
        val testScope = CoroutineScope(Dispatchers.IO + Job())
        tempFile = File.createTempFile("test", ".preferences_pb")

        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tempFile }
        )
        dataSource = LocalSyncDataSourceImpl(dataStore)
    }

    @After
    fun tearDown() {
        // 테스트 이후 dataStore와 생성된 테스트용 파일 제거해줘야 함.
        runBlocking { dataStore.edit { it.clear() } }
        tempFile.delete()
    }

    @Test
    fun UUID는_디바이스에_저장된_UUID가_없을_경우에만_생성한다() = runTest {
        // when
        dataSource.ensureUUIDExists()

        // then
        val actual = dataStore.data.first()[UUID]

        assertTrue(actual!!.matches(Regex("^[a-f0-9\\-]{36}$")))
    }

    @Test
    fun 디바이스에_저장된_UUID가_있을_경우_UUID를_생성하지_않는다() = runTest {
        // given
        val expected = "이미 생성된 UUID"
        dataStore.edit { prefs -> prefs[UUID] = expected }

        // when
        dataSource.ensureUUIDExists()

        // then
        val actual = dataStore.data.first()[UUID]
        assertEquals(expected, actual)
    }

    internal companion object {
        val UUID = stringPreferencesKey("UUID")
    }
}
