package com.iteration.climbingmuse.ui.settings

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.internal.BaselineLayout
import com.google.android.material.navigation.NavigationBarItemView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.textview.MaterialTextView
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val navRail : NavigationRailView = binding.settingsNavigationRail
        val frag : FragmentContainerView = binding.settingsFragment

        val navHostFragment = childFragmentManager.findFragmentById(R.id.settings_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        NavigationUI.setupWithNavController(navRail, navController)

        setupNavigationRailText(navRail)
        setBadges(navRail)


        return root
    }

    private fun setBadges(navRail: NavigationRailView) {
        // FIXME: This is just an example
        navRail.getOrCreateBadge(R.id.navigation_computer_vision_settings).apply {
            isVisible = true
            number = 2
            badgeGravity = BadgeDrawable.TOP_END
        }
    }

    private fun setupNavigationRailText(navRail: NavigationRailView) {
        navRail.findViewById<NavigationBarItemView>(R.id.navigation_computer_vision_settings).apply {
            setItemMaxLines(
                this.findViewById(com.google.android.material.R.id.navigation_bar_item_labels_group),
                2
            )
        }

    }

    private fun setItemMaxLines(baselineLayout: BaselineLayout?, maxLines: Int) {
        if (baselineLayout != null) {
            for (i in 0 until baselineLayout.childCount) {
                val child = baselineLayout.getChildAt(i)
                if (child is MaterialTextView) {
                    child.maxLines = maxLines
                    child.gravity = Gravity.CENTER
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}