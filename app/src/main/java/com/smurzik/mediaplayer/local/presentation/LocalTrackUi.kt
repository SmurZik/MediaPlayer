package com.smurzik.mediaplayer.local.presentation

data class LocalTrackUi(
    private val title: String,
    private val author: String,
    private val albumUri: String,
    private val trackUri: String,
    private val duration: Long
) {

    interface Mapper<T> {
        fun map(
            title: String,
            author: String,
            albumUri: String,
            trackUri: String,
            duration: Long
        ): T
    }

    fun <T> map(mapper: Mapper<T>): T = mapper.map(title, author, albumUri, trackUri, duration)
}