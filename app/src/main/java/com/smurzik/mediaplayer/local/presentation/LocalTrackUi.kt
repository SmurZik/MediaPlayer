package com.smurzik.mediaplayer.local.presentation

data class LocalTrackUi(
    private val title: String, private val author: String
) {

    interface Mapper<T> {
        fun map(title: String, author: String): T
    }

    fun <T> map(mapper: Mapper<T>): T = mapper.map(title, author)
}