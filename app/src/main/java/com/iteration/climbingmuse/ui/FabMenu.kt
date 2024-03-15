package com.iteration.climbingmuse.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.iteration.climbingmuse.R
import timber.log.Timber

// This is based on LinearLayout: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/android/widget/LinearLayout.java
// and inspired by FloatingActionButton by Clans: https://github.com/Clans/FloatingActionButton/tree/master
class FabMenu : ConstraintLayout {

    private var orientation: Int
    private var position: Int
    private var mainIconId: Int

    private val defaultSpacing = resources.getDimension(R.dimen.default_padding).toInt()
    private val mainFab: FloatingActionButton = FloatingActionButton(context)

    // Colors used for the main button
    private val primaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_primary, context.theme)
    private val onPrimaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_onPrimary, context.theme)
    // Colors used for the sub buttons
    private val secondaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_secondary, context.theme)
    private val onSecondaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_onSecondary, context.theme)

    private val subFabMenuId = View.generateViewId()
    private val fabMenuId = View.generateViewId()
    private val transparentOverlayId = View.generateViewId()
    private val mainFabId = View.generateViewId()

    @RequiresApi(Build.VERSION_CODES.Q)
    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : super(context, attrs, defStyleAttr) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.FabMenu, defStyleAttr, defStyleRes)
        saveAttributeDataForStyleable(context, R.styleable.FabMenu, attrs, attributes, defStyleAttr, defStyleRes)

        orientation = attributes.getInt(R.styleable.FabMenu_orientation, 0)
        position = attributes.getInt(R.styleable.FabMenu_position, 0)
        mainIconId = attributes.getResourceId(R.styleable.FabMenu_fab_icon, R.drawable.baseline_fiber_manual_record_24)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val childFabs = children.toList()

        Timber.d("""
            Creating FabMenu with properties:
                - Orientation: $orientation
                - Position: $position
                - IconId: $mainIconId
        """.trimIndent())


        val mainFab = setupMainFab(mainIconId, position, orientation)
        val subFabMenu = setupSubFabMenu(childFabs, orientation)
        setupOverlays()
        setupMenu(position, mainFab, subFabMenu)

        findViewById<View>(transparentOverlayId).visibility = GONE
        findViewById<LinearLayout>(subFabMenuId).visibility = GONE

        Timber.d("""
            Done creating FabMenu with properties:
                - Orientation: $orientation vs ${findViewById<LinearLayout>(subFabMenuId).orientation}
                - Position: $position
                - IconId: $mainIconId
        """.trimIndent())
    }

    private fun setupMenu(position: Int, mainFab: FloatingActionButton, subFabMenu: LinearLayout) {
        val menu = ConstraintLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                when(position) {
                    BOTTOM_RIGHT_CORNER -> {
                        bottomToBottom = LayoutParams.PARENT_ID
                        endToEnd = LayoutParams.PARENT_ID
                        setMargins(0, 0, defaultSpacing, defaultSpacing)
                    }
                    BOTTOM_LEFT_CORNER -> {
                        bottomToBottom = LayoutParams.PARENT_ID
                        startToStart = LayoutParams.PARENT_ID
                        setMargins(defaultSpacing, 0, 0, defaultSpacing)
                    }
                    TOP_RIGHT_CORNER -> {
                        topToTop = LayoutParams.PARENT_ID
                        endToEnd = LayoutParams.PARENT_ID
                        setMargins(0, defaultSpacing, defaultSpacing, 0)
                    }
                    TOP_LEFT_CORNER -> {
                        topToTop = LayoutParams.PARENT_ID
                        startToStart = LayoutParams.PARENT_ID
                        setMargins(defaultSpacing, defaultSpacing, 0, 0)
                    }
                }
                setPadding(defaultSpacing, defaultSpacing, defaultSpacing, defaultSpacing)
            }
            background = AppCompatResources.getDrawable(context, R.drawable.white_rounded_menu)
        }

        menu.addView(mainFab)
        menu.addView(subFabMenu)
        addView(menu)
    }


    private fun setupSubFabMenu(childFabs: List<View>, subFabOrientation: Int) : LinearLayout {
        val menu = LinearLayout(context)
        menu.apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                when(subFabOrientation) {
                    TOP_TO_BOTTOM -> {
                        topToBottom = mainFabId
                        menu.orientation = LinearLayout.VERTICAL
                        startToStart = mainFabId
                        endToEnd = mainFabId
                    }
                    BOTTOM_TO_TOP -> {
                        bottomToTop = mainFabId
                        menu.orientation = LinearLayout.VERTICAL
                        startToStart = mainFabId
                        endToEnd = mainFabId
                    }
                    LEFT_TO_RIGHT -> {
                        startToEnd = mainFabId
                        menu.orientation = LinearLayout.HORIZONTAL
                        topToTop = mainFabId
                        bottomToBottom = mainFabId
                    }
                    RIGHT_TO_LEFT -> {
                        endToStart = mainFabId
                        menu.orientation = LinearLayout.HORIZONTAL
                        topToTop = mainFabId
                        bottomToBottom = mainFabId
                    }
                    BLOOM -> { // TODO
                        bottomToTop = mainFabId
                        menu.orientation = LinearLayout.VERTICAL
                        startToStart = mainFabId
                        endToEnd = mainFabId
                        Snackbar.make(this@FabMenu, "Bloom orientation is not implemented yet", Snackbar.LENGTH_SHORT).show()
                    }
                }
                setMargins(0,0,0, defaultSpacing)
            }
            id = subFabMenuId
        }

        childFabs.forEach {
            Timber.d("""
                Settings for ${it.id}
                - Keep custom icon color: TBD
                - Dynamic margins       : TBD
            """.trimIndent())

            if (it is FloatingActionButton) {
                it.backgroundTintList = secondaryColor
                it.imageTintList = onSecondaryColor
            }

            // TODO make this depend on number of children, fill the space...
            it.layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(0,0,0, resources.getDimension(R.dimen.default_padding).toInt())
            }

            removeView(it)
            menu.addView(it)
        }

        return menu
    }



    private fun setupOverlays() {
        val transparentOverlay = View(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                startToStart = LayoutParams.PARENT_ID
                endToEnd = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                bottomToBottom = LayoutParams.PARENT_ID
            }
            setBackgroundColor(Color.argb(128,255,0,255)) //FIXME: Change color to white?
            id = transparentOverlayId
        }
        transparentOverlay.setOnClickListener {
            findViewById<FloatingActionButton>(mainFabId).performLongClick()
        }
        addView(transparentOverlay)
    }



    private fun setupMainFab(mainIconId: Int, position: Int, orientation: Int) : FloatingActionButton {
        mainFab.apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                if (position == BOTTOM_LEFT_CORNER || position == BOTTOM_RIGHT_CORNER) {
                    bottomToBottom = LayoutParams.PARENT_ID
                } else {
                    topToTop = LayoutParams.PARENT_ID
                }
            }
            size = FloatingActionButton.SIZE_NORMAL // TODO: might need to change this size?
            // TODO set color, icon
            backgroundTintList = primaryColor
            imageTintList = onPrimaryColor
            setImageDrawable(AppCompatResources.getDrawable(context, mainIconId))

            id = mainFabId
        }

        setMainButtonAnimation()
        mainFab.setOnLongClickListener {
            findViewById<LinearLayout>(subFabMenuId).apply {
                visibility = if(this.visibility == GONE) VISIBLE else GONE
            }
            findViewById<View>(transparentOverlayId).apply {
                visibility = if(this.visibility == GONE) VISIBLE else GONE
            }
            true
        }
        return mainFab
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

    @RequiresApi(Build.VERSION_CODES.Q)
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


        private const val BOTTOM_TO_TOP         = 0
        private const val TOP_TO_BOTTOM         = 1
        private const val LEFT_TO_RIGHT         = 2
        private const val RIGHT_TO_LEFT         = 3
        // TODO: Later functionality! This should spread the child fab like a flower around X corner
        private const val BLOOM                 = 4

        private const val BOTTOM_LEFT_CORNER    = 10
        private const val BOTTOM_RIGHT_CORNER   = 11
        private const val TOP_LEFT_CORNER       = 12
        private const val TOP_RIGHT_CORNER      = 13
    }
}