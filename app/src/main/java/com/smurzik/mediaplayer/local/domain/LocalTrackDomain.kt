package com.smurzik.mediaplayer.local.domain

data class LocalTrackDomain(
    private val title: String,
    private val author: String,
    private val albumUri: String,
    val trackUri: String,
    private val duration: Long,
    val index: Int,
    private val album: String,
    private val id: Long
) {
    interface Mapper<T> {
        fun map(
            title: String,
            author: String,
            albumUri: String,
            trackUri: String,
            duration: Long,
            index: Int,
            album: String,
            id: Long
        ): T
    }

    fun <T> map(mapper: Mapper<T>): T =
        mapper.map(title, author, albumUri, trackUri, duration, index, album, id)
}