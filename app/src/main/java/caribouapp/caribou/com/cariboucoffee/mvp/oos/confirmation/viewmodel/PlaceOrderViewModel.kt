package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import caribouapp.caribou.com.cariboucoffee.SourceApplication
import caribouapp.caribou.com.cariboucoffee.domain.AuthorizeSaleResult
import caribouapp.caribou.com.cariboucoffee.domain.AuthorizeSaleUseCase
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.PaymentTypeData
import caribouapp.caribou.com.cariboucoffee.order.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

internal const val PaymentTypePlaceOrderArg = "payment_type"
internal const val CheckoutModelPlaceOrderArg = "checkout_model"
internal const val TokenIDPlaceOrderArg = "fiserv_token_id"

internal class PlaceOrderViewModel(
    savedStateHandle: SavedStateHandle,
    private val authorizeSaleUseCase: AuthorizeSaleUseCase,
) : ViewModel() {
    private val tokenId = savedStateHandle.get<String>(TokenIDPlaceOrderArg) ?: throw IllegalStateException("Missing token ID arg")
    private val checkoutModel = savedStateHandle.get<CheckoutModel>(CheckoutModelPlaceOrderArg) ?: throw IllegalStateException("Missing checkout model arg")
    private val _state = MutableStateFlow(
        PlaceOrderUiState(
            paymentType = savedStateHandle.get<PaymentTypeData>(PaymentTypePlaceOrderArg)!!,
            total = checkoutModel.order.totalWithTip,
            orderItems = buildList {
                addAll(checkoutModel.order.items)
                addAll(checkoutModel.order.discountLines)
                add(checkoutModel.order)
            }
        )
    )
    val state: StateFlow<PlaceOrderUiState> get() = _state.asStateFlow()

    fun placeOrder() {
        viewModelScope.launch {
            _state.update { it.copy(processStatus = ProcessStatus.Loading) }
            val result = authorizeSaleUseCase(
                order = checkoutModel.order,
                fiservTokenId = tokenId,
                paymentData = state.value.paymentType,
            )
            _state.update {
                it.copy(
                    processStatus = when (result) {
                        is AuthorizeSaleResult.Error -> ProcessStatus.Error(result.message)
                        is AuthorizeSaleResult.Successful -> ProcessStatus.Done(checkoutModel.order)
                    }
                )
            }
        }
    }

    fun dismissError() {
        _state.update { it.copy(processStatus = ProcessStatus.None) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val authorizeSaleUseCase = (this[APPLICATION_KEY] as SourceApplication).component.provideAuthorizeSaleUseCase()
                PlaceOrderViewModel(savedStateHandle, authorizeSaleUseCase)
            }
        }
    }
}

@Immutable
data class PlaceOrderUiState(
    val paymentType: PaymentTypeData,
    val total: BigDecimal,
    val orderItems: List<Any> = emptyList(),
    val processStatus: ProcessStatus = ProcessStatus.None,
)

sealed class ProcessStatus {
    data object Loading : ProcessStatus()
    data object None : ProcessStatus()
    data class Done(val orderData: Order<OrderItem<*>>) : ProcessStatus()
    data class Error(val message: String) : ProcessStatus()
}
