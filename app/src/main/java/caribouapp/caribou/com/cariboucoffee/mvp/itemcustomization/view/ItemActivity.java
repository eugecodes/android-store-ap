package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.common.DialogUtil;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.ItemContract;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.QuantitySelectorModel;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.ncr.NcrItemCustomizationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuNutritionActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.SizeSelectorView;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowActivity;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import icepick.Icepick;
import icepick.State;

public class ItemActivity extends OOSFlowActivity<ActivityItemBinding>
        implements ItemContract.View, ItemModifierGroupAdapter.ItemModifierListener {

    @State
    OrderItem mModel;
    private ItemContract.Presenter mPresenter;
    private ItemModifierGroupAdapter mModifiersAdapter;

    public static Intent createIntent(Context context, MenuCardItemModel menuProduct, OrderItem orderItem) {
        Intent intent = new Intent(context, ItemActivity.class);
        intent.putExtra(AppConstants.EXTRA_MENU_PRODUCT, menuProduct);
        intent.putExtra(AppConstants.EXTRA_ORDER_ITEM, orderItem);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_item;
    }

    private ItemContract.Presenter buildPresenter() {
        NcrItemCustomizationPresenter ncrItemCustomizationPresenter = new NcrItemCustomizationPresenter(this);
        SourceApplication.get(this).getComponent().inject(ncrItemCustomizationPresenter);
        return ncrItemCustomizationPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mPresenter = buildPresenter();
        mPresenter.init();
        setOOSFlowPresenter(mPresenter);

        mModifiersAdapter = new ItemModifierGroupAdapter();
        getBinding().contentIncluded.rvModifiers.setNestedScrollingEnabled(false);
        getBinding().contentIncluded.rvModifiers
                .setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        getBinding().contentIncluded.rvModifiers.setAdapter(mModifiersAdapter);

        mModifiersAdapter.setListener(this);

        getBinding().contentIncluded.tvNutritionLink
                .setOnClickListener(view -> startActivity(
                        MenuNutritionActivity.createIntent(ItemActivity.this, mModel.getMenuData(), mModel.getSize())));

        getBinding().contentIncluded.btnAddToOrder.setOnClickListener(view -> mPresenter.addItemToCart());

        getBinding().cvCart.setOnClickListener((view) -> goToCart());

        getBinding().contentIncluded.ssvSize.setItemSizeListener(size -> mPresenter.setSize(size));

        getBinding().contentIncluded.iqvItemQuantity.setQuantityListener(newValue -> mPresenter.setQuantity(newValue));


        OrderItem orderItem = getOrderItem();
        if (mModel == null && orderItem != null) {
            // We are updating an existing order item;
            mModel = orderItem;
            mPresenter.setModel(mModel, false);
        } else if (mModel == null) {
            // Adding a new item
            mModel = mPresenter.newModel(getProductDetailsFromIntent());
        } else {
            // Activity being restored from saved instance state
            mPresenter.setModel(mModel, false);
        }

        getBinding().setItem(mModel);

        mPresenter.loadOrder();

    }

    @Override
    public void showBulkOrderingMessage(boolean showBulk) {
        getBinding().contentIncluded.tvBulkItemDescription.setVisibility(showBulk ? View.VISIBLE : View.GONE);
        getBinding().contentIncluded.tvBulkItemDescription.
                setText(getResources().getQuantityString(R.plurals.bulk_order_item_description,
                        mPresenter.getPreparationTime(), mPresenter.getPreparationTime()));
    }

    private OrderItem getOrderItem() {
        return (OrderItem) getIntent().getSerializableExtra(AppConstants.EXTRA_ORDER_ITEM);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onResume() {
        mPresenter.loadOrder();
        super.onResume();
    }

    public MenuCardItemModel getProductDetailsFromIntent() {
        return (MenuCardItemModel) getIntent().getSerializableExtra(AppConstants.EXTRA_MENU_PRODUCT);
    }

    @Override
    public void setModifiers(List<ModifierGroup> modifiers) {
        mModifiersAdapter.setModifiers(modifiers);
        getBinding().contentIncluded.inclHeaderText.tvHeader.setVisibility(modifiers.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void enterInlineEditMode(ModifierGroup modifier) {
        mModifiersAdapter.exitInlineEditMode();
        mModifiersAdapter.enterInlineEditMode(modifier);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mModifiersAdapter.exitInlineEditMode();
    }

    @Override
    public void enterOptionPlusQuantityEditMode(ModifierGroup modifierGroup) {
        ItemModifier itemModifier = null;
        ItemOption itemOption = null;

        Map<ItemModifier, ItemOption> selection = mPresenter.getCustomizationFor(modifierGroup);
        if (selection != null && !selection.isEmpty()) {
            Map.Entry<ItemModifier, ItemOption> entry = selection.entrySet().iterator().next();
            itemModifier = entry.getKey();
            itemOption = entry.getValue();
        }

        startActivityForResult(
                OptionAndQuantityActivity
                        .createIntent(this, modifierGroup,
                                itemModifier,
                                itemOption),
                AppConstants.REQUEST_CODE_SELECT_OPTION_AND_QUANTITY);
    }

    @Override
    public void enterMultipleQuantitiesEditMode(ModifierGroup modifierGroup) {
        startActivityForResult(
                MultipleModifiersAndQuantitiesActivity
                        .createIntent(this, modifierGroup, mPresenter.getCustomizationFor(modifierGroup)),
                AppConstants.REQUEST_CODE_SELECT_MULTIPLE_OPTIONS_AND_QUANTITIES);
    }


    @Override
    public void setCustomization(ModifierGroup modifierGroup, Map<ItemModifier, ItemOption> orderItemSelections) {
        mModifiersAdapter.setCustomization(modifierGroup, orderItemSelections);
    }

    @Override
    public void updateSubtotalText(BigDecimal subtotal) {
        if (BigDecimal.ZERO.compareTo(subtotal) < 0) {
            getBinding().contentIncluded.btnAddToOrder.setContentDescription(getString(R.string.add_to_order_price,
                    StringUtils.formatMoneyAmount(this, subtotal)));
            getBinding().contentIncluded.btnAddToOrder.setText(getString(R.string.add_to_order_price,
                    StringUtils.formatMoneyAmount(this, subtotal)));
        } else {
            getBinding().contentIncluded.btnAddToOrder.setText(getString(R.string.add_to_order));
        }
    }

    @Override
    public void goToCart() {
        setResult(AppConstants.RESULT_CODE_GO_TO_CART);
        finish();
    }

    @Override
    public void updateSize(SizeEnum size, Set<SizeEnum> availableSizes) {
        if (size != null && SizeEnum.ONE_SIZE != size) {
            SizeSelectorView sizeSelectorView = getBinding().contentIncluded.ssvSize;
            sizeSelectorView.setEnabledSizes(availableSizes);
            sizeSelectorView.setSize(size);
        }
    }

    @Override
    public void updateQuantity(OrderItem orderItem) {
        QuantitySelectorModel quantitySelectorModel = new QuantitySelectorModel();
        quantitySelectorModel.setMin(1);
        quantitySelectorModel.setMax(orderItem.getMaxQuantity());
        quantitySelectorModel.setQuantity(orderItem.getQuantityLessThanMax());
        getBinding().contentIncluded.iqvItemQuantity.setModel(quantitySelectorModel);
    }

    @Override
    public void updateCartItemCount(int size) {
        getBinding().cvCart.setItemCount(size);
    }

    @Override
    public void productDisableError() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sorry)
                .setMessage(R.string.sorry_you_cant_add_this_product)
                .setPositiveButton(R.string.okay, (dialog, which) -> finish())
                .show();
    }

    @Override
    public void showModifiers(boolean showItemCustomizationModifiers) {
        getBinding().contentIncluded.inclHeaderText.getRoot()
                .setVisibility(showItemCustomizationModifiers ? View.VISIBLE : View.GONE);
        getBinding().contentIncluded.rvModifiers.setVisibility(showItemCustomizationModifiers ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onNoNutritionAllergensAvailable() {
        getBinding().contentIncluded.tvNutritionLink.setVisibility(View.GONE);
    }

    @Override
    public void onNoAllergensAvailable() {
        getBinding().contentIncluded.tvNutritionLink.setText(R.string.view_nutrition);
    }

    @Override
    public void onNoNutritionAvailable() {
        getBinding().contentIncluded.tvNutritionLink.setText(R.string.view_allergens);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void itemSelected(ModifierGroup modifier) {
        mPresenter.editModifier(modifier);
    }

    @Override
    public void inlineItemOptionSelected(ModifierGroup modifier, ItemModifier itemModifier) {
        mPresenter.setCustomization(modifier, itemModifier, itemModifier.getOptions().get(0));
        mModifiersAdapter.exitInlineEditMode();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_SELECT_OPTION_AND_QUANTITY && resultCode == RESULT_OK) {
            ModifierGroup selectedModifierGroup = (ModifierGroup) data.getSerializableExtra(AppConstants.EXTRA_MODIFIER_GROUP);
            ItemModifier selectedItemModifier = (ItemModifier) data.getSerializableExtra(AppConstants.EXTRA_ITEM_MODIFIER);
            ItemOption selectedItemOption = (ItemOption) data.getSerializableExtra(AppConstants.EXTRA_ITEM_OPTION);
            mPresenter.setCustomization(selectedModifierGroup, selectedItemModifier, selectedItemOption);
        } else if (requestCode == AppConstants.REQUEST_CODE_SELECT_MULTIPLE_OPTIONS_AND_QUANTITIES && resultCode == RESULT_OK) {
            ModifierGroup selectedModifierGroup = (ModifierGroup) data.getSerializableExtra(AppConstants.EXTRA_MODIFIER_GROUP);
            Map<ItemModifier, ItemOption> selectedModifiersAndOptions =
                    (Map<ItemModifier, ItemOption>) data.getSerializableExtra(AppConstants.EXTRA_MULTIPLE_MODIFIER_OPTIONS);
            mPresenter.setCustomizations(selectedModifierGroup, selectedModifiersAndOptions);
        } else if (requestCode == AppConstants.REQUEST_CODE_SELECT_MULTIPLE_OPTIONS_AND_QUANTITIES
                && resultCode == AppConstants.RESULT_CODE_GO_TO_CART) {
            goToCart();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showAddBulkItemToOrderDialog() {
        DialogUtil.showStartingBulkOrderDialog(this, (dialog, which) -> dialog.dismiss(), (dialog, which) -> mPresenter.saveItem());
    }

    @Override
    public void showQuantityLimitDialog() {
        DialogUtil.showDismissableDialog(this,
                R.string.error_product_quantity_limit_reached_title,
                R.string.error_product_quantity_limit_reached_desc);
    }
}
