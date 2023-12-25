package com.utkangul.falci

import android.content.Context
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.takeFreshTokens
import com.utkangul.falci.internalClasses.BurcCardFunctions

import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewInvisible
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.utkangul.falci.internalClasses.dataClasses.*
import okhttp3.*
import java.io.IOException

var destroyUserStatusCode: Int? = null

class SavedLookupUserCardView constructor(
    context: Context,
    allCards: List<SavedLookupUserCardView>,
    burcCard: CardView,
    learnYourBurcButton: AppCompatButton
) : RelativeLayout(context) {
    private val cardView: CardView
    private val background: ImageView
    private val savedUsername: TextView
    private var isSelected = false
    var id: Int? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.lookup_user_card_view, this, true)
        cardView = findViewById(R.id.savedLookupUserCard)
        background = findViewById(R.id.savedLookupUserSelectedBG)
        savedUsername = findViewById(R.id.savedUsername)

        val burcCardFunctions = BurcCardFunctions(context)
        cardView.setOnClickListener {

            if (!isSelected) {
                getPartnerProfile.id = id!!
                println(getPartnerProfile.id)
                selectCard(allCards)
                burcCardFunctions.setSelectedFortuneFields(learnYourBurcButton, postHoroscopeData.type!!, postHoroscopeData.time_interval!!, getPartnerProfile.id)
                val scale = context.resources.displayMetrics.density
                val buttonParams = learnYourBurcButton.layoutParams as LayoutParams
                burcCardFunctions.setSelectedFortuneFields(learnYourBurcButton, postHoroscopeData.type!!, postHoroscopeData.time_interval!!, getPartnerProfile.id)
                burcCardFunctions.animateBurcCardSize(burcCard, 380, 655, {
                    buttonParams.topMargin = (110 * scale + 0.5f).toInt()
                    setViewVisibleWithAnimation(context, learnYourBurcButton)
                    controlVariables.isFromAddLookupUser = false}, {})
            }
        }
        cardView.setOnLongClickListener {
            performLongClick()
        }
    }

    private fun selectCard(allCards: List<SavedLookupUserCardView>) {
        isSelected = true
        setViewVisibleWithAnimation(context, background)
        for (card in allCards) {
            if (card != this) {
                card.deselectCard()
            }
        }
    }

    fun deselectCard() {
        isSelected = false
        setViewInvisible(background)
    }
}

object ExportLookupUserCardFunctions {

    fun deselectAllCards(cardList: MutableList<SavedLookupUserCardView>){
        if (cardList.isNotEmpty()) {
            for (card in cardList) {
                card.deselectCard()
            }
        }
    }
    fun destroySavedLookUpUser(
        userID: Int?,
        cardList: MutableList<SavedLookupUserCardView>,
        savedUsersLinearContainer: LinearLayout,
        savedLookupUserCardView: SavedLookupUserCardView,
        activity: MainActivity,
        context: Context
    ) {
        if (userID != null) {
            val apiUrl = "https://api.kircibros.com/api/lookup_user/$userID/"
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
                    destroyUserStatusCode = response.code
                    if (destroyUserStatusCode == 204) {
                        activity.runOnUiThread {
                            val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
                            savedLookupUserCardView.startAnimation(animation)
                            savedUsersLinearContainer.removeView(savedLookupUserCardView)
                            cardList.remove(savedLookupUserCardView)
                        }
                    }
                    else if (destroyUserStatusCode == 401){
                        takeFreshTokens(urls.refreshURL, context)
                        { responseBody401, exception ->
                            if (responseBody401 != null) {
                                println(tokensDataClass.accessToken)
                                destroySavedLookUpUser(userID,cardList,savedUsersLinearContainer, savedLookupUserCardView, activity, context)
                            } else {
                                println(exception)
                            }
                        }
                    }
                }
            })
        }
    }
}