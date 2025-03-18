package com.smurzik.mediaplayer.local.data

data class LocalTrackData(
    val title: String,
    val author: String,
    val albumUri: String,
    val album: String,
    val trackUri: String,
    val duration: Long,
    val index: Int,
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
            id: Long,
            isFavorite: Boolean
        ): T
    }

    fun <T> map(mapper: Mapper<T>): T =
        mapper.map(title, author, albumUri, trackUri, duration, index, album, id, isFavorite = false)
}