package com.smurzik.mediaplayer.local.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

interface LocalTrackDataSource {

    suspend fun allTracks(query: String): List<LocalTrackData>

    class Base(
        private val context: Context
    ) : LocalTrackDataSource {

        override suspend fun allTracks(query: String): List<LocalTrackData> {
            val trackList = mutableListOf<LocalTrack>()

            var index = -1

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val cursor: Cursor? =
                context.contentResolver.query(uri, projection, selection, null, sortOrder)

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val title = it.getString(titleColumn)
                    val artist = it.getString(artistColumn)
                    val album = it.getString(albumColumn)
                    val duration = it.getLong(durationColumn)
                    val albumId = it.getLong(albumIdColumn)

                    val trackUri =
                        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val albumArtUri = getAlbumArtUri(albumId)

                    if (query.isNotEmpty() && title.startsWith(
                            query,
                            ignoreCase = true
                        ) || artist.startsWith(
                            query,
                            ignoreCase = true
                        )
                    ) {
                        index++
                    } else if (query.isEmpty()) {
                        index++
                    }

                    trackList.add(
                        LocalTrack(
                            id,
                            title,
                            artist,
                            album,
                            duration,
                            trackUri.toString(),
                            albumArtUri,
                            index
                        )
                    )
                }
            }
            return trackList.map {
                LocalTrackData(
                    it.title,
                    it.artist,
                    it.albumUri,
                    it.album,
                    it.uri,
                    it.duration,
                    it.index,
                    it.id
                )
            }
        }

        private fun getAlbumArtUri(albumId: Long): String {
            return ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"), albumId
            ).toString()
        }
    }
}