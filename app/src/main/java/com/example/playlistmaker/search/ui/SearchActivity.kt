package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Constants
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var playerActivityResultLauncher: ActivityResultLauncher<Intent>

    private val tracksAdapter = TracksAdapter(object : OnTrackClickListener {
        override fun onTrackClick(track: Track) {
            if (clickDebounce()) {
                searchViewModel.onTrackClick(track)
            }
        }
    })
    private lateinit var binding: ActivitySearchBinding
    private val searchViewModel by viewModel<SearchViewModel>()

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setOnClickListener {
            finish()
        }

        searchViewModel.observeRunPlayer().observe(this) {
            val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java).apply {
                putExtra(Constants.SELECTED, it)
            }
            playerActivityResultLauncher.launch(playerIntent)
        }

        searchViewModel.observeSearchState().observe(this) {
            when (it) {
                is TracksState.Content -> {
                    tracksAdapter.updateData(it.tracks)
                    binding.progressBar.visibility = View.GONE
                    tracksAdapter.notifyDataSetChanged()
                }

                TracksState.Empty -> {
                    showErrorData(true)
                    binding.progressBar.visibility = View.GONE
                    tracksAdapter.notifyDataSetChanged()
                }

                TracksState.Failure -> {
                    hideHistory()
                    tracksAdapter.updateData(emptyList())
                    binding.progressBar.visibility = View.GONE
                    tracksAdapter.notifyDataSetChanged()
                    showErrorConnection(true)
                }

                TracksState.Loading -> {
                    hideHistory()
                    showErrorConnection(false)
                    showErrorData(false)
                    binding.progressBar.visibility = View.VISIBLE
                }

                is TracksState.History -> {
                    if (it.tracks.isEmpty()) {
                        binding.clearHistory.visibility = View.GONE
                        binding.lookingText.visibility = View.GONE
                    } else {
                        binding.clearHistory.visibility = View.VISIBLE
                        binding.lookingText.visibility = View.VISIBLE
                    }
                    tracksAdapter.updateData(it.tracks)
                    tracksAdapter.notifyDataSetChanged()
                }
            }
        }

        playerActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && binding.search.text.isNullOrEmpty()) {
                searchViewModel.showHistory(true)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = tracksAdapter

        binding.clearIcon.setOnClickListener {
            binding.search.text?.clear()
            tracksAdapter.updateData(emptyList())
            tracksAdapter.notifyDataSetChanged()
            searchViewModel.showHistory(true)
            showErrorData(false)
            showErrorConnection(false)
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.clearIcon.visibility = View.GONE
                } else {
                    searchViewModel.searchDebounce(s.toString())
                    binding.clearIcon.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        binding.updateButton.setOnClickListener {
            searchViewModel.updateSearch()
        }

        binding.search.addTextChangedListener(textWatcher)

        binding.search.setOnFocusChangeListener { _, hasFocus ->
            searchViewModel.showHistory(hasFocus && binding.search.text.isNullOrEmpty())
        }

        binding.clearHistory.setOnClickListener {
            searchViewModel.onHistoryClear()
        }
    }

    private fun hideHistory() {
        binding.clearHistory.visibility = View.GONE
        binding.lookingText.visibility = View.GONE

        tracksAdapter.updateData(ArrayList())
        tracksAdapter.notifyDataSetChanged()
    }

    private fun showErrorData(show: Boolean) {
        if (show) {
            binding.errorSearch.visibility = View.VISIBLE
            binding.errorSearchText.visibility = View.VISIBLE
        } else {
            binding.errorSearch.visibility = View.GONE
            binding.errorSearchText.visibility = View.GONE
        }
    }

    private fun showErrorConnection(show: Boolean) {
        if (show) {
            binding.errorConnect.visibility = View.VISIBLE
            binding.errorConnectText.visibility = View.VISIBLE
            binding.updateButton.visibility = View.VISIBLE
        } else {
            binding.errorConnect.visibility = View.GONE
            binding.errorConnectText.visibility = View.GONE
            binding.updateButton.visibility = View.GONE
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
