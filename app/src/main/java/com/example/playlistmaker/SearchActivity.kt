package com.example.playlistmaker

import android.annotation.SuppressLint
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
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Response

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
    private val tracksAdapter = TracksAdapter(tracksData)
    private var lastSearchText = ""

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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
            showErrorData(false)
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

        updateButton.setOnClickListener{
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
    }

    private fun itunesResponse(textResponse: String){
        if (textResponse.isNotEmpty()) {
            itunesService.search(textResponse)
                .enqueue(object : Callback<TrackResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        showErrorConnection(false)
                        showErrorData(false)
                        if (response.code() == 200) {
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
                        tracksData.clear()
                        tracksAdapter.notifyDataSetChanged()
                        showErrorConnection(true)
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

    private fun getMockData(): ArrayList<Track> {
        val tracks = ArrayList<Track>()
        tracks.add(
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            )
        )
        tracks.add(
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            )
        )
        tracks.add(
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            )
        )
        tracks.add(
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            )
        )
        tracks.add(
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )
        return tracks
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



