package com.smurzik.mediaplayer.local.presentation


data class LocalTrackUi(
    val title: String,
    val author: String,
    val albumUri: String,
    val trackUri: String,
    val duration: Long,
    val index: Int,
    val album: String,
    val id: Long
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