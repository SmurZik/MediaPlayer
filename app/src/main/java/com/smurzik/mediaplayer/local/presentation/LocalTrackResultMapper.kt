package com.smurzik.mediaplayer.local.presentation

import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackResult

class LocalTrackResultMapper(
    private val listLiveDataWrapper: ListLiveDataWrapper.Mutable,
    private val mapper: LocalTrackDomain.Mapper<LocalTrackUi>
) : LocalTrackResult.Mapper<Unit> {
    override fun map(list: List<LocalTrackDomain>, errorMessage: String) {
        if (errorMessage.isEmpty()) {
            listLiveDataWrapper.update(list.map { it.map(mapper) })
        }
    }
}