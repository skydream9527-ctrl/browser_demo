package com.litebrowse.engine

import android.content.Context
import android.content.SharedPreferences

data class SearchEngine(
    val id: String,
    val name: String,
    val urlTemplate: String,
    val iconLetter: String,
    val iconColor: Long
)

class SearchEngineManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("search_engine", Context.MODE_PRIVATE)

    val engines: List<SearchEngine> = listOf(
        SearchEngine("baidu", "百度", "https://www.baidu.com/s?wd={query}", "B", 0xFF4285F4),
        SearchEngine("sogou", "搜狗", "https://www.sogou.com/web?query={query}", "S", 0xFFFF6A00),
        SearchEngine("google", "Google", "https://www.google.com/search?q={query}", "G", 0xFFEA4335),
        SearchEngine("bing", "必应", "https://cn.bing.com/search?q={query}", "必", 0xFF00809D),
        SearchEngine("bilibili", "哔哩哔哩", "https://search.bilibili.com/all?keyword={query}", "B", 0xFFFB7299),
        SearchEngine("doubao", "豆包", "https://www.doubao.com/chat/{query}", "豆", 0xFF6C5CE7),
        SearchEngine("qianwen", "通义千问", "https://tongyi.aliyun.com/qianwen/?q={query}", "千", 0xFF615AE4),
        SearchEngine("zhihu", "知乎", "https://www.zhihu.com/search?type=content&q={query}", "知", 0xFF0084FF),
    )

    var currentEngineId: String
        get() = prefs.getString("current_engine", "baidu") ?: "baidu"
        set(value) = prefs.edit().putString("current_engine", value).apply()

    val currentEngine: SearchEngine
        get() = engines.find { it.id == currentEngineId } ?: engines[0]

    fun buildSearchUrl(query: String): String {
        return currentEngine.urlTemplate.replace("{query}", java.net.URLEncoder.encode(query, "UTF-8"))
    }
}
