package com.smurzik.mediaplayer.local.presentation

class TrackMapper : LocalTrackUi.Mapper<String> {
    override fun map(
        title: String,
        author: String,
        albumUri: String,
        trackUri: String,
        duration: Long
    ) = trackUri
}