package com.smurzik.mediaplayer.local.presentation

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.smurzik.mediaplayer.R

class ListItemMapper(
    private val title: TextView,
    private val artist: TextView,
    private val albumArt: ImageView
) :
    LocalTrackUi.Mapper<Unit> {
    override fun map(
        title: String,
        author: String,
        albumUri: String,
        trackUri: String,
        duration: Long,
        index: Int,
        album: String,
        id: Long
    ) {
        this.title.text = title
        artist.text = author
        Glide.with(albumArt).load(albumUri).placeholder(R.drawable.ic_music_note).into(albumArt)
    }
}