package com.example.playlistmaker.player.ui

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist

class PlaylistsBottomSheetAdapter(private val onPlaylistClickListener: (Playlist) -> Unit,
                                  private val loadImage: (String?) -> Bitmap?) : RecyclerView.Adapter<PlaylistsBottomSheetViewHolder>() {
    var playlists: List<Playlist> = emptyList()
        set(value) {
            field = value.toList()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsBottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_audio_view, parent, false)
        return PlaylistsBottomSheetViewHolder(view, onPlaylistClickListener, loadImage)
    }

    override fun onBindViewHolder(holder: PlaylistsBottomSheetViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size
}