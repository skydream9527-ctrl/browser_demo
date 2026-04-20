package com.litebrowse.data.dao

import androidx.room.*
import com.litebrowse.data.entity.QuickAccess
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickAccessDao {
    @Query("SELECT * FROM quick_access ORDER BY position ASC")
    fun getAll(): Flow<List<QuickAccess>>

    @Insert
    suspend fun insert(item: QuickAccess): Long

    @Update
    suspend fun update(item: QuickAccess)

    @Delete
    suspend fun delete(item: QuickAccess)

    @Query("SELECT COUNT(*) FROM quick_access")
    suspend fun count(): Int
}
