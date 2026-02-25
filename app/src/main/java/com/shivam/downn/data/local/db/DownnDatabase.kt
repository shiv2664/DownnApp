package com.shivam.downn.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CachedProfile::class, CachedSocial::class], version = 1, exportSchema = false)
abstract class DownnDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun socialDao(): SocialDao
}
