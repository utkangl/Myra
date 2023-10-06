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
import com.example.falci.internalClasses.AuthenticationFunctions
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

                if (navigateBackToProfileActivity){
                    navigateBackToProfileActivity = false
                    val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                    val intent = Intent(requireContext(), ProfileActivity::class.java); startActivity(intent,options.toBundle())
                    navigateToFavs = true
                } else{
                    val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.activity_slide_down, 0)
                    val mainActivityIntent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(mainActivityIntent, options.toBundle())
                }
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

        if (getHoroscopeData.is_favourite){favouriteThisHoroscope.setImageResource(R.drawable.purple_star_icon)}

        favouriteThisHoroscope.setOnClickListener{
            println("favori mi ? ${getHoroscopeData.is_favourite}")

            if (!getHoroscopeData.is_favourite){
                val gson = Gson()
                createFavouriteHoroscope.title = "title of horoscope"
                createFavouriteHoroscope.horoscopeId = getHoroscopeData.id
                val favouriteHoroscopeJson = createJsonObject("title" to createFavouriteHoroscope.title, "fortune" to createFavouriteHoroscope.horoscopeId!!.toInt())
                postJsonWithHeader(urls.favouriteHoroscopeURL, favouriteHoroscopeJson,  requireContext())
                { responseBody, _ ->
                    val responseJson = responseBody?.let { it1 -> JSONObject(it1) }
                    val detail = responseJson?.optString("detail")
                    if (statusCode == 201){
                        println(responseJson)
                        favouriteThisHoroscope.setImageResource(R.drawable.purple_star_icon)
                        getHoroscopeData.is_favourite = true
                        favouriteHoroscope = gson.fromJson(responseBody, FavouriteHoroscopeDataClass::class.java)
                        requireActivity().runOnUiThread{ Toast.makeText(requireContext(), "201 fav success" , Toast.LENGTH_SHORT).show() }
                    }
                    if (statusCode == 208){
                        println(detail)
                        favouriteThisHoroscope.setImageResource(R.drawable.purple_star_icon)
                        favouriteHoroscope = gson.fromJson(responseBody, FavouriteHoroscopeDataClass::class.java)
                        requireActivity().runOnUiThread{ Toast.makeText(requireContext(), detail , Toast.LENGTH_SHORT).show() }
                        getHoroscopeData.is_favourite = true
                    }
                    if (statusCode == 400){
                        println(detail)
                        requireActivity().runOnUiThread{ Toast.makeText(requireContext(), detail , Toast.LENGTH_SHORT).show() }
                        getHoroscopeData.is_favourite = false
                    }
                    if (statusCode == 404){
                        println(detail)
                        requireActivity().runOnUiThread{ Toast.makeText(requireContext(), detail , Toast.LENGTH_SHORT).show() }
                        getHoroscopeData.is_favourite = false
                    }
                }
            }
            // if already fav, destroy on click
            if (getHoroscopeData.is_favourite){

                favouriteHoroscope.id = getHoroscopeData.favourite_id

                println("fav horoscope id: ${favouriteHoroscope.id}")

                fun destroyFavHoroscope(){
                    if (favouriteHoroscope.id != null){
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

                                if (statusCode == 401){
                                    println("unauthorized 401, taking new access token")
                                    AuthenticationFunctions.PostJsonFunctions.takeNewAccessToken(urls.refreshURL, requireContext())
                                    { responseBody401, exception ->
                                        if (responseBody401 != null) {
                                            println(tokensDataClass.accessToken)
                                            destroyFavHoroscope()

                                        } else {
                                            println(exception)
                                        }
                                    }
                                }

                                if (statusCode == 204){
                                    getHoroscopeData.is_favourite = false
                                    requireActivity().runOnUiThread{ Toast.makeText(requireContext(), "204 deleted" , Toast.LENGTH_SHORT).show() }
                                    println("is this horoscope favourite ${getHoroscopeData.is_favourite}")
                                    favouriteThisHoroscope.setImageResource(R.drawable.white_star_icon)
                                }

                                if (statusCode == 404){
                                    println("404 not found")
                                    println(call)
                                }
                            }
                        })
                    }
                }
                destroyFavHoroscope()
                if (favouriteHoroscope.id == null){
                    requireActivity().runOnUiThread{ Toast.makeText(requireContext(), "fav horoscope id is null" , Toast.LENGTH_SHORT).show() }
                }
            }
        } // end of favouriteThisHoroscope.setOnClickListener
        return v // end of onCreateView
    }
}