package com.iteration.climbingmuse.ui

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.InverseMethod
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.iteration.climbingmuse.R
import timber.log.Timber


class MaterialViewHelper {

    companion object {

        fun MaterialCheckBox.setSubCheckboxes(childrenCheckBoxes: Sequence<AppCompatCheckBox>) {
            var isUpdatingChildren = false

            val parentOnCheckedStateChangedListener =
                MaterialCheckBox.OnCheckedStateChangedListener { checkBox: MaterialCheckBox, state: Int ->
                    val isChecked = checkBox.isChecked
                    if (state != MaterialCheckBox.STATE_INDETERMINATE) {
                        isUpdatingChildren = true
                        for (child in childrenCheckBoxes) {
                            child.isChecked = isChecked
                        }
                        isUpdatingChildren = false
                    }
                }
            this.addOnCheckedStateChangedListener(parentOnCheckedStateChangedListener)

            // Checked state changed listener for each child
            val childOnCheckedStateChangedListener =
                MaterialCheckBox.OnCheckedStateChangedListener { checkBox: MaterialCheckBox?, state: Int ->
                    if (!isUpdatingChildren) {
                        this.setParentState(childrenCheckBoxes, parentOnCheckedStateChangedListener)
                    }
                }
            for (child in childrenCheckBoxes) {
                (child as MaterialCheckBox).addOnCheckedStateChangedListener(childOnCheckedStateChangedListener)
            }

            this.setParentState(childrenCheckBoxes, parentOnCheckedStateChangedListener)
        }

        private fun MaterialCheckBox.setParentState(childrenCheckBoxes: Sequence<CheckBox>, parentOnCheckedStateChangedListener: MaterialCheckBox.OnCheckedStateChangedListener) {
            val checkedCount = childrenCheckBoxes.filter { obj: CheckBox -> obj.isChecked }
                .count()
            val allChecked = checkedCount == childrenCheckBoxes.count()
            val noneChecked = checkedCount == 0

            this.removeOnCheckedStateChangedListener(parentOnCheckedStateChangedListener)
            if (allChecked) {
                this.isChecked = true
            } else if (noneChecked) {
                this.isChecked = false
            } else {
                this.checkedState = MaterialCheckBox.STATE_INDETERMINATE
            }
            this.addOnCheckedStateChangedListener(parentOnCheckedStateChangedListener)
        }

        fun dpToPx(context: Context, @Dimension(unit = Dimension.DP) dp: Int): Float {
            val r: Resources = context.resources
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            )
        }


        class ChipBuilder(val context: Context) {
            private val secondaryColor = ResourcesCompat.getColorStateList(context.resources, R.color.md_theme_light_secondary, context.theme)
            private val onSecondaryColor = ResourcesCompat.getColorStateList(context.resources, R.color.md_theme_light_onSecondary, context.theme)
            private var chip: Chip = new().build()

            fun new() : ChipBuilder {
                chip = Chip(context).apply {
                    chipBackgroundColor = secondaryColor
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setTextColor(onSecondaryColor)
                }
                return this
            }

            fun setIcon(id: Int, keepColor: Boolean = false) : ChipBuilder {
                val icon = ResourcesCompat.getDrawable(context.resources, id, context.theme)
                chip.chipIcon = icon

                if (!keepColor) {
                    chip.chipIconTint = onSecondaryColor
                }
                return this
            }

            fun setText(label: String) : ChipBuilder {
                chip.apply { text = label }
                return this
            }

            fun build() : Chip {
                return chip
            }

        }
    }

    class MaterialCheckBoxConverter {
        companion object {
            enum class State {
                indeterminate,
                checked,
                unchecked
            }

            @InverseMethod("stringToState")
            @JvmStatic
            fun stateToString(state: Int): State {
                return when (state) {
                    MaterialCheckBox.STATE_CHECKED -> State.checked
                    MaterialCheckBox.STATE_INDETERMINATE -> State.indeterminate
                    MaterialCheckBox.STATE_UNCHECKED -> State.unchecked
                    else -> {
                        Timber.e("State [%s] should not exist", state)
                        return State.indeterminate
                    }
                }
            }

            @JvmStatic
            fun stringToState(state: String): Int {
                return when (state) {
                    "checked" -> MaterialCheckBox.STATE_CHECKED
                    "indeterminate" -> MaterialCheckBox.STATE_INDETERMINATE
                    "unchecked" -> MaterialCheckBox.STATE_UNCHECKED
                    else -> {
                        Timber.e("State [%s] should not exist", state)
                        return MaterialCheckBox.STATE_INDETERMINATE
                    }
                }

            }

        }
    }
}
