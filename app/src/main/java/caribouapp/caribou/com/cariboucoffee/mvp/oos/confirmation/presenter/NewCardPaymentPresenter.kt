package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter

import android.text.TextUtils
import android.util.Base64
import androidx.core.text.isDigitsOnly
import caribouapp.caribou.com.cariboucoffee.AppConstants
import caribouapp.caribou.com.cariboucoffee.AppDataStorage
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger
import caribouapp.caribou.com.cariboucoffee.common.CCInformationModel
import caribouapp.caribou.com.cariboucoffee.common.Clock
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback
import caribouapp.caribou.com.cariboucoffee.cybersource.CybersourceConstants
import caribouapp.caribou.com.cariboucoffee.domain.AuthorizeSaleResult
import caribouapp.caribou.com.cariboucoffee.domain.AuthorizeSaleUseCase
import caribouapp.caribou.com.cariboucoffee.fiserv.api.FiservService
import caribouapp.caribou.com.cariboucoffee.fiserv.api.PayGateService
import caribouapp.caribou.com.cariboucoffee.fiserv.model.*
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CardEnum
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowPresenter
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.NewCardPaymentContract
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.PaymentTypeData
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view.AddNewCardPaymentActivity
import caribouapp.caribou.com.cariboucoffee.order.Order
import caribouapp.caribou.com.cariboucoffee.order.OrderService
import caribouapp.caribou.com.cariboucoffee.util.Log
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursCheckUtil
import caribouapp.caribou.com.cariboucoffee.util.StringUtils.*
import caribouapp.caribou.com.cariboucoffee.util.ValidationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.joda.time.DateTimeFieldType
import org.joda.time.LocalTime
import java.nio.charset.StandardCharsets
import java.security.interfaces.RSAPublicKey
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Swapnil on 09/23/22.
 */
class NewCardPaymentPresenter(
    view: NewCardPaymentContract.View?,
    private val mCCModel: CCInformationModel
) :
    OOSFlowPresenter<NewCardPaymentContract.View?>(view),
    NewCardPaymentContract.Presenter {
    @JvmField
    @Inject
    var mSettingsServices: SettingsServices? = null

    @JvmField
    @Inject
    var mUserServices: UserServices? = null

    @JvmField
    @Inject
    var mPayGateService: PayGateService? = null

    @JvmField
    @Inject
    var mFiservService: FiservService? = null

    @JvmField
    @Inject
    var mClock: Clock? = null

    @JvmField
    @Inject
    var mAppDataStorage: AppDataStorage? = null

    @JvmField
    @Inject
    var mEventLogger: EventLogger? = null

    @Inject
    lateinit var authorizeSaleUseCase: AuthorizeSaleUseCase

    @JvmField
    @Inject
    @Named("PayGatePublicKey")
    var mPayGatePubKey: RSAPublicKey? = null

    private val DEVICE_KIND: String = "MOBILE"
    private val TOKEN_TYPE: String = "CLAIM_CHECK_NONCE"
    private val ACCOUNT_TYPE: String = "CREDIT"
    private var mCheckoutModel: CheckoutModel? = null
    val MINUTE_TO_ROUND = 10

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /**
     * Logic for resetting error to false state
     * */
    private fun resetErrors() {
        view!!.nameErrorEnabled(false)
        view!!.cardNumberErrorEnabled(false)
        view!!.expirationMonthErrorEnabled(false)
        view!!.expirationYearErrorEnabled(false)
        view!!.billingAddressErrorEnabled(false)
        view!!.cityErrorEnabled(false)
        view!!.stateErrorEnabled(false)
        view!!.zipErrorEnabled(false)
        view!!.cvvErrorEnabled(false)
        view!!.cvvUnknownErrorEnabled(false)
        view!!.cardTypeUnknownErrorEnabled(false)
    }

    companion object {
        private val TAG = AddNewCardPaymentActivity::class.java.simpleName
    }

    override fun setModel(model: CCInformationModel?) {
    }

    override fun placeOrder(checkoutModel: CheckoutModel?) {
        view!!.setModel(mCCModel)
        resetErrors()
        val unknownCCError: Boolean
        val unknownCvvError: Boolean
        if (mCCModel.ccNumber != null) {
            mCCModel.ccNumber = removeWhiteSpace(mCCModel.ccNumber)
            if (mCCModel.ccNumber != null && mCCModel.ccNumber.isNullOrEmpty()) {
                view!!.cardNumberErrorEnabled(mCCModel.ccNumber.isNullOrEmpty())
                return
            }
            unknownCCError = validateNumber(mCCModel.ccNumber)
            view!!.validCardNumberErrorEnabled(!unknownCCError)
            if (!unknownCCError) {
                return
            }
        }
        if (mCCModel.cvv != null) {
            mCCModel.cvv = removeWhiteSpace(mCCModel.cvv)
            unknownCvvError = validateNumber(mCCModel.cvv)
            view!!.validCvvErrorEnabled(!unknownCvvError)
            if (!unknownCvvError) {
                return
            }
        }
        val errorDetected: Boolean = if (mSettingsServices!!.showAllBillingInfoFieldsOnUI()) {
            validateMinimumFields() && validateAllFields()
        } else {
            validateMinimumFields()
        }
        if (errorDetected) {
            return
        }

        mCheckoutModel = checkoutModel
        val order: Order<*> = mCheckoutModel!!.order
        if (!isValidSelectedPickupTime(order.chosenPickUpTime)) {
            view!!.showNotValidSelectedPickupTime()
            setDefaultPickupTime()
            return
        }
        if (!StoreHoursCheckUtil.isStoreAbleToReceiveOrder(
                mClock,
                order.storeLocation, mSettingsServices!!.orderMinutesBeforeClosing
            )
        ) {
            view!!.showStoreClosedDialog()
            return
        }
        if (order.isBulkOrder && !order.storeLocation.enoughTimeForBulkOrder(
                mClock,
                mSettingsServices!!.bulkPrepTimeInMins, mSettingsServices!!.orderMinutesBeforeClosing
            )
        ) {
            view!!.showStoreNearClosingForBulk()
            return
        }
        if (order.isDelivery) {
            if (!StoreHoursCheckUtil.isDeliveryOpen(mClock, order.storeLocation)) {
                view?.showDeliveryClosedDialog()
                return
            }
            if (!order.isMinimumForDeliveryMet) {
                view!!.showDeliveryMinimumNotMetDialog(mCheckoutModel!!.order.storeLocation.deliveryMinimum)
                return
            }
        }
        if (!order.chosenPickUpTime.isAsap) {
            if (order.chosenPickUpTime.pickUpTime.isBefore(mClock?.currentTime)) {
                view!!.showChooseANewPickUpTimeDialog()
                return
            }
            orderService!!.setPickupTime(order.chosenPickUpTime)
        }
        orderService!!.checkOmsStatus(object : BaseViewResultCallback<ResponseBody?>(view) {
            override fun onFailView(errorCode: Int, errorMessage: String) {
                view!!.showFailToPlaceOrderDialog()
                Log.e(TAG, LogErrorException("On Fail : The oms connection check failed"))
            }

            override fun onErrorView(throwable: Throwable) {
                view!!.showFailToPlaceOrderDialog()
                Log.e(TAG, LogErrorException("On Error : The oms connection check failed"))
            }

            override fun onSuccessViewUpdates(data: ResponseBody?) {
                placeOrderService()
            }
        })
    }

    private fun placeOrderService() {
        orderService!!.placeOrder(object : BaseViewResultCallback<Order<*>?>(view, true, false) {
            override fun onFailView(errorCode: Int, errorMessage: String) {
                if (errorCode == OrderService.ERROR_CODE_DELIVERY_MINIMUM_NOT_MET) {
                    view!!.showDeliveryMinimumNotMetDialog(mCheckoutModel!!.order.storeLocation.deliveryMinimum)
                } else {
                    view!!.showFailToPlaceOrderDialog()
                }
            }

            override fun onErrorView(throwable: Throwable) {
                processPayGateAndFiservPayment()
            }

            override fun onSuccessViewUpdates(data: Order<*>?) {
                processPayGateAndFiservPayment()
            }
        })
    }

    private fun processPayGateAndFiservPayment() {
        view!!.showLoadingLayer(true)
        callAcquireToken()
    }

    private fun callAcquireToken() {
        val deviceId = encryptBase64(mSettingsServices!!.deviceId!!)
        mPayGateService?.acquireToken(object : ResultCallback<FiservAnonResponse?> {
            override fun onSuccess(fiservAnonResponse: FiservAnonResponse?) {
                val accountTokenRequest = generateRequestBodyForAccountToken(fiservAnonResponse)
                callAccountToken(accountTokenRequest, fiservAnonResponse)
            }

            override fun onFail(errorCode: Int, errorMessage: String?) {
                view!!.hideLoadingLayer()
                view!!.showErrorDialog(errorMessage!!)
                Log.e(TAG, LogErrorException("On Fail : Acquire Token connection Failed"))
            }

            override fun onError(error: Throwable?) {
                view!!.hideLoadingLayer()
                view!!.showErrorDialog(mSettingsServices!!.pgCommonErrorMsg)
                Log.e(TAG, LogErrorException("On Error : Acquire Token connection Failed"))
            }
        }, deviceId)
    }

    private fun callAccountToken(accountTokenRequest: AccountTokenRequest?, fiservAnonResponse: FiservAnonResponse?) {
        mFiservService?.acquireAccountToken(object : ResultCallback<AccountTokenResponse?> {
            override fun onSuccess(accountTokenResponse: AccountTokenResponse?) {
                callSaleRequest(mCheckoutModel!!, fiservAnonResponse?.tokenId!!, PaymentTypeData.Credit(accountTokenResponse?.token?.tokenId!!))
            }

            override fun onFail(errorCode: Int, errorMessage: String?) {
                view!!.hideLoadingLayer()
                view!!.showErrorDialog(errorMessage!!)
                Log.e(TAG, LogErrorException("On Fail : Account Token connection Failed"))
            }

            override fun onError(error: Throwable?) {
                view!!.hideLoadingLayer()
                view!!.showErrorDialog(mSettingsServices!!.pgCommonErrorMsg)
                Log.e(TAG, LogErrorException("On Error : Account Token connection Failed"))
            }
        }, accountTokenRequest)
    }

    private fun callSaleRequest(checkOutModel: CheckoutModel, fiservTokenId: String, paymentData: PaymentTypeData) = scope.launch {
        when (val result = authorizeSaleUseCase(checkOutModel.order, fiservTokenId, paymentData)) {
            is AuthorizeSaleResult.Error -> view!!.showErrorDialog(result.message)
            is AuthorizeSaleResult.Successful -> view!!.goToConfirmation(checkOutModel.order)
        }
        view!!.hideLoadingLayer()
    }

    private fun generateRequestBodyForAccountToken(data: FiservAnonResponse?): AccountTokenRequest? {
        val accountTokenRequest = AccountTokenRequest()

        val deviceInfo = DeviceInfo()
        deviceInfo.id = mSettingsServices!!.deviceId
        deviceInfo.kind = DEVICE_KIND

        val token = TokenRequest()
        token.tokenType = TOKEN_TYPE

        val account = Account()
        account.type = ACCOUNT_TYPE
        account.credit = generateCreditCardData(data)

        accountTokenRequest.account = account
        accountTokenRequest.token = token
        accountTokenRequest.deviceInfo = deviceInfo

        return accountTokenRequest
    }

    private fun generateCreditCardData(data: FiservAnonResponse?): Credit? {
        val credit = Credit()
        val expiryDate = ExpiryDate()
        val billingAddress = BillingAddress()

        val rsaPublicKey = readPublicKey(data?.publicKey)

        val cardNumber: ByteArray = mCCModel.ccNumber.toByteArray()

        var checkExpiryMonth = mCCModel.expirationMonth
        if (checkExpiryMonth != null && checkExpiryMonth.length == 1) {
            checkExpiryMonth = "0" + mCCModel.expirationMonth
            mCCModel.expirationMonth = checkExpiryMonth
        }
        val expiryMonth: ByteArray = mCCModel.expirationMonth.toByteArray()
        val expiryYear: ByteArray = mCCModel.expirationYear.toByteArray()
        val cvv: ByteArray = mCCModel.cvv.toByteArray()

        val encryptedCardNumber = encrypt(rsaPublicKey, cardNumber, data?.algorithm)
        val encryptedExpiryMonth = encrypt(rsaPublicKey, expiryMonth, data?.algorithm)
        val encryptedExpiryYear = encrypt(rsaPublicKey, expiryYear, data?.algorithm)
        val encryptedCVV = encrypt(rsaPublicKey, cvv, data?.algorithm)

        val b64EncCardNumber: ByteArray = Base64.encode(encryptedCardNumber, Base64.NO_WRAP)
        val b64EncExpiryMonth: ByteArray = Base64.encode(encryptedExpiryMonth, Base64.NO_WRAP)
        val b64EncExpiryYear: ByteArray = Base64.encode(encryptedExpiryYear, Base64.NO_WRAP)
        val b64EncCVV: ByteArray = Base64.encode(encryptedCVV, Base64.NO_WRAP)

        credit.cardNumber = "ENC_[" + String(b64EncCardNumber, StandardCharsets.UTF_8) + "]"
        credit.nameOnCard = mUserServices?.guestUser?.guestFirstName.plus(" ").plus(mUserServices?.guestUser?.guestLastName)
        credit.securityCode = "ENC_[" + String(b64EncCVV, StandardCharsets.UTF_8) + "]"

        val cardEnum: CardEnum = CardEnum.getCardTypeFromCardNumber(mCCModel.ccNumber, null)
        val cardType = cardEnum.cybersourceCode
        val cardName = getCardNameFromCode(cardType)
        credit.cardType = cardName

        expiryDate.month = "ENC_[" + String(b64EncExpiryMonth, StandardCharsets.UTF_8) + "]"
        expiryDate.year = "ENC_[" + String(b64EncExpiryYear, StandardCharsets.UTF_8) + "]"

        billingAddress.type = "WORK"
        val zip = if (mCCModel.zip.isNullOrEmpty()) "" else mCCModel.zip

        if (mSettingsServices!!.showAllBillingInfoFieldsOnUI()) {
            val streetAddress = if (mCCModel.billingAddress.isNullOrEmpty()) "" else mCCModel.billingAddress
            billingAddress.streetAddress = streetAddress

            val city = if (mCCModel.city.isNullOrEmpty()) "" else mCCModel.city
            billingAddress.locality = city

            val billingAddress2 = if (mCCModel.billingAddress2.isNullOrEmpty()) "" else mCCModel.billingAddress2
            val state = if (mCCModel.state == null) "" else mCCModel.state.abbreviation

            billingAddress.formatted = streetAddress.plus(" ").plus(billingAddress2).plus(" ")
                .plus(city).plus(" ").plus(zip).plus(" ").plus(state)
        } else {
            billingAddress.streetAddress = ""
            billingAddress.locality = ""
            billingAddress.formatted = ""
        }

        billingAddress.region = ""
        billingAddress.postalCode = zip
        billingAddress.country = AppConstants.US_COUNTRY_ABREV
        billingAddress.primary = true

        credit.billingAddress = billingAddress
        credit.expiryDate = expiryDate

        return credit
    }

    private fun getCardNameFromCode(cardType: String?): String {
        return when (cardType) {
            CybersourceConstants.CARD_TYPE_VISA -> "VISA"
            CybersourceConstants.CARD_TYPE_MASTER -> "MASTER"
            CybersourceConstants.CARD_TYPE_AMEX -> "AMEX"
            CybersourceConstants.CARD_TYPE_DISC -> "DISCOVER"
            else -> "OTHER"
        }
    }

    /***
     * Return Boolean based on
     * Firebase flag configured : ShowAllBillingFieldsOnMobileUI
     */
    override fun isAllBillingFieldsShow(): Boolean {
        return mSettingsServices!!.showAllBillingInfoFieldsOnUI()
    }

    /***
     * Get the Stored guest info details
     * and update the UI if it is exists
     */
    override fun getGuestDetails() {
        if (mUserServices!!.guestUser != null) {
            if ((!mUserServices!!.guestUser.guestFirstName.isNullOrEmpty()) &&
                (!mUserServices!!.guestUser.guestLastName.isNullOrEmpty())
            ) {
                view!!.updateNameOnCard(
                    mUserServices!!.guestUser.guestFirstName.plus(" ").plus(mUserServices!!.guestUser.guestLastName)
                )
            }
        }
    }

    override fun cancelOrder() {
        orderService.discard(object : BaseViewResultCallback<Order<*>?>(view) {
            override fun onSuccessViewUpdates(data: Order<*>?) {
                try {
                    mEventLogger!!.logOrderCancelled(AppScreen.ADD_NEW_CARD_PAYMENT, false)
                } catch (e: java.lang.RuntimeException) {
                    Log.e(TAG, LogErrorException("Problems sending orderCancelled analytics", e))
                }
                view!!.goToDashboard()
            }
        })
    }

    /**
     * Validate the minimum fields
     * flag configured in firebase
     * */
    private fun validateMinimumFields(): Boolean {
        return ((view!!.nameErrorEnabled(TextUtils.isEmpty(mCCModel.nameOnCard))
            or view!!.cardNumberErrorEnabled(validateCardNumber())
            or view!!.expirationMonthErrorEnabled(!validateExpirationMonth())
            or view!!.expirationYearErrorEnabled(!validateExpirationYear())
            or view!!.emptyZipErrorEnabled(TextUtils.isEmpty(mCCModel.zip))
            or view!!.cvvErrorEnabled(TextUtils.isEmpty(mCCModel.cvv)))
            or view!!.cardDigitNumberErrorEnabled(validateCardDigitNumber()) ||
            view!!.cvvUnknownErrorEnabled(CardEnum.getCardTypeFromCardNumber(mCCModel.ccNumber, mCCModel.cvv) == CardEnum.UNKNOWN)
            or view!!.cardTypeUnknownErrorEnabled(CardEnum.getCardTypeFromCardNumber(mCCModel.ccNumber, null) == CardEnum.UNKNOWN)
            or view!!.yearExpiredErrorEnabled(validateYearExpires())
            or view!!.monthExpiredErrorEnabled(validateMonthExpires())
            or view!!.zipErrorEnabled(!checkZipCode()))
    }

    private fun validateCardNumber(): Boolean {
        return if (TextUtils.isEmpty(mCCModel.ccNumber)) {
            true
        } else !mCCModel.ccNumber.isDigitsOnly()
    }

    private fun validateCardDigitNumber(): Boolean {
        return mCCModel.ccNumber != null &&
            (mCCModel.ccNumber.length < 15 || mCCModel.ccNumber.length > 16)
    }

    private fun checkZipCode(): Boolean {
        return ValidationUtils.isValidZipCode(mCCModel.zip)
    }

    private fun validateAllFields(): Boolean {
        return true
//        For now as per UI we need to validate only mandatory fields
//        return ((view!!.billingAddressErrorEnabled(TextUtils.isEmpty(mModel.billingAddress))
//            or view!!.cityErrorEnabled(TextUtils.isEmpty(mModel.city))
//            or view!!.stateErrorEnabled(mModel.state == null)))
    }

    /**
     * Validate expiration month value
     * */
    private fun validateExpirationMonth(): Boolean {
        val value = mCCModel.expirationMonth.toIntOrNull()
        return value in 1..12
    }

    /**
     * Validate expiration year value
     * */
    private fun validateExpirationYear(): Boolean {
        return if (TextUtils.isEmpty(mCCModel.expirationYear)) {
            false
        } else try {
            mCCModel.expirationYear.toInt()
            true
        } catch (ex: java.lang.NumberFormatException) {
            false
        }
    }

    private fun validateYearExpires(): Boolean {
        val isYearValid: Boolean = !(mCCModel.expirationYear.isNullOrEmpty()) &&
            mCCModel.expirationYear.toInt() >= view!!.getCurrentYear()
        return !(isYearValid)
    }

    private fun validateMonthExpires(): Boolean {
        var isMonthValid: Boolean = false
        if (!(mCCModel.expirationYear.isNullOrEmpty()) &&
            mCCModel.expirationYear.toInt() == view!!.getCurrentYear()
        ) {
            isMonthValid = mCCModel.expirationMonth.toInt() >= view!!.getCurrentMonth()
        } else if (!(mCCModel.expirationYear.isNullOrEmpty()) &&
            mCCModel.expirationYear.toInt() > view!!.getCurrentYear()
        ) {
            isMonthValid = true
        }
        return !(isMonthValid)
    }

    private fun isValidSelectedPickupTime(pickupTime: PickUpTimeModel): Boolean {
        return pickupTime.isAsap && !mCheckoutModel!!.order.isBulkOrder || !pickupTime.pickUpTime.isBefore(getFirstTimeForPickUp())
    }

    private fun setDefaultPickupTime() {
        mCheckoutModel!!.order.chosenPickUpTime = getFirstPickUpTimeModel()
    }

    private fun getFirstTimeForPickUp(): LocalTime? {
        return roundUp(mClock!!.currentTime)!!
            .plusMinutes(if (isBulkOrder()) mSettingsServices!!.bulkPrepTimeInMins else mSettingsServices!!.pickupTimeFirstOptionOffset)
    }

    private fun getFirstPickUpTimeModel(): PickUpTimeModel? {
        return if (isBulkOrder()) PickUpTimeModel(getFirstTimeForPickUp(), false) else PickUpTimeModel(true)
    }

    private fun roundUp(timeRoundUp: LocalTime): LocalTime? {
        var timeToRoundUp = timeRoundUp
        timeToRoundUp = timeToRoundUp.withMillisOfSecond(0).withSecondOfMinute(0)
        val minutesToRoundUp = MINUTE_TO_ROUND - timeToRoundUp[DateTimeFieldType.minuteOfHour()] % MINUTE_TO_ROUND
        if (minutesToRoundUp != MINUTE_TO_ROUND) {
            timeToRoundUp = timeToRoundUp.plusMinutes(minutesToRoundUp)
        }
        return timeToRoundUp
    }

    private fun isBulkOrder(): Boolean {
        return mSettingsServices!!.isBulkOrderingEnabled && mCheckoutModel!!.order.isBulkOrder
    }

    override fun setOrder(data: Order<out OrderItem<*>>?) {
        TODO("Not yet implemented")
    }

    override fun detachView() {
        super.detachView()
        scope.cancel()
    }

    private fun encryptBase64(input: String?): String? {
        if (input.isNullOrEmpty()) {
            return input
        }

        return encryptToBase64(mPayGatePubKey!!, input.encodeToByteArray(), AppConstants.PAY_GATE_PUBLIC_KEY_ALGORITHM)
    }
}
