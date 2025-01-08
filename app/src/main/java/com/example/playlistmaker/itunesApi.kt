package com.example.playlistmaker

import com.example.playlistmaker.entity.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface itunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}