package com.smurzik.mediaplayer.local.data

import com.smurzik.mediaplayer.local.domain.LocalTrackDomain

class LocalTrackDataToDomain : LocalTrackData.Mapper<LocalTrackDomain> {
    override fun map(
        title: String,
        author: String,
        albumUri: String,
        trackUri: String,
        duration: Long,
        index: Int
    ): LocalTrackDomain = LocalTrackDomain(title, author, albumUri, trackUri, duration, index)
}

class LocalTrackDataToQuery : LocalTrackData.Mapper<Pair<String, String>> {
    override fun map(
        title: String,
        author: String,
        albumUri: String,
        trackUri: String,
        duration: Long,
        index: Int
    ) = Pair(title, author)
}
