package com.litebrowse.engine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TabManager(private val engineManager: GeckoEngineManager) {

    private val _tabs = MutableLiveData<List<TabInfo>>(emptyList())
    val tabs: LiveData<List<TabInfo>> = _tabs

    private val _currentTab = MutableLiveData<TabInfo?>()
    val currentTab: LiveData<TabInfo?> = _currentTab

    fun addTab(url: String? = null, isPrivate: Boolean = false): TabInfo {
        val session = engineManager.createSession(isPrivate)
        val tab = TabInfo(session = session, isPrivate = isPrivate)

        val currentList = _tabs.value.orEmpty().toMutableList()
        currentList.add(tab)
        _tabs.value = currentList
        _currentTab.value = tab

        if (url != null) {
            session.loadUri(url)
        }

        return tab
    }

    fun switchTo(tab: TabInfo) {
        _currentTab.value = tab
    }

    fun closeTab(tab: TabInfo) {
        engineManager.closeSession(tab.session)
        val currentList = _tabs.value.orEmpty().toMutableList()
        currentList.remove(tab)
        _tabs.value = currentList

        if (_currentTab.value == tab) {
            _currentTab.value = currentList.lastOrNull()
        }
    }

    fun closeAllTabs(privateOnly: Boolean = false) {
        val currentList = _tabs.value.orEmpty()
        val toClose = if (privateOnly) currentList.filter { it.isPrivate } else currentList
        toClose.forEach { engineManager.closeSession(it.session) }

        _tabs.value = if (privateOnly) currentList.filter { !it.isPrivate } else emptyList()

        val current = _currentTab.value
        if (current != null && toClose.contains(current)) {
            _currentTab.value = _tabs.value?.lastOrNull()
        }
    }

    val normalTabCount: Int
        get() = _tabs.value.orEmpty().count { !it.isPrivate }

    val privateTabCount: Int
        get() = _tabs.value.orEmpty().count { it.isPrivate }

    val totalTabCount: Int
        get() = _tabs.value.orEmpty().size
}
