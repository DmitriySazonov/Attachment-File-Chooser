package com.whenwhat.attachmentfilechooser.dialog

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemLinearMarginDecorator(val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val layoutManager = parent.layoutManager as? LinearLayoutManager ?: return
        val isLast = layoutManager.getPosition(view) == layoutManager.itemCount.dec()
        if (layoutManager.orientation == LinearLayoutManager.VERTICAL)
            applyVerticalMargin(outRect, isLast)
        else
            applyHorizontalMargin(outRect, isLast)
    }

    private fun applyVerticalMargin(outRect: Rect, isLast: Boolean) {
        outRect.apply {
            val bottom = margin.takeIf { !isLast } ?: bottom
            set(left, top, right, bottom)
        }
    }

    private fun applyHorizontalMargin(outRect: Rect, isLast: Boolean) {
        outRect.apply {
            val right = margin.takeIf { !isLast } ?: right
            set(left, top, right, bottom)
        }
    }
}