package com.cmk.app.listener

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

/**
 * Created by cuimingkun
 * on 2020/3/26 09:30
 */

/**
 * Add a DSL listener to simplify [TextView.addTextChangedListener]
 */
inline fun TextView.textWatcher(watcher: DslTextWatcher.() -> Unit) =
    addTextChangedListener(DslTextWatcher().apply(watcher))

class DslTextWatcher : TextWatcher {

    private var afterTextChanged: ((Editable?) -> Unit)? = null
    private var beforeTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null
    private var onTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null

    fun afterTextChanged(listener: ((Editable?) -> Unit)) {
        afterTextChanged = listener
    }

    fun beforeTextChanged(listener: (CharSequence?, Int, Int, Int) -> Unit) {
        beforeTextChanged = listener
    }

    fun onTextChanged(listener: (CharSequence?, Int, Int, Int) -> Unit) {
        onTextChanged = listener
    }

    override fun afterTextChanged(s: Editable?) {
        afterTextChanged?.invoke(s)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        beforeTextChanged?.invoke(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChanged?.invoke(s, start, before, count)
    }
}

/**
 *  Add a DSL listener to simplify [TabLayout.addOnTabSelectedListener]
 */
inline fun TabLayout.onTabSelectedListener(listener: DslOnTabSelectedListener.() -> Unit) =
    addOnTabSelectedListener(DslOnTabSelectedListener().apply(listener))

class DslOnTabSelectedListener : TabLayout.OnTabSelectedListener {

    private var onTabReselected: ((TabLayout.Tab?) -> Unit)? = null
    private var onTabUnselected: ((TabLayout.Tab?) -> Unit)? = null
    private var onTabSelected: ((TabLayout.Tab?) -> Unit)? = null

    fun onTabReselected(listener: (TabLayout.Tab?) -> Unit) {
        onTabReselected = listener
    }

    fun onTabUnselected(listener: (TabLayout.Tab?) -> Unit) {
        onTabUnselected = listener
    }

    fun onTabSelected(listener: (TabLayout.Tab?) -> Unit) {
        onTabSelected = listener
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        onTabReselected?.invoke(tab)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        onTabUnselected?.invoke(tab)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        onTabSelected?.invoke(tab)
    }
}

/**
 *  Add a DSL listener to simplify [RecyclerView.addOnScrollListener]
 */
inline fun RecyclerView.onScrollListener(listener: DslOnScrollListener.() -> Unit) =
    addOnScrollListener(DslOnScrollListener().apply(listener))

class DslOnScrollListener : RecyclerView.OnScrollListener() {

    private var onScrollStateChanged: ((recyclerView: RecyclerView, newState: Int) -> Unit)? = null
    private var onScrolled: ((recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit)? = null

    fun onScrollStateChanged(listener: (recyclerView: RecyclerView, newState: Int) -> Unit) {
        onScrollStateChanged = listener
    }

    fun onScrolled(listener: (recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit) {
        onScrolled = listener
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        onScrollStateChanged?.invoke(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        onScrolled?.invoke(recyclerView, dx, dy)
    }
}