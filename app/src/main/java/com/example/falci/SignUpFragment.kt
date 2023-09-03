package com.example.falci

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.example.falci.SignUpFragment.Usernameandpasswordobject.password
import com.example.falci.SignUpFragment.Usernameandpasswordobject.username

class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_sign_up, container, false)


        val usernamefield = v.findViewById<EditText>(R.id.signupfragmentusernametext).text
        val passwordfield = v.findViewById<EditText>(R.id.signupfragmentpasswordtext).text

        val signupfragmentsignupbutton =
            v.findViewById<AppCompatButton>(R.id.signupfragmentsignupbutton)
        val namePickFragment = NamePickFragment()


        signupfragmentsignupbutton.setOnClickListener {

            username = usernamefield.toString()
            password = passwordfield.toString()

            println(" username $username")
            println(" password $password")

            parentFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                replace(R.id.main_fragment_container, namePickFragment)
                addToBackStack(null)
                commit()
            }
        }

        return v
    }


    object Usernameandpasswordobject {
        lateinit var username: String
        lateinit var password: String
    }


}


























