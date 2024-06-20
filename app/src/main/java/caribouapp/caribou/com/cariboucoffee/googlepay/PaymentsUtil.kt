/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package caribouapp.caribou.com.cariboucoffee.googlepay

import android.app.Activity
import caribouapp.caribou.com.cariboucoffee.AppConstants
import caribouapp.caribou.com.cariboucoffee.BuildConfig
import caribouapp.caribou.com.cariboucoffee.util.AppUtils
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object PaymentsUtil {
    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    private fun gatewayTokenizationSpecification(): JSONObject {
        val params = JSONObject().apply {
            put("gateway", BuildConfig.GOOGLE_PAY_PROCESSOR_NAME)
            put("gatewayMerchantId", BuildConfig.GOOGLE_PAY_GATEWAY_MERCHANT_ID)
        }
        return JSONObject().apply {
            put("type", GooglePayConstants.GATEWAY_TOKENIZATION_TYPE)
            put("parameters", params)
        }
    }

    private val allowedCardNetworks = JSONArray(GooglePayConstants.SUPPORTED_NETWORKS)

    private val allowedCardAuthMethods = JSONArray(GooglePayConstants.SUPPORTED_AUTH_METHODS)

    private fun baseCardPaymentMethod(): JSONObject {
        return JSONObject().apply {
            val parameters = JSONObject().apply {
                put("allowedAuthMethods", allowedCardAuthMethods)
                put("allowedCardNetworks", allowedCardNetworks)
                put("billingAddressRequired", true)
                put("billingAddressParameters", JSONObject().apply {
                    put("format", "FULL")
                })
            }
            put("type", "CARD")
            put("parameters", parameters)
        }
    }

    private fun cardPaymentMethod(): JSONObject {
        val cardPaymentMethod = baseCardPaymentMethod()
        cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification())
        return cardPaymentMethod
    }

    fun isReadyToPayRequest(): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
            }
        } catch (e: JSONException) {
            null
        }
    }

    private val merchantInfo: JSONObject = JSONObject().apply {
        put("merchantId", BuildConfig.GOOGLE_PAY_MERCHANT_ID)
        put("merchantName", BuildConfig.GOOGLE_PAY_MERCHANT_NAME)
    }

    fun createPaymentsClient(activity: Activity): PaymentsClient {
        val env: Int = if (!AppUtils.isProductionBuild()) WalletConstants.ENVIRONMENT_TEST
                        else WalletConstants.ENVIRONMENT_PRODUCTION
        val walletOptions = Wallet.WalletOptions.Builder().setEnvironment(env).build()

        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    @Throws(JSONException::class)
    private fun getTransactionInfo(price: String): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("totalPriceLabel", "Total")
            put("countryCode", AppConstants.US_COUNTRY_ABREV)
            put("currencyCode", AppConstants.CURRENCY_USD)
        }
    }

    // Generate Google Pay Request body to be pass Google Pay API
    fun getPaymentDataRequest(finalPrice: String): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
                put("transactionInfo", getTransactionInfo(finalPrice))
                put("merchantInfo", merchantInfo)

                val shippingAddressParameters = JSONObject().apply {
                    put("phoneNumberRequired", false)
                    put("allowedCountryCodes", JSONArray(listOf(AppConstants.US_COUNTRY_ABREV)))
                }
                put("shippingAddressParameters", shippingAddressParameters)
                put("shippingAddressRequired", true)
                put("callbackIntents", JSONArray(GooglePayConstants.CALLBACK_INTENDS))
            }
        } catch (e: JSONException) {
            null
        }
    }
}
