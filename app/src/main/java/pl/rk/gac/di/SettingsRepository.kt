package pl.rk.gac.di

import android.content.Context
import kotlinx.serialization.json.Json
import pl.rk.gac.enums.Language
import pl.rk.gac.model.Pericope
import pl.rk.gac.model.Settings
import pl.rk.gac.util.SettingsStore
import java.io.InputStream

class SettingsRepository(private val context: Context) {
    val settingsFlow = SettingsStore.read(context)
    suspend fun updateSettings(settings: Settings) = SettingsStore.write(context, settings)

    fun loadPericopesFromRaw(language: Language): List<Pericope> {
        val inputStream: InputStream = context.resources.openRawResource(language.resource)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return Json.decodeFromString<List<Pericope>>(jsonString)
    }
}
