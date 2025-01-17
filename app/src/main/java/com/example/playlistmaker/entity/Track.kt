package com.example.playlistmaker.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Track(
    var trackId: String,
    var trackName: String,
    var artistName: String,
    @SerializedName("trackTimeMillis") var trackTime: String,
    var artworkUrl100: String?,
    var collectionName: String?,
    var releaseDate: String,
    var primaryGenreName: String,
    var country: String
) {
    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
}

object Constants {
    const val ID = "ID"
    const val NAME = "NAME"
    const val ARTIST_NAME = "ARTIST_NAME"
    const val COLLECTION_NAME = "COLLECTION_NAME"
    const val RELEASE_DATE = "RELEASE_DATE"
    const val PRIMARY_GENRE_NAME = "PRIMARY_GENRE_NAME"
    const val COUNTRY = "COUNTRY"
    const val TRACK_TIME = "TRACK_TIME"
    const val ART_WORK_URL = "ART_WORK_URL"
}
