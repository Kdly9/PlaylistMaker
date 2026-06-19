package com.example.playlistmaker.search.ui

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.dpToPx
import java.util.Locale

class TracksViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
    private var trackImage: ImageView = parent.findViewById(R.id.trackImage)
    private var trackName: TextView = parent.findViewById(R.id.trackName)
    private var trackOwner: TextView = parent.findViewById(R.id.trackOwner)
    private var trackTime: TextView = parent.findViewById(R.id.trackTime)
    private val context = parent.context
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    fun bind(track: Track) {
        Glide.with(context).load(track.artworkUrl100).placeholder(R.drawable.mock_image)
            .transform(CenterCrop(), RoundedCorners(dpToPx(2f, context)))
            .into(trackImage)
        trackOwner.text = track.artistName
        trackTime.text = dateFormat.format(track.trackTimeMillis.toLong())
        trackName.text = track.trackName
    }
}

interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}

class TracksAdapter(private val listener: OnTrackClickListener) :
    RecyclerView.Adapter<TracksViewHolder>() {

    private var tracksList: ArrayList<Track> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_view, parent, false)
        return TracksViewHolder(view)
    }
    private var onLongClickListener: ((Track) -> Unit)? = null

    fun setOnLongClickListener(listener: (Track) -> Unit) {
        onLongClickListener = listener
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracksList[position])
        holder.itemView.setOnClickListener {
            listener.onTrackClick(tracksList[position])
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener?.invoke(tracksList[position])
            true
        }
    }

    fun updateData(newTracks: List<Track>) {
        tracksList.clear()
        tracksList.addAll(newTracks)
    }

    override fun getItemCount(): Int {
        return tracksList.size
    }
}