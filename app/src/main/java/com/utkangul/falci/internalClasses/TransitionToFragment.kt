package com.utkangul.falci.internalClasses

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.utkangul.falci.R

class TransitionToFragment {

    object ReplaceFragmentWithAnimation{
        fun replaceProfileFragmentWithAnimation(fragmentManager: FragmentManager, targetFragment: Fragment) {
            fragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.fragment_slide_down, 0, R.anim.fragment_slide_down, R.anim.slide_up)
                replace(R.id.profile_fragment_container, targetFragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    object ReplaceActivityToFragment{
        fun replaceMainActivityToFragment(fragmentManager: FragmentManager, targetFragment: Fragment) {
            fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_down, 0, R.anim.fragment_slide_down, R.anim.slide_up)
                .replace(R.id.main_fragment_container, targetFragment)
                .addToBackStack(null)
                .commit()
        }

        fun replaceLoginActivityToFragment(fragmentManager: FragmentManager, targetFragment: Fragment) {
            fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_down, 0, R.anim.fragment_slide_down, R.anim.slide_up)
                .replace(R.id.login_signup_container, targetFragment)
                .commit()
        }

        fun replaceLoginActivityToSignUpFragment(fragmentManager: FragmentManager, targetFragment: Fragment) {
            fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_down, 0, R.anim.fragment_slide_down, R.anim.slide_up)
                .addToBackStack(null)
                .replace(R.id.login_signup_container, targetFragment)
                .commit()
        }

        fun replaceProfileActivityToFragment(fragmentManager: FragmentManager, targetFragment: Fragment) {
            fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_down, 0, R.anim.fragment_slide_down, R.anim.slide_up)
                .replace(R.id.profile_fragment_container, targetFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}