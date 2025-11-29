package com.example.playlistmaker.media.ui

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

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playlistPoster: ImageView = itemView.findViewById(R.id.poster)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
    private val tracksNumbers: TextView = itemView.findViewById(R.id.tracksNumbers)
    private val context = itemView.context

    fun bind(item: Playlist, loadImage: (String) -> Bitmap?) {
        playlistName.text = item.name
        tracksNumbers.text = convertText(item.tracksCount)

        when {
            !item.imagePath.isNullOrEmpty() -> {
                loadImage(item.imagePath)?.let { bitmap ->
                    Glide.with(itemView)
                        .load(bitmap)
                        .placeholder(R.drawable.mock_image)
                        .error(R.drawable.mock_image)
                        .transform(CenterCrop(), RoundedCorners(dpToPx(8f, context)))
                        .into(playlistPoster)
                } ?: setDefaultImage()
            }

            else -> setDefaultImage()
        }
    }

    private fun setDefaultImage() {
        Glide.with(itemView)
            .load(R.drawable.mock_image)
            .centerCrop()
            .into(playlistPoster)
    }


    private fun convertText(count: Int): String {
        return when {
            count % 100 in 11..14 -> context.getString(R.string.track_count_plural, count)
            count % 10 == 1 -> context.getString(R.string.track_count_singular, count)
            count % 10 in 2..4 -> context.getString(R.string.track_count_few, count)
            else -> context.getString(R.string.track_count_plural, count)
        }
    }
}