package com.example.falci

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.example.falci.internalClasses.dataClasses.ListOfFavouriteHoroscopesDataClass
import com.example.falci.internalClasses.dataClasses.listOfFavouriteHoroscopes
import com.example.falci.internalClasses.dataClasses.tokensDataClass
import com.example.falci.internalClasses.dataClasses.urls
import com.example.falci.internalClasses.statusCode
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class FavCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0): RelativeLayout(context) {

    init {
        LayoutInflater.from(context).inflate(R.layout.fav_card_view, this, true)

    }

}