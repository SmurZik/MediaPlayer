package com.smurzik.mediaplayer.local.domain

sealed class LocalTrackResult {

    interface Mapper<T> {
        fun map(list: List<LocalTrackDomain>, errorMessage: String): T
    }

    abstract fun <T> map(mapper: Mapper<T>): T

    data class Success(private val list: List<LocalTrackDomain>) : LocalTrackResult() {
        override fun <T> map(mapper: Mapper<T>): T = mapper.map(list, "")
    }

    data class Failure(private val message: String) : LocalTrackResult() {
        override fun <T> map(mapper: Mapper<T>): T = mapper.map(emptyList(), message)
    }
}