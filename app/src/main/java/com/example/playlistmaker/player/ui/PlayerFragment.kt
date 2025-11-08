package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val playerViewModel by viewModel<PlayerViewModel>()

    private val dateFormat by lazy {
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }

    companion object {

        private const val SELECTED_KEY = "selected_key"

        fun createArgs(key: Track): Bundle = bundleOf(SELECTED_KEY to key)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playerToolbar.setOnClickListener {
            //setResult(RESULT_OK)
            findNavController().navigateUp()
        }
        val track = arguments?.getParcelable<Track>(SELECTED_KEY)

        playerViewModel.setUrl(track?.previewUrl ?: "")

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

        playerViewModel.observeShowToast().observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.load_track_error),
                Toast.LENGTH_SHORT
            )
                .show()
        }

        playerViewModel.preparePlayer()

        playerViewModel.observePlayerState().observe(viewLifecycleOwner) {
            when (it) {
                PlayerState.CompletionAction -> {
                    binding.currentTime.text = dateFormat.format(0)
                    binding.playButton.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
                }

                PlayerState.Paused -> {
                    binding.playButton.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
                }

                is PlayerState.Start -> {
                    binding.currentTime.text = dateFormat.format(it.currentPosition)
                    binding.playButton.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause)
                }
            }
        }

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.mock_image)
            .centerInside().transform(RoundedCorners(dpToPx(8f, requireContext())))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}