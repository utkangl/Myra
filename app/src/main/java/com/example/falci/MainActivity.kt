package com.example.falci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.jar.Attributes.Name

class MainActivity : AppCompatActivity() {



    val namePickFragment = NamePickFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        supportFragmentManager.beginTransaction().replace(R.id.container, namePickFragment).commit()


    }
}