package com.utkangul.falci


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.revenuecat.purchases.*
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.models.*
import com.utkangul.falci.internalClasses.AuthenticationFunctions
import com.utkangul.falci.internalClasses.AuthenticationFunctions.PostJsonFunctions.postJsonWithHeader
import com.utkangul.falci.internalClasses.InternalFunctions
import com.utkangul.falci.internalClasses.dataClasses.postHoroscopeData
import com.utkangul.falci.internalClasses.dataClasses.revenueCatOneTimeCoinPackages
import com.utkangul.falci.internalClasses.dataClasses.revenueCatSubscriptionPackages
import com.utkangul.falci.internalClasses.dataClasses.urls
import com.utkangul.falci.internalClasses.statusCode


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


        var weeklySubsStoreProduct: GoogleStoreProduct? = null
        var monthlySubsStoreProduct: GoogleStoreProduct? = null
        var yearlySubsStoreProduct: GoogleStoreProduct? = null


        var fiveCoinStoreProduct: GoogleStoreProduct? = null
        var twentyFiveCoinStoreProduct: GoogleStoreProduct? = null
        var fiftyCoinStoreProduct: GoogleStoreProduct? = null



        //onCompleted , onError situations for purchase callback
        val makepurchaseCallback = object : PurchaseCallback {
            override fun onCompleted(storeTransaction: StoreTransaction, customerInfo: CustomerInfo) {
                println(storeTransaction)
                println(customerInfo)
                println("satin alimi basarili")
                val userId = Purchases.sharedInstance.appUserID
                val revenuecatUserIdJson = AuthenticationFunctions.CreateJsonObject.createJsonObject(
                    "user_id" to userId
                )

                postJsonWithHeader(urls.notifyApiOnPurchaseURL, revenuecatUserIdJson, requireContext())
                { responseBody, exception ->
                    exception?.printStackTrace()
                    println("responseBody $responseBody")
                }
            }
            override fun onError(error: PurchasesError, userCancelled: Boolean) {
                println(error)
                println(userCancelled)
            }
        }


        //set storeProducts
        Purchases.sharedInstance.getOfferingsWith({ error -> println(error) }) { offerings ->
            offerings.current?.availablePackages?.takeUnless { it.isEmpty() }?.let {

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

                println(" yaraamm ${fiveCoinStoreProduct!!.productDetails.name}")

            }
        }


        //Set Prices Texts
        fiveCoinPriceText.text        =fiveCoinStoreProduct?.price?.formatted
        twentyFiveCoinPriceText.text  =twentyFiveCoinStoreProduct?.price?.formatted
        fiftyCoinPriceText.text       =fiftyCoinStoreProduct?.price?.formatted
        weeklySubsPriceText.text      =weeklySubsStoreProduct?.price?.formatted
        monthlySubsPriceText.text     =monthlySubsStoreProduct?.price?.formatted
        yearlySubsPriceText.text      =yearlySubsStoreProduct?.price?.formatted


        // Set Display Names
        fiveCoinText.text = fiveCoinStoreProduct?.productDetails?.name
        twentyFiveCoinText.text = twentyFiveCoinStoreProduct?.productDetails?.name
        fiftyCoinText.text = fiftyCoinStoreProduct?.productDetails?.name
        weeklySubsTextView.text = weeklySubsStoreProduct?.productDetails?.name
        monthlySubsTextView.text = monthlySubsStoreProduct?.productDetails?.name
        yearlySubsTextView.text = yearlySubsStoreProduct?.productDetails?.name


        // Make Purchase for MyraCoin
        fiveCoinCard.setOnClickListener {
            val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), fiveCoinStoreProduct!!))
            Purchases.sharedInstance.purchase(params, makepurchaseCallback)
        }
        twentyFiveCoinCard.setOnClickListener {
            val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), twentyFiveCoinStoreProduct!!))
            Purchases.sharedInstance.purchase(params, makepurchaseCallback)
        }
        fiftyCoinCard.setOnClickListener {
            val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), fiftyCoinStoreProduct!!))
            Purchases.sharedInstance.purchase(params, makepurchaseCallback)
        }

        // make purchase for subscriptions
        weeklySubsCard.setOnClickListener {
            val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), weeklySubsStoreProduct!!))
            Purchases.sharedInstance.purchase(params, makepurchaseCallback)
        }
        monthlySubsCard.setOnClickListener {
            val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), monthlySubsStoreProduct!!))
            Purchases.sharedInstance.purchase(params, makepurchaseCallback)
        }
        yearlySubsCard.setOnClickListener {
            val params = PurchaseParams(PurchaseParams.Builder(requireActivity(), yearlySubsStoreProduct!!))
            Purchases.sharedInstance.purchase(params, makepurchaseCallback)
        }

        return v
    } // end of onCreateView
} // end of class