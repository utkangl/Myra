package com.example.falci

import android.os.Bundle
import android.text.Editable
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

        nameInput = v.findViewById<EditText>(R.id.namepickfragmentnameinputtext)

        val namepickfragmentnextbutton = v.findViewById<Button>(R.id.namepickfragmentnextbutton)
        val genderPickFragment = GenderPickFragment()

        namepickfragmentnextbutton.setOnClickListener {
            val name = nameInput.text.toString().trim() // Kullanıcının girdiği adı al

            if (name.isNotEmpty()) {
                NameObject.name = name // NameObject.name'i güncelle

                parentFragmentManager.beginTransaction().apply {
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    replace(R.id.main_fragment_container, genderPickFragment)
                    addToBackStack(null)
                    commit()
                }
            }
        }

        return v
    }

    object NameObject {
        var name: String = ""
    }
}