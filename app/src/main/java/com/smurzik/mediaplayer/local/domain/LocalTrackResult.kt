package com.smurzik.mediaplayer.local.domain

sealed class LocalTrackResult {

    interface Mapper<T> {
        fun map(list: List<LocalTrackDomain>, errorMessage: String): T

        fun map(
            list: List<LocalTrackDomain>,
            errorMessage: String,
            currentTrackIndex: Int,
            progress: Long
        ): T
    }

    abstract fun <T> map(mapper: Mapper<T>): T

    abstract fun <T> map(mapper: Mapper<T>, currentTrackIndex: Int, progress: Long): T

    data class Success(private val list: List<LocalTrackDomain>) : LocalTrackResult() {
        override fun <T> map(mapper: Mapper<T>): T = mapper.map(list, "")
        override fun <T> map(mapper: Mapper<T>, currentTrackIndex: Int, progress: Long): T =
            mapper.map(list, "", currentTrackIndex, progress)
    }


    data class Failure(private val message: String) : LocalTrackResult() {
        override fun <T> map(mapper: Mapper<T>): T = mapper.map(emptyList(), message)
        override fun <T> map(mapper: Mapper<T>, currentTrackIndex: Int, progress: Long): T =
            mapper.map(emptyList(), message, currentTrackIndex, progress)
    }
}
