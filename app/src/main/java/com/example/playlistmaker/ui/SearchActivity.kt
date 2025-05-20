package com.example.playlistmaker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Constants
import com.example.playlistmaker.domain.models.Track

class SearchActivity : AppCompatActivity() {
    private var lastText = ""

    private lateinit var searchText: EditText
    private val tracksData = ArrayList<Track>()
    private lateinit var searchErrorImage: ImageView
    private lateinit var searchErrorTextView: TextView
    private lateinit var connectionErrorImage: ImageView
    private lateinit var connectionErrorTextView: TextView
    private lateinit var updateButton: Button
    private lateinit var clearButtonHistory: Button
    private lateinit var lookingFor: TextView
    private var tracksHistory = ArrayList<Track>()
    private var hasError = false
    private lateinit var sharedPrefs: SharedPreferences

    private var isClickAllowed = true
    private val searchRunnable = Runnable { itunesResponse() }
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var progressBar: ProgressBar

    private val trackInteractor = Creator.provideTrackInteractor()
    private val tracksHistoryInteractor by lazy(LazyThreadSafetyMode.NONE) {
        Creator.getTracksHistoryRepositoryInteractor()
    }

    private lateinit var playerActivityResultLauncher: ActivityResultLauncher<Intent>

    private val tracksAdapter = TracksAdapter(tracksData, object : OnTrackClickListener {
        override fun onTrackClick(track: Track) {
            if (clickDebounce()) {
                tracksHistory = tracksHistoryInteractor.getHistory() as ArrayList<Track>
                if (tracksHistory.size >= 10) {
                    tracksHistory.removeAt(tracksHistory.size - 1)
                }

                if (tracksHistory.any { it.trackId == track.trackId }) {
                    tracksHistory.remove(track)
                }
                tracksHistory.add(0, track)
                tracksHistoryInteractor.saveHistory(tracksHistory)

                val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java).apply {
                    putExtra(Constants.SELECTED, track)
                }
                playerActivityResultLauncher.launch(playerIntent)

            }
        }
    })
    private var lastSearchText = ""

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        sharedPrefs = getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE)

        val backButton = findViewById<Toolbar>(R.id.toolbar)
        backButton.setOnClickListener {
            finish()
        }

        playerActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && searchText.text.isEmpty()) {
                showHistory(true)
            }
        }


        progressBar = findViewById(R.id.progressBar)

        searchErrorImage = findViewById(R.id.errorSearch)
        searchErrorTextView = findViewById(R.id.errorSearchText)
        connectionErrorImage = findViewById(R.id.errorConnect)
        connectionErrorTextView = findViewById(R.id.errorConnectText)
        updateButton = findViewById(R.id.updateButton)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = tracksAdapter

        searchText = findViewById(R.id.search)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            searchText.text.clear()
            tracksData.clear()
            tracksAdapter.notifyDataSetChanged()
            showHistory(true)
            showErrorData(false)
            showErrorConnection(false)
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lastText = s.toString()
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.GONE
                } else {
                    lastSearchText = lastText
                    searchDebounce()
                    clearButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        updateButton.setOnClickListener {
            itunesResponse()
        }

        searchText.addTextChangedListener(textWatcher)
        searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                lastSearchText = searchText.text.toString()
                itunesResponse()
                true
            }
            false
        }
        clearButtonHistory = findViewById(R.id.clearHistory)
        lookingFor = findViewById(R.id.lookingText)
        searchText.setOnFocusChangeListener { view, hasFocus ->
            showHistory(hasFocus && searchText.text.isEmpty())
        }

        clearButtonHistory.setOnClickListener {
            tracksHistoryInteractor.clearHistory()
            showHistory(true)
        }
    }

    private fun showHistory(show: Boolean) {
        if (show) {
            tracksHistory = tracksHistoryInteractor.getHistory() as ArrayList<Track>
            if (tracksHistory.isEmpty()) {
                clearButtonHistory.visibility = View.GONE
                lookingFor.visibility = View.GONE
            } else {
                clearButtonHistory.visibility = View.VISIBLE
                lookingFor.visibility = View.VISIBLE
            }
            tracksAdapter.updateData(tracksHistory)
            tracksAdapter.notifyDataSetChanged()
        } else {
            clearButtonHistory.visibility = View.GONE
            lookingFor.visibility = View.GONE

            tracksAdapter.updateData(ArrayList())
            tracksAdapter.notifyDataSetChanged()
        }
    }


    private fun itunesResponse() {
        if (lastSearchText.isNotEmpty()) {
            showHistory(false)
            showErrorConnection(false)
            showErrorData(false)
            progressBar.visibility = View.VISIBLE
            trackInteractor.searchTracks(lastSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    handler.post {
                        tracksData.clear()
                        if (foundTracks.isNotEmpty()) {
                            tracksData.addAll(foundTracks)
                        } else {
                            showErrorData(true)
                        }
                        progressBar.visibility = View.GONE
                        tracksAdapter.notifyDataSetChanged()
                    }
                }

                override fun failure() {
                    handler.post {
                        showHistory(false)
                        tracksData.clear()
                        progressBar.visibility = View.GONE
                        tracksAdapter.notifyDataSetChanged()
                        showErrorConnection(true)
                    }
                }
            })
        }
    }

    private fun showErrorData(show: Boolean) {
        if (show) {
            searchErrorImage.visibility = View.VISIBLE
            searchErrorTextView.visibility = View.VISIBLE
        } else {
            searchErrorImage.visibility = View.GONE
            searchErrorTextView.visibility = View.GONE
        }
    }

    private fun showErrorConnection(show: Boolean) {
        if (show) {
            connectionErrorImage.visibility = View.VISIBLE
            connectionErrorTextView.visibility = View.VISIBLE
            updateButton.visibility = View.VISIBLE
        } else {
            connectionErrorImage.visibility = View.GONE
            connectionErrorTextView.visibility = View.GONE
            updateButton.visibility = View.GONE
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_TEXT, lastText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        lastText = savedInstanceState.getString(SAVED_TEXT, "")
        searchText.setText(lastText)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        private const val SAVED_TEXT = "SAVED_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
