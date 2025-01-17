package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.entity.Constants
import com.example.playlistmaker.entity.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val backButton = findViewById<Toolbar>(R.id.playerToolbar)
        backButton.setOnClickListener {
            finish()
        }
        val track = Track(
            trackId = intent.getStringExtra(Constants.ID) ?: "",
            trackName = intent.getStringExtra(Constants.NAME) ?: "",
            artistName = intent.getStringExtra(Constants.ARTIST_NAME) ?: "",
            trackTime = intent.getStringExtra(Constants.TRACK_TIME) ?: "",
            artworkUrl100 = intent.getStringExtra(Constants.ART_WORK_URL) ?: "",
            collectionName = intent.getStringExtra(Constants.COLLECTION_NAME) ?: "",
            releaseDate = intent.getStringExtra(Constants.RELEASE_DATE) ?: "",
            primaryGenreName = intent.getStringExtra(Constants.PRIMARY_GENRE_NAME) ?: "",
            country = intent.getStringExtra(Constants.COUNTRY) ?: "",
        )

        val trackImage = findViewById<ImageView>(R.id.image)
        val trackName = findViewById<TextView>(R.id.track)
        trackName.text = track.trackName
        val trackOwner = findViewById<TextView>(R.id.trackOwner)
        trackOwner.text = track.artistName
        val durationText = findViewById<TextView>(R.id.durationText)
        durationText.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
        val albumText = findViewById<TextView>(R.id.albumText)
        val albumTitle = findViewById<TextView>(R.id.albumTitle)
        if (track.collectionName!!.isEmpty()) {
            albumTitle.visibility = View.GONE
            albumText.visibility = View.GONE
        } else {
            albumText.text = track.collectionName
        }

        val yearText = findViewById<TextView>(R.id.yearText)
        if (track.releaseDate.isNotEmpty()){
            yearText.text = track.releaseDate.substring(0, 4)
        }

        val styleText = findViewById<TextView>(R.id.styleText)
        styleText.text = track.primaryGenreName
        val countryText = findViewById<TextView>(R.id.countryText)
        countryText.text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.mock_image)
            .centerInside().transform(RoundedCorners(dpToPx(8f, this)))
            .into(trackImage)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}


