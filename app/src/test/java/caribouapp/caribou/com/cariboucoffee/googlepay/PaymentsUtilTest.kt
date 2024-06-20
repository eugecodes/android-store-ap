package caribouapp.caribou.com.cariboucoffee.googlepay

import android.app.Activity
import caribouapp.caribou.com.cariboucoffee.AppConstants
import caribouapp.caribou.com.cariboucoffee.BuildConfig
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.Test
import org.mockito.Mockito.*

class PaymentsUtilTest {

    @Test
    fun isReadyToPayRequest() {
        // When
        val result: JSONObject? = PaymentsUtil.isReadyToPayRequest()

        // Then
        val expected = JSONObject("""{
    "apiVersionMinor": 0,
    "apiVersion": 2,
    "allowedPaymentMethods": [
      {
        "type": "CARD",
        "parameters": {
          "allowedAuthMethods": ${GooglePayConstants.SUPPORTED_AUTH_METHODS},
          "billingAddressRequired": true,
          "billingAddressParameters": {
            "format": "FULL"
          },
          "allowedCardNetworks": ${GooglePayConstants.SUPPORTED_NETWORKS}
        }
      }
    ]
  }""")
        assertThat(result?.toString()).isEqualTo(expected.toString())
    }

    @Test
    fun createPaymentsClient() {
        mockStatic(Wallet::class.java).use {
            // Given
            val mockActivity = mock(Activity::class.java)
            val mockResult = mock(PaymentsClient::class.java)
            `when`(Wallet.getPaymentsClient(eq(mockActivity), any(Wallet.WalletOptions::class.java)))
                .thenReturn(mockResult)

            // When
            val result = PaymentsUtil.createPaymentsClient(mockActivity)

            // Then
            assertThat(result).isSameAs(mockResult)
        }
    }

    @Test
    fun getPaymentDataRequest() {
        // Given
        val finalPrice = "12.25"

        // When
        val result = PaymentsUtil.getPaymentDataRequest(finalPrice)

        // Then
        val expected = JSONObject("""{
  "apiVersionMinor": 0,
  "apiVersion": 2,
  "allowedPaymentMethods": [
    {
      "type": "CARD",
      "parameters": {
        "allowedAuthMethods": ${GooglePayConstants.SUPPORTED_AUTH_METHODS},
        "billingAddressRequired": true,
        "billingAddressParameters": {
          "format": "FULL"
        },
        "allowedCardNetworks": ${GooglePayConstants.SUPPORTED_NETWORKS}
      },
      "tokenizationSpecification": {
        "type": "${GooglePayConstants.GATEWAY_TOKENIZATION_TYPE}",
        "parameters": {
          "gatewayMerchantId": "${BuildConfig.GOOGLE_PAY_GATEWAY_MERCHANT_ID}",
          "gateway": "${BuildConfig.GOOGLE_PAY_PROCESSOR_NAME}"
        }
      }
    }
  ],
  "merchantInfo": {
    "merchantId": "${BuildConfig.GOOGLE_PAY_MERCHANT_ID}",
    "merchantName": "${BuildConfig.GOOGLE_PAY_MERCHANT_NAME}"
  },
  "shippingAddressParameters": {
    "allowedCountryCodes": [${AppConstants.US_COUNTRY_ABREV}],
    "phoneNumberRequired": false
  },
  "shippingAddressRequired": true,
  "callbackIntents": ${GooglePayConstants.CALLBACK_INTENDS},
  "transactionInfo": {
    "totalPrice": "$finalPrice",
    "countryCode": "${AppConstants.US_COUNTRY_ABREV}",
    "totalPriceLabel": "Total",
    "totalPriceStatus": "FINAL",
    "currencyCode": "${AppConstants.CURRENCY_USD}"
  }
}""")
        assertThat(result?.toString()).isEqualTo(expected.toString())
    }
}
