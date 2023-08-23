package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction


class Loginfragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_login, container, false)


        val loginfragmentsignuplinkedtext = v.findViewById<LinkTextView>(R.id.loginfragmentsignuplinkedtext)
        val signUpFragment = SignUpFragment()

        loginfragmentsignuplinkedtext.setOnClickListener{
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.container, signUpFragment )
            transaction.commit()
        }

        return v

    }

}