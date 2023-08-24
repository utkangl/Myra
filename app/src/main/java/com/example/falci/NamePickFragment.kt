package com.example.falci

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentTransaction


class NamePickFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_name_pick, container, false)

        val loginTextView = v.findViewById<LinkTextView>(R.id.logintextview)

        val namepickfragmentnextbutton = v.findViewById<Button>(R.id.namepickfragmentnextbutton)

        val namepickfragmentnameinputtext = v.findViewById<EditText>(R.id.namepickfragmentnameinputtext)

        val genderPickFragment = GenderPickFragment()




        namepickfragmentnextbutton.setOnClickListener{

            if (namepickfragmentnameinputtext.text.isNotEmpty())
            {
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, genderPickFragment)
                    addToBackStack(null)
                    commit()
                }

            }

        }


        loginTextView.setOnClickListener{
            val intent = Intent(activity, LoginSignupActivity::class.java)
            startActivity(intent)
        }



        return v
    }


}