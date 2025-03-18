package com.smurzik.mediaplayer.favorite.data

data class FavoriteCloud(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: String,
    val albumUri: String,
    val index: Int
)