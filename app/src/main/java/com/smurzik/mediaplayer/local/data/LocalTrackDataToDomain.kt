package com.smurzik.mediaplayer.local.data

import com.smurzik.mediaplayer.local.domain.LocalTrackDomain

class LocalTrackDataToDomain : LocalTrackData.Mapper<LocalTrackDomain> {
    override fun map(
        title: String,
        author: String,
        albumUri: String,
        trackUri: String,
        duration: Long
    ): LocalTrackDomain = LocalTrackDomain(title, author, albumUri, trackUri, duration)
}