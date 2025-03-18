package com.smurzik.mediaplayer.player.presentation

import com.smurzik.mediaplayer.local.domain.LocalTrackDomain

class PlayerInfoUiMapper : LocalTrackDomain.Mapper<PlayerInfoUi> {
    override fun map(
        title: String,
        author: String,
        albumUri: String,
        trackUri: String,
        duration: Long,
        index: Int,
        album: String,
        id: Long,
        isFavorite: Boolean
    ): PlayerInfoUi {
        return PlayerInfoUi(albumUri, title, author, duration, album)
    }
}