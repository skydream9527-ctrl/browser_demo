package com.litebrowse.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.litebrowse.data.dao.BookmarkDao
import com.litebrowse.data.dao.HistoryDao
import com.litebrowse.data.dao.QuickAccessDao
import com.litebrowse.data.entity.Bookmark
import com.litebrowse.data.entity.BookmarkFolder
import com.litebrowse.data.entity.HistoryEntry
import com.litebrowse.data.entity.QuickAccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Bookmark::class, BookmarkFolder::class, HistoryEntry::class, QuickAccess::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun quickAccessDao(): QuickAccessDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "litebrowse.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).quickAccessDao().apply {
                                    insert(QuickAccess(title = "百度", url = "https://www.baidu.com", iconEmoji = "\uD83C\uDF10", position = 0))
                                    insert(QuickAccess(title = "B站", url = "https://www.bilibili.com", iconEmoji = "\uD83D\uDCFA", position = 1))
                                    insert(QuickAccess(title = "知乎", url = "https://www.zhihu.com", iconEmoji = "\uD83D\uDCD6", position = 2))
                                    insert(QuickAccess(title = "淘宝", url = "https://www.taobao.com", iconEmoji = "\uD83D\uDED2", position = 3))
                                    insert(QuickAccess(title = "今日头条", url = "https://www.toutiao.com", iconEmoji = "\uD83D\uDCF0", position = 4))
                                    insert(QuickAccess(title = "微博", url = "https://www.weibo.com", iconEmoji = "\uD83D\uDCE7", position = 5))
                                    insert(QuickAccess(title = "网易云", url = "https://music.163.com", iconEmoji = "\uD83C\uDFB5", position = 6))
                                }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
