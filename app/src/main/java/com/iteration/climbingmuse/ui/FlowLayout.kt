package com.iteration.climbingmuse.ui

/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.iteration.climbingmuse.R
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat


/**
 * Horizontally lay out children until the row is filled and then moved to the next line. Call
 * [FlowLayout.setSingleLine] to disable reflow and lay all children out in one line.
 *
 * @hide
 */
class FlowLayout : ViewGroup {
    protected var lineSpacing = 0
    protected var itemSpacing = 0
    /** Returns whether this chip group is single line or reflowed multiline.  */
    /** Sets whether this chip group is single line, or reflowed multiline.  */
    var isSingleLine: Boolean
    protected var rowCount = 0
        private set

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        isSingleLine = false
        loadFromAttributes(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        isSingleLine = false
        loadFromAttributes(context, attrs)
    }

    private fun loadFromAttributes(context: Context, attrs: AttributeSet?) {
        val array = context.theme.obtainStyledAttributes(attrs, com.google.android.material.R.styleable.FlowLayout, 0, 0)
        lineSpacing = array.getDimensionPixelSize(com.google.android.material.R.styleable.FlowLayout_lineSpacing, 0)
        itemSpacing = array.getDimensionPixelSize(com.google.android.material.R.styleable.FlowLayout_itemSpacing, 0)
        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val maxWidth =
            if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) width else Int.MAX_VALUE
        var childLeft = paddingLeft
        var childTop = paddingTop
        var childBottom = childTop
        var childRight = childLeft
        var maxChildRight = 0
        val maxRight = maxWidth - paddingRight
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams
            var leftMargin = 0
            var rightMargin = 0
            if (lp is MarginLayoutParams) {
                val marginLp = lp
                leftMargin += marginLp.leftMargin
                rightMargin += marginLp.rightMargin
            }
            childRight = childLeft + leftMargin + child.measuredWidth

            // If the current child's right bound exceeds Flowlayout's max right bound and flowlayout is
            // not confined to a single line, move this child to the next line and reset its left bound to
            // flowlayout's left bound.
            if (childRight > maxRight && !isSingleLine) {
                childLeft = paddingLeft
                childTop = childBottom + lineSpacing
            }
            childRight = childLeft + leftMargin + child.measuredWidth
            childBottom = childTop + child.measuredHeight

            // Updates Flowlayout's max right bound if current child's right bound exceeds it.
            if (childRight > maxChildRight) {
                maxChildRight = childRight
            }
            childLeft += leftMargin + rightMargin + child.measuredWidth + itemSpacing

            // For all preceding children, the child's right margin is taken into account in the next
            // child's left bound (childLeft). However, childLeft is ignored after the last child so the
            // last child's right margin needs to be explicitly added to Flowlayout's max right bound.
            if (i == childCount - 1) {
                maxChildRight += rightMargin
            }
        }
        maxChildRight += paddingRight
        childBottom += paddingBottom
        val finalWidth = getMeasuredDimension(width, widthMode, maxChildRight)
        val finalHeight = getMeasuredDimension(height, heightMode, childBottom)
        setMeasuredDimension(finalWidth, finalHeight)
    }

    override fun onLayout(sizeChanged: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount == 0) {
            // Do not re-layout when there are no children.
            rowCount = 0
            return
        }
        rowCount = 1
        val isRtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
        val paddingStart = if (isRtl) paddingRight else paddingLeft
        val paddingEnd = if (isRtl) paddingLeft else paddingRight
        var childStart = paddingStart
        var childTop = paddingTop
        var childBottom = childTop
        var childEnd: Int
        val maxChildEnd = right - left - paddingEnd
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                child.setTag(com.google.android.material.R.id.row_index_key, -1)
                continue
            }
            val lp = child.layoutParams
            var startMargin = 0
            var endMargin = 0
            if (lp is MarginLayoutParams) {
                val marginLp = lp
                startMargin = MarginLayoutParamsCompat.getMarginStart(marginLp)
                endMargin = MarginLayoutParamsCompat.getMarginEnd(marginLp)
            }
            childEnd = childStart + startMargin + child.measuredWidth
            if (!isSingleLine && childEnd > maxChildEnd) {
                childStart = paddingStart
                childTop = childBottom + lineSpacing
                rowCount++
            }
            child.setTag(com.google.android.material.R.id.row_index_key, rowCount - 1)
            childEnd = childStart + startMargin + child.measuredWidth
            childBottom = childTop + child.measuredHeight
            if (isRtl) {
                child.layout(
                    maxChildEnd - childEnd,
                    childTop,
                    maxChildEnd - childStart - startMargin,
                    childBottom
                )
            } else {
                child.layout(childStart + startMargin, childTop, childEnd, childBottom)
            }
            childStart += startMargin + endMargin + child.measuredWidth + itemSpacing
        }
    }

    /** Gets the row index of the child, primarily for accessibility.    */
    fun getRowIndex(child: View): Int {
        val index = child.getTag(com.google.android.material.R.id.row_index_key)
        return if (index !is Int) {
            -1
        } else index
    }

    companion object {
        private fun getMeasuredDimension(size: Int, mode: Int, childrenEdge: Int): Int {
            return when (mode) {
                MeasureSpec.EXACTLY -> size
                MeasureSpec.AT_MOST -> Math.min(childrenEdge, size)
                else -> childrenEdge
            }
        }
    }
}

