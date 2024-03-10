package com.iteration.climbingmuse.ui

import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatCheckBox
import com.google.android.material.checkbox.MaterialCheckBox

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
    }

}