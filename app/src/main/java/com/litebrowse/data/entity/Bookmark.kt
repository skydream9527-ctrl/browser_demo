package com.litebrowse.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    foreignKeys = [ForeignKey(
        entity = BookmarkFolder::class,
        parentColumns = ["id"],
        childColumns = ["folderId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("folderId")]
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val favicon: String? = null,
    val folderId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
