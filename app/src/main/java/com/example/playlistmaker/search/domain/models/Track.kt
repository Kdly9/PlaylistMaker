package com.example.playlistmaker.search.domain.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    var trackId: String,
    var trackName: String,
    var artistName: String,
    var trackTimeMillis: String,
    var artworkUrl100: String?,
    var collectionName: String?,
    var releaseDate: String,
    var primaryGenreName: String,
    var country: String,
    var previewUrl: String,
    var isFavorite: Boolean = false
) : Parcelable {
    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
}

object Constants {
    const val SELECTED = "selected_track"
}
