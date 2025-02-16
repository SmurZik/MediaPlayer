package com.smurzik.mediaplayer.local.domain

data class LocalTrackDomain(
    private val title: String,
    private val author: String,
    private val albumUri: String,
    val trackUri: String,
    private val duration: Long,
    private val index: Int,
    private val album: String
) {
    interface Mapper<T> {
        fun map(
            title: String,
            author: String,
            albumUri: String,
            trackUri: String,
            duration: Long,
            index: Int,
            album: String
        ): T
    }

    fun <T> map(mapper: Mapper<T>): T =
        mapper.map(title, author, albumUri, trackUri, duration, index, album)
}