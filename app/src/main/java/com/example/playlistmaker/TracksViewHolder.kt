package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.entity.Track

class TracksViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
    private var trackImage: ImageView = parent.findViewById(R.id.trackImage)
    private var trackName: TextView = parent.findViewById(R.id.trackName)
    private var trackOwner: TextView = parent.findViewById(R.id.trackOwner)
    private var trackTime: TextView = parent.findViewById(R.id.trackTime)
    private val context = parent.context

    fun bind(track: Track) {
        Glide.with(context).load(track.artworkUrl100).placeholder(R.drawable.mock_image).centerInside().transform(RoundedCorners(dpToPx(2f, context)))
            .into(trackImage)
        trackOwner.text = track.artistName
        trackTime.text = track.trackTime
        trackName.text = track.trackName
    }
}

class TracksAdapter(private val tracksList: ArrayList<Track>) :
    RecyclerView.Adapter<TracksViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracksList[position])
    }

    override fun getItemCount(): Int {
        return tracksList.size
    }
}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}