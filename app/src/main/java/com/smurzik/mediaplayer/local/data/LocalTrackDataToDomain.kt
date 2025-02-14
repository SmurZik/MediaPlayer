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

class LocalTrackDataToQuery : LocalTrackData.Mapper<Pair<String, String>> {
    override fun map(
        title: String,
        author: String,
        albumUri: String,
        trackUri: String,
        duration: Long
    ) = Pair(title, author)
}
