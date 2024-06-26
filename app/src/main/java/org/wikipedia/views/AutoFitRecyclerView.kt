package org.akhil.nitcwiki.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.recyclerview.widget.RecyclerView
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.util.log.L.logRemoteErrorIfProd
import androidx.annotation.IntRange as AndroidIntRange

/** [RecyclerView] that invokes a callback when the number of columns should be updated.  */
open class AutoFitRecyclerView constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RecyclerView(context, attrs, defStyleAttr) {

    interface Callback {
        fun onColumns(columns: Int)
    }

    private val minColumnWidth = context.obtainStyledAttributes(attrs, R.styleable.AutoFitRecyclerView, defStyleAttr, 0).use {
        it.getDimensionPixelSize(R.styleable.AutoFitRecyclerView_minColumnWidth, 0)
    }
    @AndroidIntRange(from = MIN_COLUMNS.toLong())
    var columns = MIN_COLUMNS
    var callback: Callback = DefaultCallback()

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        val cols = calculateColumns(minColumnWidth, measuredWidth)
        if (columns != cols) {
            columns = cols
            callback.onColumns(columns)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // https://issuetracker.google.com/issues/37034096
        // TODO: check again in August 2024
        try {
            super.onLayout(changed, l, t, r, b)
        } catch (e: Exception) {
            logRemoteErrorIfProd(e)
        }
    }

    private fun calculateColumns(columnWidth: Int, availableWidth: Int): Int {
        return if (columnWidth > 0) MIN_COLUMNS.coerceAtLeast(availableWidth / columnWidth) else MIN_COLUMNS
    }

    private class DefaultCallback : Callback {
        override fun onColumns(columns: Int) {}
    }

    companion object {
        private const val MIN_COLUMNS = 1
    }
}
