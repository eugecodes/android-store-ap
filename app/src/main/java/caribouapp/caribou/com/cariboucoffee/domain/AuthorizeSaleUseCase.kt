package caribouapp.caribou.com.cariboucoffee.domain

import caribouapp.caribou.com.cariboucoffee.AppConstants
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback
import caribouapp.caribou.com.cariboucoffee.fiserv.api.PayGateService
import caribouapp.caribou.com.cariboucoffee.fiserv.model.Address
import caribouapp.caribou.com.cariboucoffee.fiserv.model.BillingAddress
import caribouapp.caribou.com.cariboucoffee.fiserv.model.Customer
import caribouapp.caribou.com.cariboucoffee.fiserv.model.Fiserv
import caribouapp.caribou.com.cariboucoffee.fiserv.model.SaleRequest
import caribouapp.caribou.com.cariboucoffee.fiserv.model.SaleResponse
import caribouapp.caribou.com.cariboucoffee.fiserv.model.Store
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.PaymentTypeData
import caribouapp.caribou.com.cariboucoffee.order.Order
import caribouapp.caribou.com.cariboucoffee.order.PickupData
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder
import caribouapp.caribou.com.cariboucoffee.util.Log
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException
import caribouapp.caribou.com.cariboucoffee.util.StringUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.security.interfaces.RSAPublicKey
import kotlin.coroutines.resume

class AuthorizeSaleUseCase(
    private val payGatePubKey: RSAPublicKey,
    private val payGateService: PayGateService,
    private val userServices: UserServices,
    private val settingsServices: SettingsServices,
    private val eventLogger: EventLogger,
    private val tagger: Tagger,
) {
    suspend operator fun invoke(
        order: Order<*>,
        fiservTokenId: String,
        paymentData: PaymentTypeData,
    ): AuthorizeSaleResult = suspendCancellableCoroutine { continuation ->
        payGateService.runCatching {
            startAuthorizeSale(object : ResultCallback<SaleResponse> {
                override fun onSuccess(data: SaleResponse?) {
                    continuation.resume(AuthorizeSaleResult.Successful.also { setupOrder(order) })
                }

                override fun onFail(errorCode: Int, errorMessage: String?) {
                    continuation.resume(AuthorizeSaleResult.Error(errorMessage ?: settingsServices.pgCommonErrorMsg))
                    Log.e("AuthorizeSaleUseCase", LogErrorException("On Fail : Sale Request connection Failed"))
                }

                override fun onError(error: Throwable?) {
                    continuation.resume(AuthorizeSaleResult.Error(settingsServices.pgCommonErrorMsg))
                    Log.e("AuthorizeSaleUseCase", LogErrorException("On Error : Sale Request connection Failed"))
                }
            }, generateRequestBodySale(order, paymentData, fiservTokenId))
        }.onFailure {
            if (it is CancellationException) {
                throw it
            } else {
                continuation.resume(AuthorizeSaleResult.Error(settingsServices.pgCommonErrorMsg))
            }
        }
    }

    private fun generateRequestBodySale(
        order: Order<*>,
        paymentData: PaymentTypeData,
        fiservTokenId: String,
    ) = SaleRequest().apply {
        val ncrOrder = order as NcrOrder
        chargeAmount = ncrOrder.totalWithTip.toString()
        ncrOrderId = ncrOrder.ncrOrderId
        deviceId = settingsServices.deviceId.encryptBase64(payGatePubKey)

        customer = generateCustomerObject()
        fiserv = generateFiservObject(fiservTokenId, paymentData)
        store = getStoreAddress(order.storeLocation)
    }

    private fun generateCustomerObject() = Customer().apply {
        val guestUser = userServices.guestUser
        firstName = guestUser.guestFirstName?.encryptBase64(payGatePubKey)
        lastName = guestUser.guestLastName?.encryptBase64(payGatePubKey)
        phoneNumber = guestUser.guestPhoneNumber?.encryptBase64(payGatePubKey)
        email = guestUser.guestEmailId?.encryptBase64(payGatePubKey)
    }

    private fun getStoreAddress(storeLocation: StoreLocation) = Store().apply {
        mainPhone = storeLocation.phone
        timeZone = storeLocation.getmTimeZone()
        address = Address().apply {
            street1 = storeLocation.addressShort
            city = storeLocation.locatity
            state = storeLocation.state
            zip = storeLocation.zipcode
        }
    }

    private fun generateFiservObject(fiservTokenId: String, paymentData: PaymentTypeData) = Fiserv().apply {
        clientToken = fiservTokenId.encryptBase64(payGatePubKey)
        fundingSourceType = paymentData.type

        when (paymentData) {
            is PaymentTypeData.GooglePay -> {
                val dataObject = JSONObject(paymentData.data)
                val billingAddress = dataObject.getJSONObject("paymentMethodData").getJSONObject("info").getJSONObject("billingAddress")
                val tokenDetails = dataObject.getJSONObject("paymentMethodData").getJSONObject("tokenizationData")
                val tokenData = tokenDetails.getString("token")

                version = JSONObject(tokenData).getString("protocolVersion")
                data = JSONObject(tokenData).getString("signedMessage")
                signature = JSONObject(tokenData).getString("signature")
                this.billingAddress = generateBillingAddressRequestBody(billingAddress)
            }

            is PaymentTypeData.PayPal -> {
                payerId = paymentData.payerId
                nonceToken = paymentData.accountNonce.encryptBase64(payGatePubKey)
            }

            is PaymentTypeData.Venmo -> {
                nonceToken = paymentData.accountNonce.encryptBase64(payGatePubKey)
            }

            is PaymentTypeData.Credit -> {
                nonceToken = paymentData.nonceToken.encryptBase64(payGatePubKey)
            }
        }
    }

    private fun generateBillingAddressRequestBody(billingAddress: JSONObject) = BillingAddress().apply {
        primary = true
        type = "Work"
        streetAddress = billingAddress.getString("address1")
        locality = billingAddress.getString("locality")
        region = billingAddress.getString("administrativeArea")
        postalCode = billingAddress.getString("postalCode")
        country = billingAddress.getString("countryCode")
    }

    private fun setupOrder(order: Order<*>) {
        val asapOrder = order.chosenPickUpTime == null || order.chosenPickUpTime.isAsap
        eventLogger.logOrderCompleted(order.isFromReorder, order.isEdited, asapOrder, order.totalWithTip)
        trackOrderPickUpType(order.pickupData)
        trackBulkOrder(order)
        tagUserWithOrder(order)
        if (!asapOrder) {
            eventLogger.logOrderNotAsap()
        }
    }

    private fun trackOrderPickUpType(pickupData: PickupData?) {
        if (pickupData == null || pickupData.yextPickupType == null) {
            return
        }
        val yextPickupType = pickupData.yextPickupType
        eventLogger.logOrderPickUpType(yextPickupType)
        tagger.tagPickUpOrder(yextPickupType)
    }

    private fun trackBulkOrder(order: Order<*>) {
        if (!order.isBulkOrder) {
            return
        }
        tagger.tagBulkOrder()
        eventLogger.logBulkOrder()
    }

    private fun tagUserWithOrder(order: Order<*>) {
        tagger.tagOrderAheadUser()
        if (order.chosenPickUpTime == null || order.chosenPickUpTime.isAsap) {
            return
        }
        tagger.tagOrderAheadPickup()
    }
}

sealed class AuthorizeSaleResult {
    data object Successful : AuthorizeSaleResult()
    data class Error(val message: String) : AuthorizeSaleResult()
}

private fun String.encryptBase64(pubKey: RSAPublicKey): String =
    StringUtils.encryptToBase64(pubKey, encodeToByteArray(), AppConstants.PAY_GATE_PUBLIC_KEY_ALGORITHM)
