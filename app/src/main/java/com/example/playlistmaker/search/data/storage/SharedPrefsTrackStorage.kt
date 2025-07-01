package com.example.playlistmaker.search.data.storage

import android.content.Context
import androidx.core.content.edit
import com.example.playlistmaker.search.data.dto.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val TRACKS_HISTORY_KEY = "tracks_history"

class SharedPrefsTrackStorage(context: Context) : TrackStorage {
    private val sharedPreferences =
        context.getSharedPreferences(TRACKS_HISTORY_KEY, Context.MODE_PRIVATE)

    override fun saveTracks(tracks: List<TrackDto>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit() {
            putString(TRACKS_HISTORY_KEY, json)
        }
    }

    override fun getTracks(): List<TrackDto> {
        val json =
            sharedPreferences.getString(TRACKS_HISTORY_KEY, null)
                ?: return emptyList()
        val type = object : TypeToken<List<TrackDto>>() {}.type
        return Gson().fromJson(json, type)
    }

    override fun clear() {
        sharedPreferences.edit().remove(TRACKS_HISTORY_KEY).apply()
    }
}