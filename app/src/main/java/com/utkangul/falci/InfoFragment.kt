package com.utkangul.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class InfoFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_info, container, false)

        val versionText = v.findViewById<TextView>(R.id.VersionValue)

        versionText.text = BuildConfig.VERSION_NAME


        return v
    }

}