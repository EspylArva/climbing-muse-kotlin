package com.iteration.climbingmuse.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.navigationrail.NavigationRailView
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.forEach
import timber.log.Timber

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
        val notificationsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val navRail : NavigationRailView = binding.settingsNavigationRail
        val frag : FragmentContainerView = binding.settingsFragment

        createBadges(navRail)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.settings_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        NavigationUI.setupWithNavController(navRail, navController)

        return root
    }

    private fun createBadges(navRail: NavigationRailView) {
        navRail.getOrCreateBadge(R.id.navigation_permissions_settings).apply {
            isVisible = true
            number = 2
            badgeGravity = BadgeDrawable.TOP_END
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}