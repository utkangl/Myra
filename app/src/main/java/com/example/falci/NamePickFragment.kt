package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class NamePickFragment : Fragment() {

    private lateinit var nameInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_name_pick, container, false)

        val namepickfragmentnextbutton = v.findViewById<Button>(R.id.namepickfragmentnextbutton)
        val genderPickFragment = GenderPickFragment()

        namepickfragmentnextbutton.setOnClickListener {
            nameInput = v.findViewById(R.id.namepickfragmentnameinputtext)

            if (nameInput.text.isNotEmpty()) {
                NameObject.name = nameInput.text.toString()
                println(NameObject.name)

                parentFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.slide_down,
                        R.anim.slide_up,
                        R.anim.slide_down,
                        R.anim.slide_up
                    )
                    replace(R.id.main_fragment_container, genderPickFragment)
                    addToBackStack(null)
                    commit()
                }
            }
        }

        return v
    }

    object NameObject {
        lateinit var name: String
    }
}