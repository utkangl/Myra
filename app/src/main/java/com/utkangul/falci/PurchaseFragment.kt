package com.utkangul.falci

import android.animation.Animator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.revenuecat.purchases.*
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.models.*
import com.utkangul.falci.internalClasses.AuthenticationFunctions
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.takeFreshTokens
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewGoneWithAnimation
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisible
import com.utkangul.falci.internalClasses.InternalFunctions.SetVisibilityFunctions.setViewVisibleWithAnimation
import com.utkangul.falci.internalClasses.dataClasses.revenueCatOneTimeCoinPackages
import com.utkangul.falci.internalClasses.dataClasses.revenueCatSubscriptionPackages
import com.utkangul.falci.internalClasses.dataClasses.urls
import com.utkangul.falci.internalClasses.statusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PurchaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_purchase, container, false)

        val fiveCoinText = v.findViewById<TextView>(R.id.fiveCoin_textView)
        val fiveCoinCard = v.findViewById<CardView>(R.id.fiveCoin_cardView)
        val fiveCoinPriceText = v.findViewById<TextView>(R.id.fiveCoin_price_textView)


        val twentyFiveCoinText = v.findViewById<TextView>(R.id.twentyFiveCoin_textView)
        val twentyFiveCoinCard = v.findViewById<CardView>(R.id.twentyFiveCoin_cardView)
        val twentyFiveCoinPriceText = v.findViewById<TextView>(R.id.twentyFiveCoin_price_textView)


        val fiftyCoinText = v.findViewById<TextView>(R.id.fiftyCoin_textView)
        val fiftyCoinCard = v.findViewById<CardView>(R.id.fiftyCoin_cardView)
        val fiftyCoinPriceText = v.findViewById<TextView>(R.id.fiftyCoin_price_textView)


        val weeklySubsTextView = v.findViewById<TextView>(R.id.weeklySubs_textView)
        val weeklySubsCard = v.findViewById<CardView>(R.id.weeklySubs_cardView)
        val weeklySubsPriceText = v.findViewById<TextView>(R.id.weeklySubs_price_textView)


        val monthlySubsTextView = v.findViewById<TextView>(R.id.monthlySubs_textView)
        val monthlySubsCard = v.findViewById<CardView>(R.id.monthlySubs_cardView)
        val monthlySubsPriceText = v.findViewById<TextView>(R.id.monthlySubs_price_textView)


        val yearlySubsTextView = v.findViewById<TextView>(R.id.yearlySubs_textView)
        val yearlySubsCard = v.findViewById<CardView>(R.id.yearlySubs_cardView)
        val yearlySubsPriceText = v.findViewById<TextView>(R.id.yearlySubs_price_textView)


        val loadingAnimation = v.findViewById<LottieAnimationView>(R.id.purchaseLoadingAnimation)
        val successAnimation = v.findViewById<LottieAnimationView>(R.id.purchaseSuccessfullAnimation)
        val purchaseLoadingAnimationContainer = v.findViewById<CardView>(R.id.purchaseLoadingAnimationContainer)

        val purchaseBackButton = v.findViewById<ImageButton>(R.id.purchaseBackButton)

        var weeklySubsStoreProduct: GoogleStoreProduct? = null
        var monthlySubsStoreProduct: GoogleStoreProduct? = null
        var yearlySubsStoreProduct: GoogleStoreProduct? = null


        var fiveCoinStoreProduct: GoogleStoreProduct? = null
        var twentyFiveCoinStoreProduct: GoogleStoreProduct? = null
        var fiftyCoinStoreProduct: GoogleStoreProduct? = null


        var allowBackPressed = true

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (allowBackPressed) {
                isEnabled = false
                requireActivity().onBackPressed()
            } else {
                Toast.makeText(requireContext(), requireActivity().resources.getString(R.string.wait_for_process), Toast.LENGTH_SHORT).show()
            }
        }


        successAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {
                setViewGoneWithAnimation(requireContext(), purchaseLoadingAnimationContainer, successAnimation, loadingAnimation)
                allowBackPressed = true
                callback.isEnabled = true
            }

            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
        })


        //onCompleted , onError situations for purchase callback
        val makepurchaseCallback = object : PurchaseCallback {
            override fun onCompleted(storeTransaction: StoreTransaction, customerInfo: CustomerInfo) {
                fun notifyApiAfterPurchase() {
                    println(storeTransaction)
                    println(customerInfo)
                    val userId = Purchases.sharedInstance.appUserID
                    val revenuecatUserIdJson = AuthenticationFunctions.CreateJsonObject.createJsonObject("user_id" to userId)
                    setViewVisibleWithAnimation(requireContext(), purchaseLoadingAnimationContainer)

                    CoroutineScope(Dispatchers.IO).launch {
                        loadingAnimation.post {
                            setViewVisible(loadingAnimation)
                            loadingAnimation.playAnimation()
                        }
                        try {
                            delay(2500)
                            postJsonWithHeader(requireActivity(),urls.notifyApiOnPurchaseURL, revenuecatUserIdJson, requireContext())
                            { responseBody, exception ->
                                exception?.printStackTrace()
                                println("responseBody $responseBody")
                                when (statusCode) {
                                    200 -> {
                                        requireActivity().runOnUiThread {
                                            Toast.makeText(context, requireActivity().resources.getString(R.string.gained_benefits_purchase), Toast.LENGTH_SHORT).show()
                                        }
                                        loadingAnimation.post {
                                            loadingAnimation.cancelAnimation()
                                            setViewGoneWithAnimation(requireContext(), loadingAnimation)
                                            setViewVisible(successAnimation)
                                            successAnimation.post {
                                                successAnimation.playAnimation()
                                                successAnimation.repeatCount = 0
                                            }
                                        }
                                    }
                                    401 -> {
                                        takeFreshTokens(requireActivity(),urls.refreshURL, requireContext()) { responseBody401, exception401 ->
                                            if (exception401 != null) exception401.printStackTrace()
                                            else {
                                                if (responseBody401 != null) {
                                                    notifyApiAfterPurchase()
                                                }
                                            }
                                        }
                                    }
                                    else -> {
                                        Toast.makeText(context, requireActivity().resources.getString(R.string.unexpected_error_occured_onServer_text), Toast.LENGTH_SHORT).show()
                                        val options = ActivityOptions.makeCustomAnimation(context, R.anim.activity_slide_down, 0)
                                        val intent = Intent(context, ProfileActivity::class.java)
                                        ContextCompat.startActivity(requireContext(), intent, options.toBundle())
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                notifyApiAfterPurchase()
            }

            override fun onError(error: PurchasesError, userCancelled: Boolean) {
                println(error)
                println(userCancelled)
                allowBackPressed = true
            }
        }

        //set storeProducts
        Purchases.sharedInstance.getOfferingsWith({ error -> println(error) }) { offerings ->

            offerings.current!!.availablePackages.takeUnless { it.isEmpty() }?.let {
                for ((index, subsPackage) in offerings.current!!.availablePackages.withIndex()) {
                    when (index) {
                        0 -> revenueCatSubscriptionPackages.weeklySubsPackage = subsPackage
                        1 -> revenueCatSubscriptionPackages.monthlySubsPackage = subsPackage
                        2 -> revenueCatSubscriptionPackages.yearlySubsPackage = subsPackage
                    }
                }
                weeklySubsStoreProduct = revenueCatSubscriptionPackages.weeklySubsPackage?.product?.googleProduct!!
                monthlySubsStoreProduct = revenueCatSubscriptionPackages.monthlySubsPackage?.product?.googleProduct!!
                yearlySubsStoreProduct = revenueCatSubscriptionPackages.yearlySubsPackage?.product?.googleProduct!!
            }

            offerings.getOffering("CoinPurchasesOffering")?.availablePackages?.takeUnless { it.isEmpty() }?.let {
                for ((index, oneTimePackage) in offerings.getOffering("CoinPurchasesOffering")!!.availablePackages.withIndex()) {
                    when (index) {
                        0 -> revenueCatOneTimeCoinPackages.fiveCoinPackage = oneTimePackage
                        1 -> revenueCatOneTimeCoinPackages.twentyFiveCoinPackage = oneTimePackage
                        2 -> revenueCatOneTimeCoinPackages.fiftyCoinPackage = oneTimePackage
                    }
                }
                fiveCoinStoreProduct = revenueCatOneTimeCoinPackages.fiveCoinPackage?.product?.googleProduct!!
                twentyFiveCoinStoreProduct = revenueCatOneTimeCoinPackages.twentyFiveCoinPackage?.product?.googleProduct!!
                fiftyCoinStoreProduct = revenueCatOneTimeCoinPackages.fiftyCoinPackage?.product?.googleProduct!!
            }
        }


        //Set Prices Texts
        fiveCoinPriceText.text = fiveCoinStoreProduct?.price?.formatted
        twentyFiveCoinPriceText.text = twentyFiveCoinStoreProduct?.price?.formatted
        fiftyCoinPriceText.text = fiftyCoinStoreProduct?.price?.formatted
        weeklySubsPriceText.text = weeklySubsStoreProduct?.price?.formatted
        monthlySubsPriceText.text = monthlySubsStoreProduct?.price?.formatted
        yearlySubsPriceText.text = yearlySubsStoreProduct?.price?.formatted


        // Set Display Names
        fiveCoinText.text = fiveCoinStoreProduct?.productDetails?.name
        twentyFiveCoinText.text = twentyFiveCoinStoreProduct?.productDetails?.name
        fiftyCoinText.text = fiftyCoinStoreProduct?.productDetails?.name
        weeklySubsTextView.text = weeklySubsStoreProduct?.productDetails?.name
        monthlySubsTextView.text = monthlySubsStoreProduct?.productDetails?.name
        yearlySubsTextView.text = yearlySubsStoreProduct?.productDetails?.name


        // Make Purchase for MyraCoin
        fiveCoinCard.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(
                "Information about this purchase: " +
                        "\n This purchase is an One Time Purchase " +
                        "\n The cost is 79.99 TRY plus %20 taxes for Turkiye " +
                        "\n Please note that the tax percentage can change accross countries" +
                        "\n You dont have to make a purchase to use the app but coins are needed for some features,(example: get horoscope)" +
                        "\n You will get 5 Myra Coins after this purchase" +
                        "\nClick continue to purchase"
            )
            builder.setPositiveButton("Continue") { _, _ ->
                val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), fiveCoinStoreProduct!!))
                Purchases.sharedInstance.purchase(params, makepurchaseCallback)
                allowBackPressed = false
            }
            builder.setNegativeButton("Cancel", null)
            builder.create().show()
        }


        twentyFiveCoinCard.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(
                "Information about this purchase: " +
                        "\n This purchase is an One Time Purchase " +
                        "\n The cost is 79.99 TRY plus %20 taxes for Turkiye " +
                        "\n Please note that the tax percentage can change accross countries" +
                        "\n You dont have to make a purchase to use the app but coins are needed for some features,(example: get horoscope)" +
                        "\n You will get 25 Myra Coins after this purchase" +
                        "\nClick continue to purchase"
            )
            builder.setPositiveButton("Continue") { _, _ ->
                val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), twentyFiveCoinStoreProduct!!))
                Purchases.sharedInstance.purchase(params, makepurchaseCallback)
                allowBackPressed = false
            }
            builder.setNegativeButton("Cancel", null)
            builder.create().show()
        }


        fiftyCoinCard.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(
                "Information about this purchase: " +
                        "\n This purchase is an One Time Purchase " +
                        "\n The cost is 149.99 TRY plus %20 taxes for Turkiye " +
                        "\n Please note that the tax percentage can change accross countries" +
                        "\n You dont have to make a purchase to use the app but coins are needed for some features,(example: get horoscope)" +
                        "\n You will get 50 Myra Coins after this purchase" +
                        "\nClick continue to purchase"
            )
            builder.setPositiveButton("Continue") { _, _ ->
                val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), fiftyCoinStoreProduct!!))
                Purchases.sharedInstance.purchase(params, makepurchaseCallback)
                allowBackPressed = false
            }
            builder.setNegativeButton("Cancel", null)
            builder.create().show()
        }

        // make purchase for subscriptions
        weeklySubsCard.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(
                "Information about this subscription: \nThis subscription automatically renews itself in weekly periods" +
                        "until you cancel it. \nThe cost is 24.99 TRY plus %20 taxes for Turkiye \n Please note that the tax" +
                        "percentage can change accross countries\nYou dont have to subscribe to use the app, but the subcription has those" +
                        "benefits:\n   Use the app without ads for a week \n Click continue to purchase"
            )
            builder.setPositiveButton("Continue") { _, _ ->
                val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), weeklySubsStoreProduct!!))
                Purchases.sharedInstance.purchase(params, makepurchaseCallback)
                allowBackPressed = false
            }
            builder.setNegativeButton("Cancel", null)
            builder.create().show()
        }


        monthlySubsCard.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(
                "Information about this subscription: \nThis subscription automatically renews itself in monthly periods" +
                        "until you cancel it. \nThe cost is 59.99 TRY plus %20 taxes for Turkiye \n Please note that the tax" +
                        "percentage can change accross countries\nYou dont have to subscribe to use the app, but the subcription has those" +
                        "benefits:\n   Use the app without ads for a month \n Click continue to purchase"
            )
            builder.setPositiveButton("Continue") { _, _ ->
                val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), monthlySubsStoreProduct!!))
                Purchases.sharedInstance.purchase(params, makepurchaseCallback)
                allowBackPressed = false
            }
            builder.setNegativeButton("Cancel", null)
            builder.create().show()
        }


        yearlySubsCard.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(
                "Information about this subscription: \nThis subscription automatically renews itself in yearly periods" +
                        "until you cancel it. \nThe cost is 599.99 TRY plus %20 taxes for Turkiye \n Please note that the tax" +
                        "percentage can change accross countries\nYou dont have to subscribe to use the app, but the subcription has those" +
                        "benefits:\n   Use the app without ads for a year \n Click continue to purchase"
            )
            builder.setPositiveButton("Continue") { _, _ ->
                val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), yearlySubsStoreProduct!!))
                Purchases.sharedInstance.purchase(params, makepurchaseCallback)
                allowBackPressed = false
            }
            builder.setNegativeButton("Cancel", null)
            builder.create().show()
        }

        purchaseBackButton.setOnClickListener{
            requireActivity().onBackPressed()
        }

        return v
    } // end of onCreateView
} // end of class