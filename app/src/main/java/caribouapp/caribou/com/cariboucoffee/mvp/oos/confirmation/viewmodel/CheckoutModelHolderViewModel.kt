package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view.PaymentTypeSelectionActivity.StaticIntent.EXTRA_CHECKOUT_MODEL

class CheckoutModelHolderViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val checkoutModel = savedStateHandle.get<CheckoutModel>(EXTRA_CHECKOUT_MODEL)
        ?: checkoutModelCached
        ?: throw IllegalStateException("Missing checkout model arg")

    init {
        checkoutModelCached = checkoutModel
    }

    companion object {
        private var checkoutModelCached: CheckoutModel? = null
    }
}
