package com.tgyuu.deviceinfo

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceInfoProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : DeviceInfoProvider {

    override suspend fun getDeviceName(): String {
        val cached = context.dataStore.data
            .map { it[MARKET_NAME] }
            .first()

        if (cached != null) return cached

        val fetched = fetchFromNetwork()
        return if (fetched != null) {
            context.dataStore.edit { prefs ->
                prefs[MANUFACTURER] = fetched.manufacturer
                prefs[MARKET_NAME] = fetched.marketName
                prefs[CODENAME] = fetched.codename
                prefs[MODEL] = fetched.model
            }
            fetched.marketName
        } else {
            Build.MODEL
        }
    }

    private suspend fun fetchFromNetwork(): DeviceInfo? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(CSV_URL).openConnection() as HttpURLConnection).apply {
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
            }

            val storedETag = context.dataStore.data
                .map { it[ETAG] }
                .first()

            if (storedETag != null) {
                connection.setRequestProperty("If-None-Match", storedETag)
            }

            when (connection.responseCode) {
                HttpURLConnection.HTTP_NOT_MODIFIED -> null
                HttpURLConnection.HTTP_OK -> {
                    val newETag = connection.getHeaderField("etag")
                    val device = parseCsv(connection)
                    if (newETag != null) {
                        context.dataStore.edit { it[ETAG] = newETag }
                    }
                    device
                }
                else -> null
            }
        } catch (_: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }

    private fun parseCsv(connection: HttpURLConnection): DeviceInfo? {
        BufferedReader(InputStreamReader(connection.inputStream, "UTF-16")).use { reader ->
            for (line in reader.lineSequence()) {
                val data = line.split(",").dropLastWhile(String::isEmpty)
                if (data.size == 4 && data[2] == Build.DEVICE) {
                    return DeviceInfo(
                        manufacturer = data[0],
                        marketName = data[1],
                        codename = data[2],
                        model = data[3],
                    )
                }
            }
        }
        return null
    }

    private data class DeviceInfo(
        val manufacturer: String,
        val marketName: String,
        val codename: String,
        val model: String,
    )

    private companion object {
        private const val CSV_URL =
            "https://storage.googleapis.com/play_public/supported_devices.csv"
        private const val TIMEOUT_MS = 5_000

        private val Context.dataStore: DataStore<Preferences>
                by preferencesDataStore(name = "device_info_prefs")

        private val MANUFACTURER = stringPreferencesKey("device_manufacturer")
        private val MARKET_NAME = stringPreferencesKey("device_market_name")
        private val CODENAME = stringPreferencesKey("device_codename")
        private val MODEL = stringPreferencesKey("device_model")
        private val ETAG = stringPreferencesKey("device_csv_etag")
    }
}
