package com.litebrowse.data.repository

import com.litebrowse.data.dao.HistoryDao
import com.litebrowse.data.entity.HistoryEntry
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: HistoryDao) {
    fun getAll(): Flow<List<HistoryEntry>> = dao.getAll()
    fun search(query: String): Flow<List<HistoryEntry>> = dao.search(query)
    suspend fun insert(entry: HistoryEntry): Long = dao.insert(entry)
    suspend fun delete(entry: HistoryEntry) = dao.delete(entry)
    suspend fun deleteAll() = dao.deleteAll()
}
