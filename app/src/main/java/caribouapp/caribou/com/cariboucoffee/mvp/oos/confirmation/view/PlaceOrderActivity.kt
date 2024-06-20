package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.isVisible
import caribouapp.caribou.com.cariboucoffee.R
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityPlaceOrderBinding
import caribouapp.caribou.com.cariboucoffee.databinding.BindingAdapters
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutGrandTotalCheckoutItemBinding
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutOrderCheckoutItemBinding
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutOrderCheckoutRewardBinding
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutSectionHeaderBinding
import caribouapp.caribou.com.cariboucoffee.design.BagelBrandsTheme
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.view.CheckoutItemsAdapter
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.PaymentTypeData
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.viewmodel.PlaceOrderUiState
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.viewmodel.PlaceOrderViewModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.viewmodel.ProcessStatus
import caribouapp.caribou.com.cariboucoffee.order.DiscountLine
import caribouapp.caribou.com.cariboucoffee.order.Order
import caribouapp.caribou.com.cariboucoffee.order.OrderItemUtil
import caribouapp.caribou.com.cariboucoffee.util.StringUtils
import java.math.BigDecimal

class PlaceOrderActivity : BaseActivity<ActivityPlaceOrderBinding?>() {

    private val viewModel by viewModels<PlaceOrderViewModel> { PlaceOrderViewModel.Factory }

    override fun getLayoutId() = R.layout.activity_place_order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding?.composeView?.setContent {
            BagelBrandsTheme {
                val state by viewModel.state.collectAsState()

                PlaceOrderScreen(
                    uiState = state,
                    onPlaceOrder = viewModel::placeOrder,
                    onErrorDialogDismissed = viewModel::dismissError,
                    onBackPressed = onBackPressedDispatcher::onBackPressed
                )

                LaunchedEffect(state.processStatus) {
                    val processStatus = state.processStatus
                    if (processStatus is ProcessStatus.Loading) {
                        showLoadingLayer(true)
                    } else {
                        hideLoadingLayer()
                    }

                    when (processStatus) {
                        is ProcessStatus.Done -> {
                            startActivity(OrderConfirmationActivity.createIntent(this@PlaceOrderActivity, processStatus.orderData))
                            finish()
                        }

                        is ProcessStatus.Error,
                        is ProcessStatus.Loading,
                        is ProcessStatus.None -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceOrderScreen(
    uiState: PlaceOrderUiState,
    modifier: Modifier = Modifier,
    onPlaceOrder: () -> Unit = {},
    onErrorDialogDismissed: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.place_order_screen_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed,
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
            )
        }
    ) { contentPadding ->
        PlaceOrderScreenContent(
            modifier = Modifier.padding(contentPadding),
            uiState = uiState,
            onPlaceOrder = onPlaceOrder,
            onChangePaymentMethod = onBackPressed,
        )
    }

    if (uiState.processStatus is ProcessStatus.Error) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.sorry)) },
            text = { Text(text = uiState.processStatus.message) },
            confirmButton = {
                Button(onClick = onErrorDialogDismissed) {
                    Text(text = stringResource(id = R.string.okay))
                }
            },
            onDismissRequest = onErrorDialogDismissed,
        )
    }
}

@Composable
private fun PlaceOrderScreenContent(
    uiState: PlaceOrderUiState,
    modifier: Modifier = Modifier,
    onChangePaymentMethod: () -> Unit = {},
    onPlaceOrder: () -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        CheckoutOrderList(
            uiState = uiState,
            modifier = Modifier.weight(1f),
            onChangePaymentMethod = onChangePaymentMethod,
        )
        val context = LocalContext.current
        val totalWithTip = remember(context, uiState.total) {
            StringUtils.formatMoneyAmount(context, uiState.total, null)
        }
        PlaceOrderBottomSheet(
            totalWithTip = totalWithTip,
            onPlaceOrder = onPlaceOrder,
        )
    }
}

@Composable
private fun PlaceOrderScreenHeader(
    paymentMethodLabel: String,
    modifier: Modifier = Modifier,
    onChangePaymentMethod: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.place_order_header, paymentMethodLabel),
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.payment_type_screen),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.place_order_change_payment),
            modifier = Modifier.clickable(onClick = onChangePaymentMethod),
            style = MaterialTheme.typography.caption,
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colors.secondary,
        )
    }
}

@Composable
private fun CheckoutOrderList(
    uiState: PlaceOrderUiState,
    modifier: Modifier = Modifier,
    onChangePaymentMethod: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        content = {
            item {
                PlaceOrderScreenHeader(
                    paymentMethodLabel = stringResource(id = uiState.paymentType.labelRes),
                    onChangePaymentMethod = onChangePaymentMethod,
                )
            }
            item {
                val yourOrderTitle = stringResource(id = R.string.your_order)
                AndroidViewBinding(
                    factory = { inflater, parent, attachToParent ->
                        LayoutSectionHeaderBinding.inflate(inflater, parent, attachToParent).apply {
                            sectionTitle = yourOrderTitle
                        }
                    },
                )
            }
            itemsIndexed(
                items = uiState.orderItems,
                contentType = { _, item ->
                    when (item) {
                        is Order<*> -> CheckoutItemsAdapter.CHECKOUT_TOTAL_AND_TAXES_VIEW
                        is DiscountLine -> CheckoutItemsAdapter.CHECKOUT_DISCOUNT_VIEW
                        else -> CheckoutItemsAdapter.CHECKOUT_ORDER_ITEM_VIEW
                    }
                }
            ) { index, item ->
                when (item) {
                    is Order<*> -> AndroidViewBinding(
                        modifier = Modifier.padding(top = 16.dp),
                        factory = LayoutGrandTotalCheckoutItemBinding::inflate,
                        update = { model = item }
                    )

                    is DiscountLine -> AndroidViewBinding(
                        factory = LayoutOrderCheckoutRewardBinding::inflate,
                        update = {
                            btnRemoveReward.isVisible = false
                            model = item
                        }
                    )

                    else -> OrderItemSection(orderItem = item as OrderItem<*>, index = index)
                }
            }
        },
    )
}

@Composable
fun OrderItemSection(
    orderItem: OrderItem<*>,
    index: Int,
    modifier: Modifier = Modifier,
) {
    AndroidViewBinding(
        modifier = modifier,
        factory = LayoutOrderCheckoutItemBinding::inflate,
        update = {
            val context = root.context
            model = orderItem
            tvOrderItemName.text = if (orderItem.quantity > 1) {
                context.getString(R.string.items_quantity_with_name, orderItem.quantity, orderItem.menuData.name)
            } else {
                orderItem.menuData.name
            }
            @Suppress("UNCHECKED_CAST")
            BindingAdapters.bindProductCustomization(
                tvModifiers,
                OrderItemUtil.getCurrentSelectionDescription(context, orderItem as OrderItem<out OrderItem<Any>>?, false)
            )

            val customizations = BindingAdapters.buildProductCustomization(
                root.context,
                OrderItemUtil.getCurrentSelectionDescription(context, orderItem, false)
            )
            root.contentDescription = if (SizeEnum.ONE_SIZE == orderItem.size) {
                context.getString(
                    R.string.checkout_item_row_cd,
                    index + 1,
                    orderItem.quantity,
                    orderItem.menuData.name,
                    customizations
                )
            } else {
                context.getString(
                    R.string.checkout_item_row_size_cd,
                    index + 1,
                    orderItem.quantity,
                    orderItem.size.toString(),
                    orderItem.menuData.name,
                    customizations
                )
            }
        }
    )
}

@Composable
private fun PlaceOrderBottomSheet(
    totalWithTip: String,
    modifier: Modifier = Modifier,
    onPlaceOrder: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        elevation = 8.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val totalContentDescription = stringResource(R.string.total_cd, totalWithTip)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) { contentDescription = totalContentDescription },
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = stringResource(id = R.string.total))
                Text(text = totalWithTip)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    disabledElevation = 0.dp,
                    hoveredElevation = 0.dp,
                    focusedElevation = 0.dp,
                ),
                onClick = onPlaceOrder,
            ) {
                Text(text = stringResource(id = R.string.place_order).uppercase())
            }
        }
    }
}

private val PaymentTypeData.labelRes: Int
    get() = when (this) {
        is PaymentTypeData.Credit,
        is PaymentTypeData.GooglePay -> throw IllegalStateException("Label not supported")
        is PaymentTypeData.PayPal -> R.string.payment_type_paypal
        is PaymentTypeData.Venmo -> R.string.payment_type_venmo
    }

@Preview(device = Devices.PIXEL_4)
@Composable
private fun PreviewPlaceOrderScreen() {
    BagelBrandsTheme {
        PlaceOrderScreen(
            uiState = PlaceOrderUiState(paymentType = PaymentTypeData.Venmo(""), total = BigDecimal.ONE)
        )
    }
}
