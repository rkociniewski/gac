package pl.rk.gac.di

import android.content.Context
import kotlinx.serialization.json.Json
import pl.rk.gac.R
import pl.rk.gac.model.Pericope
import pl.rk.gac.model.Settings
import pl.rk.gac.util.SettingsStore
import java.io.InputStream

class SettingsRepository(private val context: Context) {
    val settingsFlow = SettingsStore.read(context)
    suspend fun updateSettings(settings: Settings) = SettingsStore.write(context, settings)

    fun loadPericopesFromRaw(): List<Pericope> {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.pl_gospel)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return Json.decodeFromString<List<Pericope>>(jsonString)
    }

}
