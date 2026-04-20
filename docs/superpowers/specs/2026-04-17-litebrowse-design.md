# LiteBrowse — 极简 Android 浏览器设计文档

**日期:** 2026-04-17
**状态:** 已确认

---

## 1. 概述

LiteBrowse 是一款基于 Mozilla GeckoView 引擎的极简 Android 浏览器，采用 Material Design 3 (Material You) 动态配色风格。具备多搜索引擎切换、标签页管理、收藏夹、历史记录、视频播放等核心浏览器功能。

项目通过 GitHub Actions 进行 CI/CD 打包，不依赖本地 Android 开发环境。

## 2. 技术选型

| 项目 | 选择 |
|------|------|
| 渲染引擎 | Mozilla GeckoView (最新稳定版) |
| 语言 | Kotlin 1.9+ |
| UI 框架 | XML Layout + Material Design 3 |
| 数据持久化 | Room Database |
| 依赖注入 | 无（手动构造，保持极简） |
| 异步 | Kotlin Coroutines |
| minSdk | 24 (Android 7.0+) |
| targetSdk | 34 (Android 14) |
| CI/CD | GitHub Actions |

## 3. 架构分层

```
┌─────────────────────────────────────────┐
│            UI Layer (Material 3)        │
│  MainActivity · HomeFragment            │
│  BrowserFragment · TabManagerFragment   │
│  BookmarkFragment · HistoryFragment     │
│  SettingsFragment                       │
├─────────────────────────────────────────┤
│           ViewModel Layer               │
│  BrowserViewModel · TabViewModel        │
│  BookmarkViewModel · SearchEngineVM     │
├─────────────────────────────────────────┤
│       Domain / Repository Layer         │
│  TabManager · BookmarkRepository        │
│  HistoryRepository · SearchEngineManager│
├──────────────────┬──────────────────────┤
│  GeckoView Engine│   Room Database      │
│  GeckoRuntime    │   BookmarkDao        │
│  GeckoSession    │   HistoryDao         │
│  页面渲染/JS执行  │   TabDao/SettingsDao │
└──────────────────┴──────────────────────┘
```

## 4. 首页设计 (HomeFragment)

### 布局结构（从上到下）

1. **搜索栏** — 居中大搜索框，左侧显示当前搜索引擎图标（点击弹出引擎选择面板）
2. **引擎 chips** — 搜索框下方横向滚动标签，可快速切换搜索引擎
3. **快捷方块** — 4×2 网格，预设常用网站 + "添加"按钮，长按可编辑/删除
4. **底部导航栏** — 首页 | 后退 | 标签数量 | 前进 | 更多菜单

### 预设快捷方块

百度、B站、知乎、淘宝、今日头条、微博、网易云音乐、+ 添加

## 5. 搜索引擎模块

### 支持的引擎及 URL 模板

| 引擎 | URL 模板 |
|------|---------|
| 百度 | `https://www.baidu.com/s?wd={query}` |
| 搜狗 | `https://www.sogou.com/web?query={query}` |
| Google | `https://www.google.com/search?q={query}` |
| 必应 | `https://cn.bing.com/search?q={query}` |
| 哔哩哔哩 | `https://search.bilibili.com/all?keyword={query}` |
| 豆包 | `https://www.doubao.com/chat/{query}` |
| 通义千问 | `https://tongyi.aliyun.com/qianwen/?q={query}` |
| 知乎 | `https://www.zhihu.com/search?type=content&q={query}` |

### 切换交互

- **搜索框左侧图标** — 显示当前引擎 logo，点击弹出 BottomSheet 引擎选择列表
- **引擎选择面板** — 列表形式，每项显示引擎图标、名称、域名，当前选中项高亮打勾
- **记忆选择** — 用户选择的引擎通过 SharedPreferences 持久化

## 6. 浏览器页面 (BrowserFragment)

### 地址栏

- 显示 SSL 锁图标 + 域名（简化显示）
- 点击展开完整 URL，可编辑
- 右侧刷新/停止按钮

### GeckoView 集成

- 每个标签对应一个 GeckoSession
- 支持全屏视频播放（通过 GeckoView fullscreen callback）
- 支持文件下载（通过 GeckoView download delegate）
- 支持页内查找（GeckoSession.Finder）

### 底部导航栏

- 首页（返回 HomeFragment）
- 后退（GeckoSession.goBack）
- 标签数量指示器（点击进入 TabManagerFragment）
- 前进（GeckoSession.goForward）
- 更多菜单（弹出 BottomSheet）

## 7. 标签页管理 (TabManagerFragment)

### 布局

- 顶部标题 + "关闭全部" + "新建标签"按钮
- 普通标签 / 无痕标签切换 chips
- 卡片网格（2列），每张卡片包含：
  - 页面缩略图预览
  - 页面标题（单行省略）
  - 域名 + 关闭按钮

### 交互

- 点击卡片切换到该标签
- 点击 ✕ 关闭单个标签
- 左滑关闭标签
- 当前活动标签卡片高亮边框

### 无痕模式

- 独立的 GeckoSession 配置（不保存历史/Cookie）
- 标签页管理中独立分组显示
- 关闭时自动清除所有会话数据

## 8. 收藏夹 (BookmarkFragment)

### 数据结构

```kotlin
@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val favicon: String?,       // favicon URL
    val folderId: Long? = null, // null = 未分类
    val createdAt: Long
)

@Entity
data class BookmarkFolder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val parentId: Long? = null, // 支持嵌套
    val createdAt: Long
)
```

### 功能

- 文件夹分类（支持嵌套）
- 新建文件夹
- 添加/删除收藏
- 长按编辑（修改标题/URL/移动到其他文件夹）
- 浏览页面通过更多菜单中的 ⭐ 一键收藏

## 9. 历史记录 (HistoryFragment)

### 数据结构

```kotlin
@Entity
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val visitedAt: Long
)
```

### 功能

- 按日期分组显示（今天、昨天、更早）
- 搜索历史记录
- 删除单条记录
- 清除全部历史

## 10. 更多菜单 (BottomSheet)

从底部弹出，4×2 功能网格 + 开关项：

**功能网格：**
- ⭐ 收藏当前页
- 📋 收藏夹
- 🕐 历史记录
- ⬇ 下载管理
- 🔗 分享
- 🔍 页内查找
- 🖥 桌面模式（切换 User-Agent）
- ⚙ 设置

**开关项：**
- 无痕模式开关
- 深色模式开关

## 11. 设置页 (SettingsFragment)

- 默认搜索引擎选择
- 首页快捷方块管理
- 清除浏览数据（历史/缓存/Cookie）
- 广告拦截开关（GeckoView content blocking）
- 字体大小调整
- 下载路径设置
- 关于页面（版本号等）

## 12. 项目结构

```
app/
├── src/main/
│   ├── java/com/litebrowse/
│   │   ├── LiteBrowseApp.kt              # Application 类，初始化 GeckoRuntime
│   │   ├── MainActivity.kt               # 单 Activity
│   │   ├── ui/
│   │   │   ├── home/
│   │   │   │   ├── HomeFragment.kt
│   │   │   │   └── QuickAccessAdapter.kt
│   │   │   ├── browser/
│   │   │   │   ├── BrowserFragment.kt
│   │   │   │   └── FullscreenHelper.kt
│   │   │   ├── tabs/
│   │   │   │   ├── TabManagerFragment.kt
│   │   │   │   └── TabCardAdapter.kt
│   │   │   ├── bookmark/
│   │   │   │   ├── BookmarkFragment.kt
│   │   │   │   └── BookmarkAdapter.kt
│   │   │   ├── history/
│   │   │   │   ├── HistoryFragment.kt
│   │   │   │   └── HistoryAdapter.kt
│   │   │   ├── menu/
│   │   │   │   └── MoreMenuSheet.kt
│   │   │   └── settings/
│   │   │       └── SettingsFragment.kt
│   │   ├── viewmodel/
│   │   │   ├── BrowserViewModel.kt
│   │   │   ├── TabViewModel.kt
│   │   │   ├── BookmarkViewModel.kt
│   │   │   └── SearchEngineViewModel.kt
│   │   ├── data/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── BookmarkDao.kt
│   │   │   │   ├── HistoryDao.kt
│   │   │   │   └── TabDao.kt
│   │   │   ├── entity/
│   │   │   │   ├── Bookmark.kt
│   │   │   │   ├── BookmarkFolder.kt
│   │   │   │   ├── HistoryEntry.kt
│   │   │   │   └── TabInfo.kt
│   │   │   └── repository/
│   │   │       ├── BookmarkRepository.kt
│   │   │       └── HistoryRepository.kt
│   │   ├── engine/
│   │   │   ├── GeckoEngineManager.kt     # GeckoRuntime 管理
│   │   │   ├── TabManager.kt             # GeckoSession 生命周期
│   │   │   └── SearchEngineManager.kt    # 搜索引擎配置
│   │   └── util/
│   │       ├── UrlUtils.kt
│   │       └── Extensions.kt
│   └── res/
│       ├── layout/
│       ├── values/
│       ├── values-night/
│       ├── drawable/
│       └── navigation/
├── build.gradle.kts
└── proguard-rules.pro

build.gradle.kts                          # 项目级
settings.gradle.kts
gradle.properties
.github/
└── workflows/
    └── build.yml                         # GitHub Actions CI
```

## 13. GitHub Actions CI 配置

```yaml
# 关键步骤
- JDK 17 setup
- Gradle cache
- GeckoView AAR 自动通过 Maven 依赖解析（无需手动下载）
- assembleRelease
- APK artifact upload
```

GeckoView 通过 Mozilla Maven 仓库 (`maven.mozilla.org/maven2`) 的 `org.mozilla.geckoview:geckoview-omni` 依赖自动下载，需在项目级 `build.gradle.kts` 中添加 Mozilla Maven 仓库。CI 无需额外手动配置。

## 14. 主题与样式

- Material Design 3 动态配色 (DynamicColors)
- 支持亮色/暗色主题自动跟随系统
- 圆角卡片 (12dp radius)
- 底部导航栏固定
- BottomSheet 采用 Material 3 样式

## 15. 权限

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 16. 范围外（不在 V1 实现）

- 账号同步
- 扩展/插件系统
- 阅读模式
- 广告拦截自定义规则
- 多语言国际化（V1 仅中文）
- Widget
