package com.smurzik.mediaplayer.local.presentation

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.smurzik.mediaplayer.local.domain.LocalTrackDomain

class MediaItemMapper : LocalTrackDomain.Mapper<MediaItem> {
    override fun map(
        title: String,
        author: String,
        albumUri: String,
        trackUri: String,
        duration: Long,
        index: Int,
        album: String
    ): MediaItem {
        return MediaItem.Builder()
            .setUri(Uri.parse(trackUri))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(author)
                    .setArtworkUri(Uri.parse(albumUri))
                    .build()
            )
            .build()
    }
}