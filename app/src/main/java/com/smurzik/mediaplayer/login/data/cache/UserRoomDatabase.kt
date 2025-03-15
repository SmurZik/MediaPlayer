package com.smurzik.mediaplayer.login.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserCache::class], version = 1)
abstract class UserRoomDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}