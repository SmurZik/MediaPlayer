package com.smurzik.mediaplayer.login.data.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class UserCache(
    @PrimaryKey @ColumnInfo(name = "login") val login: String,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "password") val password: String
)