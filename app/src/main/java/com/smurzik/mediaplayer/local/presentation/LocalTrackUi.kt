package com.smurzik.mediaplayer.local.presentation


data class LocalTrackUi(
    private val title: String,
    private val author: String,
    private val albumUri: String,
    val trackUri: String,
    private val duration: Long,
    val index: Int
) {

    interface Mapper<T> {
        fun map(
            title: String,
            author: String,
            albumUri: String,
            trackUri: String,
            duration: Long,
            index: Int
        ): T
    }

    fun <T> map(mapper: Mapper<T>): T =
        mapper.map(title, author, albumUri, trackUri, duration, index)
}