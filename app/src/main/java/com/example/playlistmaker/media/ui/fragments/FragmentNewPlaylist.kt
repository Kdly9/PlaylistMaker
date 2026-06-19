package com.example.playlistmaker.media.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.NewPlaylistState
import com.example.playlistmaker.media.view_model.NewPlaylistViewModel
import com.example.playlistmaker.utils.dpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class FragmentNewPlaylist : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private var playlistName = ""
    private var playlistDescription = ""
    private var imageUri: Uri? = null
    private val newPlaylistViewModel by viewModel<NewPlaylistViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getLong(PLAYLIST_ID) ?: -1L

        newPlaylistViewModel.setPlaylist(playlistId)

        newPlaylistViewModel.screenState.observe(viewLifecycleOwner) { state ->

            when (state) {
                is NewPlaylistState.EditState -> {
                    binding.playlistToolbar.title = getString(R.string.edit)
                    binding.createButton.text = getString(R.string.save)
                    binding.playlistNameEditText.setText(state.playlist.name)
                    binding.playlistDescriptionEditText.setText(state.playlist.description)
                    playlistName = state.playlist.name
                    playlistDescription = state.playlist.description ?: ""

                    state.playlist.imagePath?.let {
                        Glide.with(binding.imagePlaylist)
                            .load(File(it))
                            .into(binding.imagePlaylist)
                        binding.icon.isVisible = false
                        imageUri = File(it).toUri()
                    }
                }

                is NewPlaylistState.CreateState -> {
                    binding.playlistToolbar.title = getString(R.string.new_playlist)
                    binding.createButton.text = getString(R.string.create)
                }
            }
        }

        binding.playlistToolbar.setOnClickListener {
            confirmationDialog()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            confirmationDialog()
        }

        binding.playlistNameEditText.doOnTextChanged { text, _, _, _ ->
            binding.createButton.isEnabled = !text?.trim().isNullOrEmpty()
            if (text != null && text.endsWith("\n")) {
                val cursorPosition = binding.playlistNameEditText.selectionStart
                binding.playlistNameEditText.setText(text.substring(0, text.length - 1))
                binding.playlistNameEditText.setSelection(cursorPosition - 1)

                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(
                    binding.playlistNameEditText.windowToken,
                    0
                )
                playlistName = binding.playlistNameEditText.text.toString()
            } else {
                if (!text.isNullOrEmpty()) {
                    playlistName = text.toString()
                }
            }
        }
        binding.playlistNameEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }


        binding.playlistDescriptionEditText.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.endsWith("\n")) {
                val cursorPosition = binding.playlistDescriptionEditText.selectionStart
                binding.playlistDescriptionEditText.setText(text.substring(0, text.length - 1))
                binding.playlistDescriptionEditText.setSelection(cursorPosition - 1)

                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(
                    binding.playlistDescriptionEditText.windowToken,
                    0
                )
                playlistDescription = binding.playlistDescriptionEditText.text.toString()
            } else {
                playlistDescription = text.toString()
            }
        }

        binding.playlistDescriptionEditText.setOnEditorActionListener { v, actionId, event ->
            if (event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER ||
                        actionId == EditorInfo.IME_ACTION_DONE)
            ) {
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri == null) return@registerForActivityResult
                Glide.with(this)
                    .load(uri)
                    .transform(CenterCrop(), RoundedCorners(dpToPx(8f, requireContext())))
                    .into(binding.imagePlaylist)
                binding.icon.isVisible = false
                imageUri = uri

            }
        binding.imagePlaylist.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createButton.setOnClickListener {
            if (playlistName.isBlank()) return@setOnClickListener
            val savedImagePath = imageUri?.let { uri ->
                saveImageToInternalStorage(requireContext(), uri)
            }

            val newPlaylist = Playlist(
                id = 0,
                name = playlistName,
                description = playlistDescription,
                imagePath = savedImagePath,
                trackIds = emptyList(),
                tracksCount = 0
            )

            newPlaylistViewModel.savePlaylist(newPlaylist)
            val message = if (playlistId != 0L) {
                getString(R.string.playlist_created_message, playlistName)
            } else {
                getString(R.string.playlist_changed_message, playlistName)
            }
            Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()

        }
    }

    private fun saveImageToInternalStorage(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val directory = File(context.filesDir, "playlist_covers").apply { mkdirs() }
            val fileName = "cover_${System.currentTimeMillis()}.jpg"
            val outputFile = File(directory, fileName)

            inputStream?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            outputFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun confirmationDialog() {
        val state = newPlaylistViewModel.screenState.value
        if (state is NewPlaylistState.EditState) {
            findNavController().navigateUp()
        } else {
            if (imageUri != null || playlistName.isNotEmpty()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_info)
                    .setNeutralButton(R.string.cancel) { dialog, which ->
                    }
                    .setPositiveButton(R.string.close) { dialog, which ->
                        findNavController().navigateUp()
                    }
                    .show()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    companion object {

        private const val PLAYLIST_ID = "playlistId"
        fun createArgs(id: Long): Bundle = Bundle().apply {
            putLong(PLAYLIST_ID, id)
        }
    }
}