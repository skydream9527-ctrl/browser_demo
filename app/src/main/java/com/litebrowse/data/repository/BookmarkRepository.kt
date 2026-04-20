package com.litebrowse.data.repository

import com.litebrowse.data.dao.BookmarkDao
import com.litebrowse.data.entity.Bookmark
import com.litebrowse.data.entity.BookmarkFolder
import kotlinx.coroutines.flow.Flow

class BookmarkRepository(private val dao: BookmarkDao) {
    fun getUncategorized(): Flow<List<Bookmark>> = dao.getUncategorized()
    fun getByFolder(folderId: Long): Flow<List<Bookmark>> = dao.getByFolder(folderId)
    suspend fun getByUrl(url: String): Bookmark? = dao.getByUrl(url)
    suspend fun insert(bookmark: Bookmark): Long = dao.insert(bookmark)
    suspend fun update(bookmark: Bookmark) = dao.update(bookmark)
    suspend fun delete(bookmark: Bookmark) = dao.delete(bookmark)
    fun getRootFolders(): Flow<List<BookmarkFolder>> = dao.getRootFolders()
    fun getSubFolders(parentId: Long): Flow<List<BookmarkFolder>> = dao.getSubFolders(parentId)
    suspend fun getBookmarkCount(folderId: Long): Int = dao.getBookmarkCount(folderId)
    suspend fun insertFolder(folder: BookmarkFolder): Long = dao.insertFolder(folder)
    suspend fun updateFolder(folder: BookmarkFolder) = dao.updateFolder(folder)
    suspend fun deleteFolder(folder: BookmarkFolder) = dao.deleteFolder(folder)
}
