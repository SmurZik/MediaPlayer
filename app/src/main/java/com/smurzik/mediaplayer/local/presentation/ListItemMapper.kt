package com.smurzik.mediaplayer.local.presentation

import android.widget.TextView

class ListItemMapper(private val title: TextView, private val artist: TextView) :
    LocalTrackUi.Mapper<Unit> {
    override fun map(title: String, author: String) {
        this.title.text = title
        artist.text = author
    }
}