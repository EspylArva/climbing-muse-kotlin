package com.iteration.climbingmuse.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.children
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.iteration.climbingmuse.R
import timber.log.Timber


// This is based on LinearLayout: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/android/widget/LinearLayout.java
// and inspired by FloatingActionButton by Clans: https://github.com/Clans/FloatingActionButton/tree/master
class FabMenu @RequiresApi(Build.VERSION_CODES.Q) @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var orientation: Int
    private var position: Int
    private var mainIconId: Int
    private var veilAlpha: Int
    private var veilTint: Int
    private var menuAlpha: Int
    private var menuTint: Int


    private val defaultSpacing = (resources.getDimension(R.dimen.default_padding)/2).toInt()
    private val scale = resources.displayMetrics.density

    // Components of FabMenu
    private val mainFab: FloatingActionButton = FloatingActionButton(context)
    private val fabMenu: ConstraintLayout = ConstraintLayout(context)
    private val subFabMenu: GridLayout = GridLayout(context)
    private val veil = View(context)
    private val subFabMenuId = View.generateViewId()
    private val fabMenuId = View.generateViewId()
    private val veilId = View.generateViewId()
    private val mainFabId = View.generateViewId()

    // Colors used for the main button
    private val primaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_primary, context.theme)
    private val onPrimaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_onPrimary, context.theme)
    // Colors used for the sub buttons
    private val secondaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_secondary, context.theme)
    private val onSecondaryColor = ResourcesCompat.getColorStateList(resources, R.color.md_theme_light_onSecondary, context.theme)


    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.FabMenu, defStyleAttr, defStyleRes)
        saveAttributeDataForStyleable(context, R.styleable.FabMenu, attrs, attributes, defStyleAttr, defStyleRes)
        orientation = attributes.getInt(R.styleable.FabMenu_orientation, 0)
        position = attributes.getInt(R.styleable.FabMenu_position, 0)
        mainIconId = attributes.getResourceId(R.styleable.FabMenu_fab_icon, R.drawable.baseline_fiber_manual_record_24)
        veilAlpha = attributes.getResourceId(R.styleable.FabMenu_veil_alpha, 100)
        veilTint = attributes.getResourceId(R.styleable.FabMenu_veil_tint, R.color.white)
        menuAlpha = attributes.getResourceId(R.styleable.FabMenu_menu_overlay_alpha, 100)
        menuTint = attributes.getResourceId(R.styleable.FabMenu_menu_overlay_tint, R.color.white)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val childFabs = children.toList()

        Timber.d("""
            Creating FabMenu with properties:
                - Orientation: $orientation
                - Position: $position
                - IconId: $mainIconId
            Children (count: ${childFabs.size}):
                - $childFabs
        """.trimIndent())


        val mainFab = setupMainFab(mainIconId, position)
        val subFabMenu = setupSubFabMenu(childFabs, orientation)
        addSubFab(childFabs)
        setupOverlays()
        setupMenu(position, mainFab, subFabMenu)

        veil.visibility = GONE
        subFabMenu.visibility = GONE

        Timber.d("""
            Done creating FabMenu with properties:
                - Orientation: $orientation vs ${subFabMenu.orientation}
                - Position: $position
                - IconId: $mainIconId
        """.trimIndent())

    }

    private fun setupMenu(position: Int, mainFab: FloatingActionButton, subFabMenu: ViewGroup) {
        fabMenu.addView(mainFab)
        fabMenu.addView(subFabMenu)
        fabMenu.apply {
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
                val paddingPx = MaterialViewHelper.dpToPx(context, defaultSpacing).toInt()
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
            }
            id = fabMenuId
        }
        setMenuBackground()

        addView(fabMenu)
    }

    private fun setMenuBackground() {
        val unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.rounded_corners)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        val menuColor = Color.argb(veilAlpha, menuTint.red, menuTint.green, menuTint.blue)
        DrawableCompat.setTint(wrappedDrawable, menuColor)

        fabMenu.apply {
            this.background = wrappedDrawable
        }
    }


    private fun setupSubFabMenu(childFabs: List<View>, subFabOrientation: Int) : GridLayout {
        Timber.d("Setup SubFabMenu")
        val buttonCount = childFabs.count { it is FloatingActionButton }
        subFabMenu.apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                when (position) {
                    BOTTOM_RIGHT_CORNER -> { bottomToBottom = LayoutParams.PARENT_ID ; endToEnd = LayoutParams.PARENT_ID }
                    BOTTOM_LEFT_CORNER -> { bottomToBottom = LayoutParams.PARENT_ID ; startToStart = LayoutParams.PARENT_ID }
                    TOP_RIGHT_CORNER -> { topToTop = LayoutParams.PARENT_ID ; endToEnd = LayoutParams.PARENT_ID }
                    TOP_LEFT_CORNER -> { topToTop = LayoutParams.PARENT_ID ; startToStart = LayoutParams.PARENT_ID }
                }
                when(subFabOrientation) {
                    TOP_TO_BOTTOM -> {
                        rowCount = buttonCount
                        columnCount = 2
                        topToBottom = mainFabId
                        setMargins(0, 2*defaultSpacing, 0, 0)
                    }
                    BOTTOM_TO_TOP -> {
                        rowCount = buttonCount
                        columnCount = 2
                        bottomToTop = mainFabId
                        setMargins(0, 0, 0, 2*defaultSpacing)
                    }
                    LEFT_TO_RIGHT -> {
                        rowCount = 2
                        columnCount = buttonCount
                        startToStart = mainFabId
                        setMargins(2*defaultSpacing, 0, 0, 0)
                    }
                    RIGHT_TO_LEFT -> {
                        endToEnd = mainFabId
                        rowCount = 2
                        columnCount = buttonCount
                        setMargins(0, 0, 2*defaultSpacing, 0)
                    }
                    BLOOM -> { // TODO
                        bottomToTop = mainFabId
                        rowCount = buttonCount
                        columnCount = 2
                        startToStart = mainFabId
                        endToEnd = mainFabId
                        Snackbar.make(this@FabMenu, "Bloom orientation is not implemented yet", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            id = subFabMenuId
        }
        return subFabMenu
    }

    private fun addSubFab(childFabs: List<View>) {
        // We want to add the FABs in the correct order
        val childrenList = if(orientation == TOP_TO_BOTTOM || orientation == LEFT_TO_RIGHT) childFabs else childFabs.reversed()
        for (i in childrenList.indices) {
            val child = childrenList[i]
            // Remove before adding to GridLayout, to move the children
            removeView(child)
            if (child is FloatingActionButton) {
                when(orientation) {
                    BOTTOM_TO_TOP, TOP_TO_BOTTOM -> {
                        val buttonColIndex = if (position == BOTTOM_LEFT_CORNER || position == TOP_LEFT_CORNER) 0 else 1
                        addSubFab(child, i, buttonColIndex, vertical = true)
                        Timber.d("Adding ${child.id} (${child.tag}) to gridLayout at position ${i}x$buttonColIndex")
                    }
                    LEFT_TO_RIGHT, RIGHT_TO_LEFT -> {
                        val buttonRowIndex = if (position == TOP_LEFT_CORNER || position == TOP_RIGHT_CORNER) 0 else 1
                        addSubFab(child, buttonRowIndex, i, vertical = false)
                        Timber.d("Adding ${child.id} (${child.tag}) to gridLayout at position ${buttonRowIndex}x$i")
                    }
                    else -> {
                        subFabMenu.addView(child)
                    }
                }
                child.backgroundTintList = secondaryColor
                child.imageTintList = onSecondaryColor
            } else {
                Timber.w("Child ${child.id} is not a FloatingActionButton (type: ${child.javaClass})")
            }
        }
    }

    private fun addSubFab(subFab: FloatingActionButton, rowIndex: Int, colIndex: Int, vertical: Boolean) {
        val labelRow = if(vertical) rowIndex else 1-rowIndex
        val labelCol = if(vertical) 1-colIndex else colIndex
        subFabMenu.addView(subFab.apply {
            layoutParams = GridLayout.LayoutParams().apply {
                height = LayoutParams.WRAP_CONTENT
                width = LayoutParams.WRAP_CONTENT
                bottomMargin = defaultSpacing
                rowSpec = GridLayout.spec(rowIndex)
                columnSpec = GridLayout.spec(colIndex)
            }
        })
        if(subFab.tag != "") {
            subFabMenu.addView(MaterialTextView(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    height = LayoutParams.WRAP_CONTENT
                    width = LayoutParams.WRAP_CONTENT
                    bottomMargin = defaultSpacing
                    rowSpec = GridLayout.spec(labelRow)
                    columnSpec = GridLayout.spec(labelCol)
                    setGravity(Gravity.CENTER)
                    setMargins(defaultSpacing, defaultSpacing, defaultSpacing, defaultSpacing)
                    text = subFab.tag.toString()
                }
            })
        } else {
            Timber.w("No tag for ${subFab.id} (tag:${subFab.tag}")
        }
    }



    private fun setupOverlays() {
        veil.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                startToStart = LayoutParams.PARENT_ID
                endToEnd = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                bottomToBottom = LayoutParams.PARENT_ID
            }
            val veilColor = resources.getColor(veilTint, context.theme)
            setBackgroundColor(Color.argb(veilAlpha,veilColor.red, veilColor.green, veilColor.blue))
            id = veilId
        }
        veil.setOnClickListener {
            mainFab.performLongClick()
        }
        addView(veil)
    }

    private fun setupMainFab(mainIconId: Int, position: Int) : FloatingActionButton {
        mainFab.apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                when (position) {
                    BOTTOM_LEFT_CORNER -> { bottomToBottom = LayoutParams.PARENT_ID ; startToStart = LayoutParams.PARENT_ID }
                    BOTTOM_RIGHT_CORNER -> { bottomToBottom = LayoutParams.PARENT_ID ; endToEnd = LayoutParams.PARENT_ID }
                    TOP_LEFT_CORNER -> { topToTop = LayoutParams.PARENT_ID ; startToStart = LayoutParams.PARENT_ID }
                    TOP_RIGHT_CORNER -> { topToTop = LayoutParams.PARENT_ID ; endToEnd = LayoutParams.PARENT_ID }
                }
            }
            size = FloatingActionButton.SIZE_NORMAL // TODO: might need to change this size?
            backgroundTintList = primaryColor
            imageTintList = onPrimaryColor
            setImageDrawable(AppCompatResources.getDrawable(context, mainIconId))

            id = mainFabId
        }

        mainFab.setOnLongClickListener {
            subFabMenu.apply {
                visibility = if(this.visibility == GONE) VISIBLE else GONE
            }
            veil.apply {
                visibility = if(this.visibility == GONE) VISIBLE else GONE
            }


            true
        }
        return mainFab
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onLayout(sizeChanged: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount == 0) {
            // Do not re-layout when there are no children.
            return
        }
        super.onLayout(sizeChanged, left, top, right, bottom)

        if (isInEditMode) {
            subFabMenu.visibility = View.VISIBLE
            forceLayout()
        }
    }




    companion object {

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