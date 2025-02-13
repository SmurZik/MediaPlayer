package com.smurzik.mediaplayer.local.presentation

class TrackMapper : LocalTrackUi.Mapper<Array<String>> {
    override fun map(
        title: String,
        author: String,
        albumUri: String,
        trackUri: String,
        duration: Long
    ): Array<String> = arrayOf(trackUri, title, author, (duration / 1000).toString(), albumUri)
}