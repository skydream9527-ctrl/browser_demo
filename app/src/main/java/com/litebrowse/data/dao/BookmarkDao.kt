package com.litebrowse.data.dao

import androidx.room.*
import com.litebrowse.data.entity.Bookmark
import com.litebrowse.data.entity.BookmarkFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE folderId IS NULL ORDER BY createdAt DESC")
    fun getUncategorized(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE folderId = :folderId ORDER BY createdAt DESC")
    fun getByFolder(folderId: Long): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE url = :url LIMIT 1")
    suspend fun getByUrl(url: String): Bookmark?

    @Insert
    suspend fun insert(bookmark: Bookmark): Long

    @Update
    suspend fun update(bookmark: Bookmark)

    @Delete
    suspend fun delete(bookmark: Bookmark)

    @Query("SELECT * FROM bookmark_folders WHERE parentId IS NULL ORDER BY name")
    fun getRootFolders(): Flow<List<BookmarkFolder>>

    @Query("SELECT * FROM bookmark_folders WHERE parentId = :parentId ORDER BY name")
    fun getSubFolders(parentId: Long): Flow<List<BookmarkFolder>>

    @Query("SELECT COUNT(*) FROM bookmarks WHERE folderId = :folderId")
    suspend fun getBookmarkCount(folderId: Long): Int

    @Insert
    suspend fun insertFolder(folder: BookmarkFolder): Long

    @Update
    suspend fun updateFolder(folder: BookmarkFolder)

    @Delete
    suspend fun deleteFolder(folder: BookmarkFolder)
}
