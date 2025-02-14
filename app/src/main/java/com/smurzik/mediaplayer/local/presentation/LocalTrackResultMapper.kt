package com.smurzik.mediaplayer.local.presentation

import androidx.media3.common.MediaItem
import com.smurzik.mediaplayer.core.PlaybackServiceHelper
import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalTrackResultMapper(
    private val listLiveDataWrapper: ListLiveDataWrapper.Mutable,
    private val mapper: LocalTrackDomain.Mapper<LocalTrackUi>,
    private val musicHelper: PlaybackServiceHelper,
    private val mediaItemMapper: LocalTrackDomain.Mapper<MediaItem>
) : LocalTrackResult.Mapper<Unit> {
    override fun map(list: List<LocalTrackDomain>, errorMessage: String) {
        if (errorMessage.isEmpty()) {
            listLiveDataWrapper.update(list.map { it.map(mapper) })
            CoroutineScope(Dispatchers.Main).launch {
                musicHelper.setMediaItemList(list.map { it.map(mediaItemMapper) })
            }
        }
    }
}