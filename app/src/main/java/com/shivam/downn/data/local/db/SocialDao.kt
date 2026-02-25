package com.shivam.downn.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {
    @Query("SELECT * FROM social_activities ORDER BY scheduledTime DESC")
    fun getFeedActivities(): Flow<List<CachedSocial>>
    
    @JvmSuppressWildcards
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<CachedSocial>): List<Long>
    
    @JvmSuppressWildcards
    @Query("DELETE FROM social_activities")
    suspend fun clearFeed(): Int
}
