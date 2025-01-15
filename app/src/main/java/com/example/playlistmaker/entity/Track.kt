package com.example.playlistmaker.entity

import com.google.gson.annotations.SerializedName

data class Track(
    var trackId: String,
    var trackName: String,
    var artistName: String,
    @SerializedName("trackTimeMillis") var trackTime: String,
    var artworkUrl100: String?
)

