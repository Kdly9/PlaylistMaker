package com.example.playlistmaker.ui

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
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.MediaInteractor
import com.example.playlistmaker.domain.models.Constants
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private val mediaPlayerInteractor = Creator.getMediaInteractor()
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
            setResult(RESULT_OK)
            finish()
        }
        val track = intent.getParcelableExtra<Track>(Constants.SELECTED)

        val trackImage = findViewById<ImageView>(R.id.image)
        val trackName = findViewById<TextView>(R.id.track)
        trackName.text = track?.trackName
        val trackOwner = findViewById<TextView>(R.id.trackOwner)
        trackOwner.text = track?.artistName
        val durationText = findViewById<TextView>(R.id.durationText)
        durationText.text =
            dateFormat.format(track?.trackTime?.toLong())
        val albumText = findViewById<TextView>(R.id.albumText)
        val albumTitle = findViewById<TextView>(R.id.albumTitle)
        if (track?.collectionName!!.isEmpty()) {
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

        mediaPlayerInteractor.preparePlayer(track.previewUrl, object : MediaInteractor.Completion {
            override fun completionAction() {
                currentTimeText.text = dateFormat.format(0)
                uiHandler.removeCallbacks(updateCurrentTimeRunnable)
                playButton.background = ContextCompat.getDrawable(this@PlayerActivity, R.drawable.ic_play)
            }

            override fun errorPrepare() {
                Toast.makeText(this@PlayerActivity, resources.getString(R.string.load_track_error), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun startPlayer() {
                playButton.background = ContextCompat.getDrawable(this@PlayerActivity, R.drawable.ic_pause)
            }

            override fun pausePlayer() {
                uiHandler.removeCallbacks(updateCurrentTimeRunnable)
                playButton.background = ContextCompat.getDrawable(this@PlayerActivity, R.drawable.ic_play)
            }

            override fun paused() {
                uiHandler.post(updateCurrentTimeRunnable)
            }
        })
        currentTimeText = findViewById(R.id.currentTime)

        playButton = findViewById(R.id.playButton)
        playButton.setOnClickListener {
            mediaPlayerInteractor.playbackControl()
        }
    }

    private val updateCurrentTimeRunnable = object : Runnable {
        override fun run() {
            currentTimeText.text = dateFormat.format(mediaPlayerInteractor.getCurrentPosition())
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

    override fun onPause() {
        super.onPause()
        mediaPlayerInteractor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerInteractor.release()
    }

    companion object {
        private const val UPDATE_TIME = 300L
    }
}


