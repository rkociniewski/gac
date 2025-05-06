package rk.gac.data

import android.content.Context
import kotlinx.serialization.json.Json
import rk.gac.model.Config
import rk.gac.R
import rk.gac.enums.AdditionalMode
import rk.gac.model.Pericope
import java.io.InputStream

class PericopeProvider(private val context: Context) {

    private val allPericopes: List<Pericope> by lazy {
        loadPericopesFromRaw()
    }

    private fun loadPericopesFromRaw(): List<Pericope> {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.pl_gospel)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return Json.decodeFromString<List<Pericope>>(jsonString)
    }

    fun getRandomPericopeWithContext(config: Config): List<Pericope> {
        if (allPericopes.isEmpty()) return emptyList()

        val index = allPericopes.indices.random()
        val main = allPericopes[index]
        val wordCount = main.text.split("\\s+".toRegex()).size

        val useAdditional = when (config.additionalMode) {
            AdditionalMode.NO -> false
            AdditionalMode.YES -> true
            AdditionalMode.CONDITIONAL -> wordCount < config.wordThreshold
        }

        if (!useAdditional) return listOf(main)

        val totalSize = allPericopes.size
        val result = mutableListOf<Pericope>()

        val hasPrev = index > 0
        val hasNext = index < totalSize - 1

        val prev = if (hasPrev && config.prevCount > 0) {
            allPericopes.subList((index - config.prevCount).coerceAtLeast(0), index)
        } else if (!hasPrev) {
            allPericopes.subList(index + 1, (index + 1 + config.startFallback).coerceAtMost(totalSize))
        } else {
            emptyList()
        }

        val next = if (hasNext && config.nextCount > 0) {
            allPericopes.subList(index + 1, (index + 1 + config.nextCount).coerceAtMost(totalSize))
        } else if (!hasNext) {
            allPericopes.subList((index - config.endFallback).coerceAtLeast(0), index)
        } else {
            emptyList()
        }

        result.addAll(prev)
        result.add(main) // main pericope to highlight it in UI.
        result.addAll(next)

        return result
    }
}
