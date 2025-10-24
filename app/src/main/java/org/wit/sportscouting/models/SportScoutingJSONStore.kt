package org.wit.sportscouting.models

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class SportScoutingJSONStore(private val context: Context) {

    private val fileName = "sportscouting.json"
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private fun dataFile(): File = File(context.filesDir, fileName)

    // Load the data of the app. If there isn't data, load sample_players.json
    fun load(): MutableList<SportScoutingModel> {
        val file = dataFile()
        return when {
            file.exists() -> {
                val text = file.readText()
                if (text.isBlank()) mutableListOf()
                else json.decodeFromString(text)
            }
            else -> {
                // load assets/sample_players.json
                runCatching {
                    context.assets.open("sample_players.json").bufferedReader().use { it.readText() }
                }.mapCatching { sample ->
                    json.decodeFromString<MutableList<SportScoutingModel>>(sample)
                }.getOrElse { mutableListOf() }
            }
        }
    }

    // Save the data in the JSON file
    fun saveAll(items: List<SportScoutingModel>) {
        val text = json.encodeToString(items)
        dataFile().writeText(text)
    }
}
