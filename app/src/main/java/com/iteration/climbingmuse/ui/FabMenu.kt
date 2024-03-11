package com.iteration.climbingmuse.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.iteration.climbingmuse.R

// This is based on LinearLayout: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/android/widget/LinearLayout.java
// and inspired by FloatingActionButton by Clans: https://github.com/Clans/FloatingActionButton/tree/master
class FabMenu : LinearLayout {

    val mainFab: FloatingActionButton = FloatingActionButton(context)

    // Colors used for the main button
    private val primaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_primary, context.theme)
    private val onPrimaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_onPrimary, context.theme)
    // Colors used for the sub buttons
    private val secondaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_secondary, context.theme)
    private val onSecondaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_onSecondary, context.theme)


    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : super(context, attrs, defStyleAttr) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.FabMenu, defStyleAttr, defStyleRes)
        saveAttributeDataForStyleable(context, R.styleable.FabMenu, attrs, attributes, defStyleAttr, defStyleRes)

        val direction = attributes.getInt(R.styleable.FabMenu_direction, 0)

        val mainIconId = attributes.getResourceId(R.styleable.FabMenu_fab_icon, R.drawable.baseline_fiber_manual_record_24)
        setupMainFab(mainIconId)
    }

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
    }

    private fun setupMainFab(mainIconId: Int) {
        mainFab.apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            mainFab.size = FloatingActionButton.SIZE_NORMAL // TODO: might need to change this size?
            // TODO set color, icon
            mainFab.backgroundTintList = primaryColor
            mainFab.imageTintList = onPrimaryColor
            mainFab.setImageDrawable(AppCompatResources.getDrawable(context, mainIconId))
        }



        setMainButtonAnimation()

        addView(mainFab)
    }

    private fun setMainButtonAnimation() {
        val collapseAngle: Float
        val expandAngle: Float
//        if (mOpenDirection == OPEN_UP) {
//            collapseAngle = if (mLabelsPosition == LABELS_POSITION_LEFT) OPENED_PLUS_ROTATION_LEFT else OPENED_PLUS_ROTATION_RIGHT
//            expandAngle = if (mLabelsPosition == LABELS_POSITION_LEFT) OPENED_PLUS_ROTATION_LEFT else OPENED_PLUS_ROTATION_RIGHT
//        } else {
//            collapseAngle = if (mLabelsPosition == LABELS_POSITION_LEFT) OPENED_PLUS_ROTATION_RIGHT else OPENED_PLUS_ROTATION_LEFT
//            expandAngle = if (mLabelsPosition == LABELS_POSITION_LEFT) OPENED_PLUS_ROTATION_RIGHT else OPENED_PLUS_ROTATION_LEFT
//        }

//        val collapseAnimator = ObjectAnimator.ofFloat(mImageToggle, "rotation", collapseAngle, CLOSED_PLUS_ROTATION)
//        val expandAnimator = ObjectAnimator.ofFloat(mImageToggle, "rotation", CLOSED_PLUS_ROTATION, expandAngle)
//
//        mOpenAnimatorSet.play(expandAnimator)
//        mCloseAnimatorSet.play(collapseAnimator)
//
//        mOpenAnimatorSet.setInterpolator(mOpenInterpolator)
//        mCloseAnimatorSet.setInterpolator(mCloseInterpolator)
//
//        mOpenAnimatorSet.setDuration(ANIMATION_DURATION)
//        mCloseAnimatorSet.setDuration(ANIMATION_DURATION)
    }

    override fun onLayout(sizeChanged: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount == 0) {
            // Do not re-layout when there are no children.
            return
        }

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (isInEditMode) {
                child.visibility = View.VISIBLE
            }
        }
        super.onLayout(sizeChanged, left, top, right, bottom)
    }



    companion object {
        private const val OPENED_PLUS_ROTATION_LEFT = -90f - 45f
        private const val OPENED_PLUS_ROTATION_RIGHT = 90f + 45f
        private const val CLOSED_PLUS_ROTATION = 0f
        private const val ANIMATION_DURATION = 300
        private const val OPEN_UP = 0
        private const val OPEN_DOWN = 1
        private const val LABELS_POSITION_LEFT = 0
        private const val LABELS_POSITION_RIGHT = 1
    }
}