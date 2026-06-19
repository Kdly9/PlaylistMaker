package com.example.playlistmaker.player.ui

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.utils.dpToPx

class PlaylistsBottomSheetViewHolder(
    itemView: View,
    private val onPlaylistClickListener: (Playlist) -> Unit,
    private val loadImage: (String?) -> Bitmap?
) : RecyclerView.ViewHolder(itemView) {

    private val playlistPoster: ImageView = itemView.findViewById(R.id.poster)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistAudioName)
    private val playlistTracksNumbers: TextView = itemView.findViewById(R.id.numberOfTracks)
    private val context = itemView.context

    fun bind(playlist: Playlist) {
        itemView.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onPlaylistClickListener(playlist)
            }
        }

        playlistName.text = playlist.name
        playlistTracksNumbers.text = convertTrackCountText(playlist.tracksCount)

        loadImage(playlist.imagePath)?.let { bitmap ->
            Glide.with(itemView)
                .load(bitmap)
                .placeholder(R.drawable.mock_image)
                .error(R.drawable.mock_image)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8f, itemView.context)))
                .into(playlistPoster)
        } ?: run {
            playlistPoster.setImageResource(R.drawable.mock_image)
        }
    }

    private fun convertTrackCountText(count: Int): String {
        return when {
            count % 100 in 11..14 -> context.getString(R.string.track_count_plural, count)
            count % 10 == 1 -> context.getString(R.string.track_count_singular, count)
            count % 10 in 2..4 -> context.getString(R.string.track_count_few, count)
            else -> context.getString(R.string.track_count_plural, count)
        }
    }
}