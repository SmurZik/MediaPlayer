package com.smurzik.mediaplayer.local.presentation

import com.smurzik.mediaplayer.local.domain.LocalTrackDomain

class LocalTrackUiMapper : LocalTrackDomain.Mapper<LocalTrackUi> {
    override fun map(title: String, author: String) = LocalTrackUi(title, author)
}