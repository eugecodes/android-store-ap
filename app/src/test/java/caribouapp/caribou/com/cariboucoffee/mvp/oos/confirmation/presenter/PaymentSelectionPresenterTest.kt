package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter

import android.annotation.SuppressLint
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback
import caribouapp.caribou.com.cariboucoffee.domain.AuthorizeSaleResult
import caribouapp.caribou.com.cariboucoffee.domain.AuthorizeSaleUseCase
import caribouapp.caribou.com.cariboucoffee.fiserv.api.PayGateService
import caribouapp.caribou.com.cariboucoffee.fiserv.model.FiservAnonResponse
import caribouapp.caribou.com.cariboucoffee.googlepay.PaymentsUtil
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.PaymentSelectionContract
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.PaymentTypeData
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view.PaymentTypeSelectionActivity
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder
import caribouapp.caribou.com.cariboucoffee.util.StringUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.*
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.security.interfaces.RSAPublicKey
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class PaymentSelectionPresenterTest {
    @Mock
    val mView: PaymentSelectionContract.View? = null

    @Mock
    val mSettingsServices: SettingsServices? = null

    @Mock
    val mPayGateService: PayGateService? = null

    @Mock
    val mPayGatePubKey: RSAPublicKey? = null

    @Mock
    lateinit var authorizeSaleUseCase: AuthorizeSaleUseCase

    private var subject: PaymentSelectionPresenter? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        // Create our testee and initialize it
        val subject = PaymentSelectionPresenter(mView)
        injectMocks(subject)

        // Assign initialized testee for use within tests
        this.subject = subject
    }

    /**
     * Injects mocks into the testee manually, as if Dagger had injected the mocks automatically.
     * We can set up default behaviors here, for example if most of our tests use a specific config
     * for settings services, we can set that here and override it in specific tests.
     */
    private fun injectMocks(subject: PaymentSelectionPresenter) {
        subject.mSettingsServices = mSettingsServices
        subject.mPayGateService = mPayGateService
        subject.mPayGatePubKey = mPayGatePubKey
        subject.authorizeSaleUseCase = authorizeSaleUseCase
    }

    @Test
    fun isGooglePayAvailableAndEnabled_checkTrue_returnTrue() {
        mockStatic(Wallet::class.java).use {
            // Given
            val mockActivity = mock(PaymentTypeSelectionActivity::class.java)
            val mockClient = mock(PaymentsClient::class.java)

            @Suppress("UNCHECKED_CAST")
            val mockTask: Task<Boolean> = mock(Task::class.java) as Task<Boolean>

            @Suppress("UNCHECKED_CAST")
            val callbackCaptor = ArgumentCaptor.forClass(OnCompleteListener::class.java) as ArgumentCaptor<OnCompleteListener<Boolean>>

            `when`(Wallet.getPaymentsClient(any(), any())).thenReturn(mockClient)
            `when`(mockClient.isReadyToPay(any())).thenReturn(mockTask)
            `when`(mockTask.isSuccessful).thenReturn(true)
            `when`(mockTask.result).thenReturn(true)
            `when`(mSettingsServices!!.isGooglePayEnabled).thenReturn(true)
            subject!!.initiatePaymentsClient(mockActivity)
            verify(mockTask).addOnCompleteListener(callbackCaptor.capture())
            callbackCaptor.value.onComplete(mockTask)

            // When
            val result = subject!!.isGooglePayEnabled

            // Then
            assertThat(result).isTrue
        }
    }

    @Test
    fun isGooglePayAvailableAndDisable_checkFalse_returnFalse() {
        mockStatic(Wallet::class.java).use {
            // Given
            //      set up our mocks here to simulate the expected system state
            val mockActivity = mock(PaymentTypeSelectionActivity::class.java)
            val mockClient = mock(PaymentsClient::class.java)

            @Suppress("UNCHECKED_CAST")
            val mockTask: Task<Boolean> = mock(Task::class.java) as Task<Boolean>

            @Suppress("UNCHECKED_CAST")
            val callbackCaptor = ArgumentCaptor.forClass(OnCompleteListener::class.java) as ArgumentCaptor<OnCompleteListener<Boolean>>

            `when`(Wallet.getPaymentsClient(any(), any())).thenReturn(mockClient)
            `when`(mockClient.isReadyToPay(any())).thenReturn(mockTask)
            `when`(mockTask.isSuccessful).thenReturn(true)
            `when`(mockTask.result).thenReturn(true)
            `when`(mSettingsServices!!.isGooglePayEnabled).thenReturn(false)
            subject!!.initiatePaymentsClient(mockActivity)
            verify(mockTask).addOnCompleteListener(callbackCaptor.capture())
            callbackCaptor.value.onComplete(mockTask)

            // When
            val result = subject!!.isGooglePayEnabled

            // Then
            assertThat(result).isFalse
        }
    }

    @Test
    fun isGooglePayEnabled_checkFalse_returnFalse() {
        // Given

        // When
        //      Run any calls on subject necessary for what we're testing
        val result = subject!!.isGooglePayEnabled

        // Then
        assertThat(result).isFalse
        verifyNoInteractions(mSettingsServices)
    }

    @Test
    fun isPayPalEnabled_checkTrue_returnTrue() {
        // Given
        //      set up our mocks here to simulate the expected system state
        `when`(mSettingsServices!!.isPayPalEnabled).thenReturn(true)

        // When
        //      Run any calls on subject necessary for what we're testing
        val result = subject!!.isPayPalEnabled()

        // Then
        //      Verify that we got the expected results
        assertThat(result).isTrue
    }

    @Test
    fun isPayPalEnabled_checkFalse_returnFalse() {
        // Given
        //      set up our mocks here to simulate the expected system state
        `when`(mSettingsServices!!.isPayPalEnabled).thenReturn(false)

        // When
        //      Run any calls on subject necessary for what we're testing
        val result = subject!!.isPayPalEnabled()

        // Then
        //      Verify that we got the expected results
        assertThat(result).isFalse
    }

    @Test
    fun isVenmoEnabled_checkFalse_returnFalse() {
        // Given

        // When
        //      Run any calls on subject necessary for what we're testing
        val result = subject!!.isVenmoEnabled

        // Then
        //      Verify that we got the expected results
        assertThat(result).isFalse
        verifyNoInteractions(mSettingsServices)
    }

    @Test
    fun isPaymentSelectionScreenEnabled_checkTrue_returnTrue() {
        // Given
        //      set up our mocks here to simulate the expected system state
        `when`(mSettingsServices!!.isPaymentSelectionScreenEnabled).thenReturn(true)

        // When
        //      Run any calls on subject necessary for what we're testing
        val result = subject!!.isPaymentSelectionScreenEnabled()

        // Then
        //      Verify that we got the expected results
        assertThat(result).isTrue
    }

    @Test
    fun isPaymentSelectionScreenEnabled_checkFalse_returnFalse() {
        // Given
        //      set up our mocks here to simulate the expected system state
        `when`(mSettingsServices!!.isPaymentSelectionScreenEnabled).thenReturn(false)

        // When
        //      Run any calls on subject necessary for what we're testing
        val result = subject!!.isPaymentSelectionScreenEnabled()

        // Then
        //      Verify that we got the expected results
        assertThat(result).isFalse
    }

    @Test
    fun encryptBase64_encryptsStringsCorrectly() {
        // Given / When
        //      Run any calls on subject necessary for what we're testing
        val result = subject!!.encryptBase64("input")

        // Then
        //      Verify that we got the expected results
        assertThat(result).isEqualTo("ENCRYPTED_STRING")
    }

    @Test
    fun encryptBase64_skipEncryptionOnNullOrEmpty() {
        // Given / When
        val resNull = subject!!.encryptBase64(null)
        val resEmpty = subject!!.encryptBase64("")

        // Then
        assertThat(resNull).isNull()
        assertThat(resEmpty).isEmpty()
    }

    /**
     * Companion object to provide a static mock to allow unit testing without actually invoking encryption,
     * which does not work correctly on the JVM (but works OK on Android)
     */
    companion object {
        private lateinit var mockStatic: MockedStatic<StringUtils>

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            dummyOutEncryptionCall()
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            mockStatic.close()
        }

        @SuppressLint("CheckResult")
        private fun dummyOutEncryptionCall() {
            // Dummies out the encryption call by StringUtils due to testing incompatibility on JUnit
            mockStatic = mockStatic(StringUtils::class.java)
            `when`(StringUtils.encryptToBase64(any(), any(), any())).thenReturn("ENCRYPTED_STRING")
        }
    }

    @Test
    fun initiatePaymentsClient_IsReady() {
        mockStatic(Wallet::class.java).use {
            // Given
            val mockActivity = mock(PaymentTypeSelectionActivity::class.java)
            val mockClient = mock(PaymentsClient::class.java)
            `when`(Wallet.getPaymentsClient(any(), any())).thenReturn(mockClient)

            @Suppress("UNCHECKED_CAST")
            val mockTask: Task<Boolean> = mock(Task::class.java) as Task<Boolean>
            `when`(mockClient.isReadyToPay(any())).thenReturn(mockTask)

            // When
            `when`(mockTask.isSuccessful).thenReturn(true)
            `when`(mockTask.result).thenReturn(true)
            subject!!.initiatePaymentsClient(mockActivity)

            // Then
            val reqCaptor = ArgumentCaptor.forClass(IsReadyToPayRequest::class.java)
            verify(mockClient).isReadyToPay(reqCaptor.capture())
            val expectedRequest = PaymentsUtil.isReadyToPayRequest()!!.toString()
            assertThat(reqCaptor.value.toJson()).isEqualTo(expectedRequest)

            @Suppress("UNCHECKED_CAST")
            val callbackCaptor = ArgumentCaptor.forClass(OnCompleteListener::class.java) as ArgumentCaptor<OnCompleteListener<Boolean>>
            verify(mockTask).addOnCompleteListener(callbackCaptor.capture())

            // Call mock to verify callback behaviors
            callbackCaptor.value.onComplete(mockTask)
            verify(mView!!).showGooglePayButton()
        }
    }

    @Test
    fun initiatePaymentsClient_IsNotReady() {
        mockStatic(Wallet::class.java).use {
            // Given
            val mockActivity = mock(PaymentTypeSelectionActivity::class.java)
            val mockClient = mock(PaymentsClient::class.java)
            `when`(Wallet.getPaymentsClient(any(), any())).thenReturn(mockClient)

            @Suppress("UNCHECKED_CAST")
            val mockTask: Task<Boolean> = mock(Task::class.java) as Task<Boolean>
            `when`(mockClient.isReadyToPay(any())).thenReturn(mockTask)

            // When
            `when`(mockTask.isSuccessful).thenReturn(true)
            `when`(mockTask.result).thenReturn(false)
            subject!!.initiatePaymentsClient(mockActivity)

            // Then
            val reqCaptor = ArgumentCaptor.forClass(IsReadyToPayRequest::class.java)
            verify(mockClient).isReadyToPay(reqCaptor.capture())
            val expectedRequest = PaymentsUtil.isReadyToPayRequest()!!.toString()
            assertThat(reqCaptor.value.toJson()).isEqualTo(expectedRequest)

            @Suppress("UNCHECKED_CAST")
            val callbackCaptor = ArgumentCaptor.forClass(OnCompleteListener::class.java) as ArgumentCaptor<OnCompleteListener<Boolean>>
            verify(mockTask).addOnCompleteListener(callbackCaptor.capture())

            // Call mock to verify callback behaviors
            callbackCaptor.value.onComplete(mockTask)
            verify(mView!!).hideGooglePayButton()
        }
    }

    @Test
    fun initiatePaymentsClient_Fails() {
        mockStatic(Wallet::class.java).use {
            // Given
            val mockActivity = mock(PaymentTypeSelectionActivity::class.java)
            val mockClient = mock(PaymentsClient::class.java)
            `when`(Wallet.getPaymentsClient(any(), any())).thenReturn(mockClient)

            @Suppress("UNCHECKED_CAST")
            val mockTask: Task<Boolean> = mock(Task::class.java) as Task<Boolean>
            `when`(mockClient.isReadyToPay(any())).thenReturn(mockTask)

            // When
            `when`(mockTask.isSuccessful).thenReturn(false)
            `when`(mockTask.exception).thenReturn(RuntimeException("Message"))
            subject!!.initiatePaymentsClient(mockActivity)

            // Then
            val reqCaptor = ArgumentCaptor.forClass(IsReadyToPayRequest::class.java)
            verify(mockClient).isReadyToPay(reqCaptor.capture())
            val expectedRequest = PaymentsUtil.isReadyToPayRequest()!!.toString()
            assertThat(reqCaptor.value.toJson()).isEqualTo(expectedRequest)

            @Suppress("UNCHECKED_CAST")
            val callbackCaptor = ArgumentCaptor.forClass(OnCompleteListener::class.java) as ArgumentCaptor<OnCompleteListener<Boolean>>
            verify(mockTask).addOnCompleteListener(callbackCaptor.capture())

            // Call mock to verify callback behaviors
            callbackCaptor.value.onComplete(mockTask)
            verify(mView!!).hideLoadingLayer()
            verify(mView).showErrorDialog("Message")
        }
    }

    @Test
    fun getPaymentsClient() {
        mockStatic(Wallet::class.java).use {
            // Given
            val mockActivity = mock(PaymentTypeSelectionActivity::class.java)
            val mockClient = mock(PaymentsClient::class.java)
            `when`(Wallet.getPaymentsClient(any(), any())).thenReturn(mockClient)

            @Suppress("UNCHECKED_CAST")
            val mockTask: Task<Boolean> = mock(Task::class.java) as Task<Boolean>
            `when`(mockClient.isReadyToPay(any())).thenReturn(mockTask)

            // When
            subject!!.initiatePaymentsClient(mockActivity)
            val result = subject!!.getPaymentsClient()

            // Then
            assertThat(result).isEqualTo(mockClient)
        }
    }

    @Test
    fun getAccountToken_SuccessfulResponse() {
        // Given
        val givenResponse = FiservAnonResponse()
        val deviceId = UUID.randomUUID().toString()
        `when`(mSettingsServices!!.deviceId).thenReturn(deviceId)

        // When
        subject!!.getAccountToken()

        @Suppress("UNCHECKED_CAST")
        val callbackCaptor = ArgumentCaptor.forClass(ResultCallback::class.java) as ArgumentCaptor<ResultCallback<FiservAnonResponse>>
        verify(mPayGateService!!).acquireToken(callbackCaptor.capture(), eq("ENCRYPTED_STRING"))
        callbackCaptor.value.onSuccess(givenResponse)

        // Then
        verify(mView!!).showLoadingLayer()
        verify(mView).hideLoadingLayer()
        verify(mView).getAccountTokenResponse(eq(givenResponse))
    }

    @Test
    fun getAccountToken_FailResponse() {
        // Given
        val deviceId = UUID.randomUUID().toString()
        `when`(mSettingsServices!!.deviceId).thenReturn(deviceId)

        // When
        subject!!.getAccountToken()

        @Suppress("UNCHECKED_CAST")
        val callbackCaptor = ArgumentCaptor.forClass(ResultCallback::class.java) as ArgumentCaptor<ResultCallback<FiservAnonResponse>>
        verify(mPayGateService!!).acquireToken(callbackCaptor.capture(), eq("ENCRYPTED_STRING"))
        callbackCaptor.value.onFail(401, "Failure")

        // Then
        verify(mView!!).showLoadingLayer()
        verify(mView).hideLoadingLayer()
        verify(mView).showErrorDialog("Failure")
    }

    @Test
    fun getAccountToken_ErrorResponse() {
        // Given
        val deviceId = UUID.randomUUID().toString()
        `when`(mSettingsServices!!.deviceId).thenReturn(deviceId)
        `when`(mSettingsServices.pgCommonErrorMsg).thenReturn("CommonErrMsg")

        // When
        subject!!.getAccountToken()

        @Suppress("UNCHECKED_CAST")
        val callbackCaptor = ArgumentCaptor.forClass(ResultCallback::class.java) as ArgumentCaptor<ResultCallback<FiservAnonResponse>>
        verify(mPayGateService!!).acquireToken(callbackCaptor.capture(), eq("ENCRYPTED_STRING"))
        callbackCaptor.value.onError(RuntimeException())

        // Then
        verify(mView!!).showLoadingLayer()
        verify(mView).hideLoadingLayer()
        verify(mView).showErrorDialog("CommonErrMsg")
    }

    @Test
    fun callSaleRequest_SuccessfulCall() = runTest {
        // Given
        val checkoutModel = mock(CheckoutModel::class.java)
        val fiservTokenId = "token"
        val paymentTypeData = PaymentTypeData.Venmo("nonce")
        val order = NcrOrder(StoreLocation())
        `when`(checkoutModel.order).thenReturn(order)
        `when`(authorizeSaleUseCase.invoke(order, fiservTokenId, paymentTypeData)).thenReturn(AuthorizeSaleResult.Successful)

        // When
        subject!!.callSaleRequest(checkoutModel, fiservTokenId, paymentTypeData)

        // Then
        verify(mView!!).showLoadingLayer(true)
        verify(mView).goToConfirmation(eq(order))
        verify(mView).hideLoadingLayer()
    }

    @Test
    fun callSaleRequest_ErrorCall() = runTest {
        // Given
        val checkoutModel = mock(CheckoutModel::class.java)
        val fiservTokenId = "token"
        val paymentTypeData = PaymentTypeData.Venmo("nonce")
        val order = NcrOrder(StoreLocation())
        val errorMessage = "error"
        `when`(checkoutModel.order).thenReturn(order)
        `when`(authorizeSaleUseCase.invoke(order, fiservTokenId, paymentTypeData)).thenReturn(AuthorizeSaleResult.Error(errorMessage))

        // When
        subject!!.callSaleRequest(checkoutModel, fiservTokenId, paymentTypeData)

        // Then
        verify(mView!!).showLoadingLayer(true)
        verify(mView).showErrorDialog(errorMessage)
        verify(mView).hideLoadingLayer()
    }
}
