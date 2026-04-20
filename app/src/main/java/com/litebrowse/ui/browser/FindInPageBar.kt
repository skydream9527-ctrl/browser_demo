package com.litebrowse.ui.browser

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import org.mozilla.geckoview.GeckoSession

class FindInPageBar(
    private val container: View,
    private val input: EditText,
    private val upButton: ImageButton,
    private val downButton: ImageButton,
    private val closeButton: ImageButton,
    private val session: GeckoSession
) {
    private val finder = session.finder

    init {
        finder.displayFlags = GeckoSession.FINDER_DISPLAY_HIGHLIGHT_ALL or
            GeckoSession.FINDER_DISPLAY_DIM_PAGE

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: return
                if (query.isNotEmpty()) {
                    finder.find(query, 0)
                }
            }
        })

        downButton.setOnClickListener { finder.find(null, 0) }
        upButton.setOnClickListener { finder.find(null, GeckoSession.FINDER_FIND_BACKWARDS) }
        closeButton.setOnClickListener { hide() }
    }

    fun show() {
        container.visibility = View.VISIBLE
        input.requestFocus()
    }

    fun hide() {
        container.visibility = View.GONE
        input.text.clear()
        finder.clear()
    }
}
