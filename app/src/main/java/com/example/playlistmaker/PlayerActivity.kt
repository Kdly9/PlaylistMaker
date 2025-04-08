package com.example.playlistmaker

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.entity.Constants
import com.example.playlistmaker.entity.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val uiHandler = Handler(Looper.getMainLooper())
    private val dateFormat by lazy {
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }
    private lateinit var playButton: ImageButton
    private lateinit var currentTimeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val backButton = findViewById<Toolbar>(R.id.playerToolbar)
        backButton.setOnClickListener {
            finish()
        }
        val track = Track(
            trackId = intent.getStringExtra(Constants.ID).orEmpty(),
            trackName = intent.getStringExtra(Constants.NAME).orEmpty(),
            artistName = intent.getStringExtra(Constants.ARTIST_NAME).orEmpty(),
            trackTime = intent.getStringExtra(Constants.TRACK_TIME).orEmpty(),
            artworkUrl100 = intent.getStringExtra(Constants.ART_WORK_URL).orEmpty(),
            collectionName = intent.getStringExtra(Constants.COLLECTION_NAME).orEmpty(),
            releaseDate = intent.getStringExtra(Constants.RELEASE_DATE).orEmpty(),
            primaryGenreName = intent.getStringExtra(Constants.PRIMARY_GENRE_NAME).orEmpty(),
            country = intent.getStringExtra(Constants.COUNTRY).orEmpty(),
            previewUrl = intent.getStringExtra(Constants.PREVIEW_URL).orEmpty()
        )

        val trackImage = findViewById<ImageView>(R.id.image)
        val trackName = findViewById<TextView>(R.id.track)
        trackName.text = track.trackName
        val trackOwner = findViewById<TextView>(R.id.trackOwner)
        trackOwner.text = track.artistName
        val durationText = findViewById<TextView>(R.id.durationText)
        durationText.text =
            dateFormat.format(track.trackTime.toLong())
        val albumText = findViewById<TextView>(R.id.albumText)
        val albumTitle = findViewById<TextView>(R.id.albumTitle)
        if (track.collectionName!!.isEmpty()) {
            albumTitle.visibility = View.GONE
            albumText.visibility = View.GONE
        } else {
            albumText.text = track.collectionName
        }

        val yearText = findViewById<TextView>(R.id.yearText)
        if (track.releaseDate.isNotEmpty()) {
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

        preparePlayer(track.previewUrl)
        currentTimeText = findViewById(R.id.currentTime)

        playButton = findViewById(R.id.playButton)
        playButton.setOnClickListener {
            playbackControl()
        }
    }

    private val updateCurrentTimeRunnable = object : Runnable {
        override fun run() {
            currentTimeText.text = dateFormat.format(mediaPlayer.currentPosition)
            uiHandler.postDelayed(this, UPDATE_TIME)
        }
    }


    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                uiHandler.post(updateCurrentTimeRunnable)
            }

            STATE_ERROR -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.load_track_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun preparePlayer(url: String) {
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                currentTimeText.text = dateFormat.format(0)
                uiHandler.removeCallbacks(updateCurrentTimeRunnable)
                playButton.background = ContextCompat.getDrawable(this, R.drawable.ic_play)
                playerState = STATE_PREPARED
            }
        } catch (_: Exception) {
            playerState = STATE_ERROR
            Toast.makeText(this, resources.getString(R.string.load_track_error), Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.background = ContextCompat.getDrawable(this, R.drawable.ic_pause)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        uiHandler.removeCallbacks(updateCurrentTimeRunnable)
        playButton.background = ContextCompat.getDrawable(this, R.drawable.ic_play)
        playerState = STATE_PAUSED
    }


    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val STATE_ERROR = 4
        private const val UPDATE_TIME = 300L
    }
}


