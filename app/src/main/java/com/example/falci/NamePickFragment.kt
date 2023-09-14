package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.falci.internalClasses.TransitionToFragment.ReplaceFragmentWithAnimation.replaceFragmentWithAnimation
import com.example.falci.internalClasses.userRegister

class NamePickFragment : Fragment() {

    private lateinit var nameInput: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_name_pick, container, false)

        val namepickfragmentnextbutton = v.findViewById<Button>(R.id.namepickfragmentnextbutton)

        namepickfragmentnextbutton.setOnClickListener {
            nameInput = v.findViewById(R.id.namepickfragmentnameinputtext)

            if (nameInput.text.isNotEmpty()) {
                userRegister.name = nameInput.text.toString()
                replaceFragmentWithAnimation(parentFragmentManager, GenderPickFragment())
            }
        }

        return v
    }

}