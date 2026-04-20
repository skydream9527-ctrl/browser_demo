package com.litebrowse.data.dao

import androidx.room.*
import com.litebrowse.data.entity.HistoryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY visitedAt DESC")
    fun getAll(): Flow<List<HistoryEntry>>

    @Query("SELECT * FROM history WHERE title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%' ORDER BY visitedAt DESC")
    fun search(query: String): Flow<List<HistoryEntry>>

    @Insert
    suspend fun insert(entry: HistoryEntry): Long

    @Delete
    suspend fun delete(entry: HistoryEntry)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}
