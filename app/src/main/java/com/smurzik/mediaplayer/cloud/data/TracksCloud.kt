package com.smurzik.mediaplayer.cloud.data

import com.google.gson.annotations.SerializedName

data class TracksCloud(
    @SerializedName("tracks")
    val tracks: TracksData
)

data class TracksData(
    @SerializedName("data")
    val data: List<TrackContent>
)

data class TrackContent(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("preview")
    val preview: String,
    @SerializedName("artist")
    val artist: TrackArtist,
    @SerializedName("album")
    val album: TrackAlbum,
    @SerializedName("position")
    val position: Int
)

data class TrackArtist(
    @SerializedName("name")
    val name: String
)

data class TrackAlbum(
    @SerializedName("title")
    val albumTitle: String,
    @SerializedName("cover_xl")
    val albumCover: String
)