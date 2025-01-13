package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.entity.Track
import com.example.playlistmaker.entity.TrackResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val TRACKS_HISTORY_KEY = "tracks_history"

class SearchActivity : AppCompatActivity() {
    private var lastText = ""
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(itunesApi::class.java)
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

    private val tracksAdapter = TracksAdapter(tracksData, object : OnTrackClickListener {
        override fun onTrackClick(track: Track) {
            tracksHistory = readTracksHistory(sharedPrefs) ?: ArrayList()
            if (tracksHistory.size >= 10) {
                tracksHistory.removeAt(tracksHistory.size - 1)
            }

            if (tracksHistory.any { it.trackId == track.trackId }) {
                tracksHistory.remove(track)
            }
            tracksHistory.add(0, track)
            writeTracksHistory(sharedPrefs, tracksHistory)
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
                    clearButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        updateButton.setOnClickListener {
            itunesResponse(lastSearchText)
        }

        searchText.addTextChangedListener(textWatcher)
        searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                lastSearchText = searchText.text.toString()
                itunesResponse(searchText.text.toString())
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
            sharedPrefs.edit().remove(TRACKS_HISTORY_KEY).apply()
            showHistory(true)
        }
    }

    private fun showHistory(show: Boolean) {
        if (show) {
            clearButtonHistory.visibility = View.VISIBLE
            lookingFor.visibility = View.VISIBLE
            tracksHistory = readTracksHistory(sharedPrefs) ?: ArrayList()
            tracksAdapter.updateData(tracksHistory)
        } else {
            clearButtonHistory.visibility = View.GONE
            lookingFor.visibility = View.GONE
        }

    }


    private fun itunesResponse(textResponse: String) {
        if (textResponse.isNotEmpty()) {
            itunesService.search(textResponse)
                .enqueue(object : Callback<TrackResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        showHistory(false)
                        showErrorConnection(false)
                        showErrorData(false)
                        if (response.code() == 200) {
                            hasError = false
                            tracksData.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracksData.addAll(response.body()?.results!!)
                            }
                            if (tracksData.isEmpty()) {
                                showErrorData(true)
                            }
                        } else {
                            tracksData.clear()
                            showErrorConnection(true)
                        }
                        tracksAdapter.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showHistory(false)
                        tracksData.clear()
                        tracksAdapter.notifyDataSetChanged()
                        showErrorConnection(true)
                    }
                })
        }
    }

    fun readTracksHistory(sharedPreferences: SharedPreferences): ArrayList<Track>? {
        val json = sharedPreferences.getString(TRACKS_HISTORY_KEY, null) ?: return null
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun writeTracksHistory(sharedPreferences: SharedPreferences, tracks: ArrayList<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit()
            .putString(TRACKS_HISTORY_KEY, json)
            .apply()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_TEXT, lastText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        lastText = savedInstanceState.getString(SAVED_TEXT, "")
        searchText.setText(lastText)
    }

    companion object {
        const val SAVED_TEXT = "SAVED_TEXT"
    }
}
