package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Constants
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private val mediaPlayerInteractor = Creator.getMediaInteractor()
    private lateinit var playerViewModel: PlayerViewModel

    private val dateFormat by lazy {
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playerToolbar.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        val track = intent.getParcelableExtra<Track>(Constants.SELECTED)

        playerViewModel = ViewModelProvider(
            this,
            PlayerViewModel.getFactory(
                mediaPlayerInteractor,
                track?.previewUrl ?: ""
            )
        )[PlayerViewModel::class.java]

        binding.track.text = track?.trackName
        binding.trackOwner.text = track?.artistName
        binding.durationText.text =
            dateFormat.format(track?.trackTime?.toLong())

        if (track?.collectionName!!.isEmpty()) {
            binding.albumTitle.visibility = View.GONE
            binding.albumText.visibility = View.GONE
        } else {
            binding.albumText.text = track.collectionName
        }

        if (track.releaseDate.isNotEmpty()) {
            binding.yearText.text = track.releaseDate.substring(0, 4)
        }

        binding.styleText.text = track.primaryGenreName
        binding.countryText.text = track.country

        playerViewModel.observeShowToast().observe(this){
            Toast.makeText(this@PlayerActivity, resources.getString(R.string.load_track_error), Toast.LENGTH_SHORT)
                .show()
        }

        playerViewModel.preparePlayer()

        playerViewModel.observePlayerState().observe(this) {
            when (it) {
                PlayerState.CompletionAction -> {
                    binding.currentTime.text = dateFormat.format(0)
                    binding.playButton.background =
                        ContextCompat.getDrawable(this@PlayerActivity, R.drawable.ic_play)
                }

                PlayerState.Paused -> {
                    binding.playButton.background =
                        ContextCompat.getDrawable(this@PlayerActivity, R.drawable.ic_play)
                }

                is PlayerState.Start -> {
                    binding.currentTime.text = dateFormat.format(it.currentPosition)
                    binding.playButton.background =
                        ContextCompat.getDrawable(this@PlayerActivity, R.drawable.ic_pause)
                }
            }
        }

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.mock_image)
            .centerInside().transform(RoundedCorners(dpToPx(8f, this)))
            .into(binding.image)

        binding.playButton.setOnClickListener {
            playerViewModel.playbackControl()
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
        playerViewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.onRelease()
    }

}


