package com.smurzik.mediaplayer.local.presentation

import android.view.View
import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackResult

class LocalTrackResultMapper(
    private val listLiveDataWrapper: ListLiveDataWrapper.Mutable,
    private val showErrorLiveDataWrapper: ShowErrorLiveDataWrapper.Mutable,
    private val mapper: LocalTrackDomain.Mapper<LocalTrackUi>,
) : LocalTrackResult.Mapper<Unit> {
    override fun map(list: List<LocalTrackDomain>, errorMessage: String) {
        if (errorMessage.isEmpty()) {
            listLiveDataWrapper.update(list.map { it.map(mapper) })
            showErrorLiveDataWrapper.update(View.GONE)
        } else {
            showErrorLiveDataWrapper.update(View.VISIBLE)
        }
    }

    override fun map(
        list: List<LocalTrackDomain>,
        errorMessage: String,
        currentTrackIndex: Int,
        progress: Long
    ) = Unit
}

class LocalTrackQueryMapper(
    private val listLiveDataWrapper: ListLiveDataWrapper.Mutable,
    private val mapper: LocalTrackDomain.Mapper<LocalTrackUi>,
    private val showErrorLiveDataWrapper: ShowErrorLiveDataWrapper.Mutable
) : LocalTrackResult.Mapper<Unit> {
    override fun map(list: List<LocalTrackDomain>, errorMessage: String) {
        if (errorMessage.isEmpty()) {
            listLiveDataWrapper.update(list.map { it.map(mapper) })
            showErrorLiveDataWrapper.update(View.GONE)
        } else {
            showErrorLiveDataWrapper.update(View.VISIBLE)
        }
    }

    override fun map(
        list: List<LocalTrackDomain>,
        errorMessage: String,
        currentTrackIndex: Int,
        progress: Long
    ) = Unit
}