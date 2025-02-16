package com.smurzik.mediaplayer.player.presentation

data class PlayerInfoUi(
    val albumUri: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val album: String
)