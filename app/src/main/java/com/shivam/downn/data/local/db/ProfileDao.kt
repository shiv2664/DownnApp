package com.shivam.downn.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles")
    fun getAllProfiles(): Flow<List<CachedProfile>>
    
    @JvmSuppressWildcards
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<CachedProfile>): List<Long>
    
    @JvmSuppressWildcards
    @Query("DELETE FROM profiles")
    suspend fun clearAll(): Int
}
