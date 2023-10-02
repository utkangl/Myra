package com.example.falci

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.falci.internalClasses.AuthenticationFunctions.CreateJsonObject.createJsonObject
import com.example.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader
import com.example.falci.internalClasses.TransitionToFragment.ReplaceActivityToFragment.replaceMainActivityToFragment
import com.example.falci.internalClasses.dataClasses.*
import com.example.falci.internalClasses.statusCode
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class HoroscopeDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_horoscope_detail, container, false)
        val horoscopeText = v.findViewById<TextView>(R.id.horoscope_textView)
        val miraHoroscopeDetailBottom = v.findViewById<ImageView>(R.id.miraHoroscopeDetailBottom)
        val miraHoroscopeDetailTop = v.findViewById<ImageView>(R.id.MiraHoroscopeDetailTop)
        val favouriteThisHoroscope = v.findViewById<ImageView>(R.id.favouriteThisHoroscope)

        // create and format horoscope string with the instance of GetHoroscopeData's field
        val horoscope =
            " Welcome ${userCompleteProfile.name} \n \n " + "Burc Ozetin: \n\n  " +
            "${getHoroscopeData.summary}  \n\n " +
            "Pozitif Yonler: \n \n ${getHoroscopeData.good}" +
            "\n\n Negatif Yonler: \n\n ${getHoroscopeData.bad}"

        // to show user the text we created above, set TextView's text as the text we created
        horoscopeText.text = horoscope

        // is user clicks to mira image at the bottom, navigate user to chat w/ mira screen
        miraHoroscopeDetailBottom.setOnClickListener{
            isFromHoroscope = true
            replaceMainActivityToFragment(parentFragmentManager, ChatFragment())
            }

        // create callback variable which will handle onBackPressed and navigate to main activity
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                val mainActivityIntent = Intent(requireContext(), MainActivity::class.java)
                startActivity(mainActivityIntent, options.toBundle())
            }
        }
        // call callback to navigate user to main activity when back button is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

//        textToSpeech = TextToSpeech(requireContext()) { status ->
//            if (status != TextToSpeech.ERROR) {
//                val locale = Locale("tr", "TR")
//                textToSpeech.language = locale
//
//            }
//        }
//
        miraHoroscopeDetailTop.setOnClickListener{

            val gson = Gson()

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(urls.favouriteHoroscopeURL)
                .get()
                .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("exception $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()
                    statusCode = response.code()
                    println("get fav horoscopes response body: $responseBody")

                    if (statusCode == 200) {
                        println("get fav horoscopes response code $statusCode")
                        println("before responseBody $listOfFavouriteHoroscopes")
                        listOfFavouriteHoroscopes = gson.fromJson(responseBody, ListOfFavouriteHoroscopesDataClass::class.java)
                        println("after responseBody $listOfFavouriteHoroscopes")



                    }
                }
            })
        }

        if (createFavouriteHoroscope.isThisHoroscopeFavourite){favouriteThisHoroscope.setImageResource(R.drawable.purple_star_icon)}

        favouriteThisHoroscope.setOnClickListener{

            if (!createFavouriteHoroscope.isThisHoroscopeFavourite){
                val gson = Gson()
                createFavouriteHoroscope.title = "title of horoscope"
                createFavouriteHoroscope.horoscopeId = getHoroscopeData.id
                val favouriteHoroscopeJson = createJsonObject("title" to createFavouriteHoroscope.title, "fortune" to createFavouriteHoroscope.horoscopeId!!.toInt())
                postJsonWithHeader(urls.favouriteHoroscopeURL, favouriteHoroscopeJson, tokensDataClass.accessToken)
                { responseBody, _ ->
                    val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                    val detail = responseJson?.optString("detail")
                    if (statusCode == 201){
                        println(responseJson)
                        favouriteThisHoroscope.setImageResource(R.drawable.purple_star_icon)
                        createFavouriteHoroscope.isThisHoroscopeFavourite = true
                        favouriteHoroscope = gson.fromJson(responseBody, FavouriteHoroscopeDataClass::class.java)
                        println(favouriteHoroscope)
                    }
                    if (statusCode == 208){
                        println(detail)
                        favouriteThisHoroscope.setImageResource(R.drawable.purple_star_icon)
                        requireActivity().runOnUiThread{ Toast.makeText(requireContext(), detail , Toast.LENGTH_SHORT).show() }
                        createFavouriteHoroscope.isThisHoroscopeFavourite = true
                    }
                    if (statusCode == 400){
                        println(detail)
                        requireActivity().runOnUiThread{ Toast.makeText(requireContext(), detail , Toast.LENGTH_SHORT).show() }
                        createFavouriteHoroscope.isThisHoroscopeFavourite = false
                    }
                    if (statusCode == 404){
                        println(detail)
                        requireActivity().runOnUiThread{ Toast.makeText(requireContext(), detail , Toast.LENGTH_SHORT).show() }
                        createFavouriteHoroscope.isThisHoroscopeFavourite = false
                    }
                }
            } else {

                val idToDelete = favouriteHoroscope.id
                val apiUrl = "https://api.atlasuavteam.com/api/favourite/$idToDelete/"
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(apiUrl)
                    .delete()
                    .header("Authorization", "Bearer ${tokensDataClass.accessToken}")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("exception $e")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        println(response)
                        statusCode = response.code()
                        println("destroy fav horoscope response code $statusCode")

                        if (statusCode == 204){
                            createFavouriteHoroscope.isThisHoroscopeFavourite = false
                            println("is this horoscope favourite ${createFavouriteHoroscope.isThisHoroscopeFavourite}")
                            favouriteThisHoroscope.setImageResource(R.drawable.white_star_icon)
                        }

                        if (statusCode == 404){
                            println("404 not found")
                        }
                    }
                })
            }
        } // end of favouriteThisHoroscope.setOnClickListener
        return v // end of onCreateView
    }
}