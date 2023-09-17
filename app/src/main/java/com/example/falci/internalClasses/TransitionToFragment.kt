package com.example.falci.internalClasses

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.falci.R

class TransitionToFragment {

    object ReplaceFragmentWithAnimation{
        fun replaceFragmentWithAnimation(fragmentManager: FragmentManager, targetFragment: Fragment) {
            fragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_down,
                    R.anim.slide_up,
                    R.anim.slide_down,
                    R.anim.slide_up
                )
                replace(R.id.main_fragment_container, targetFragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    object ReplaceActivityToFragment{
        fun replaceMainActivityToFragment(fragmentManager: FragmentManager, targetFragment: Fragment) {
            fragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_down,
                    R.anim.slide_up,
                    R.anim.slide_down,
                    R.anim.slide_up
                )
                .replace(R.id.main_fragment_container, targetFragment)
                .addToBackStack(null)
                .commit()
        }

        fun replaceLoginActivityToFragment(fragmentManager: FragmentManager, targetFragment: Fragment) {
            fragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_down,
                    R.anim.slide_up,
                    R.anim.slide_down,
                    R.anim.slide_up
                )
                .replace(R.id.login_signup_container, targetFragment)
//                .addToBackStack(null)
                .commit()
        }

    }
}