package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter;

import androidx.annotation.VisibleForTesting;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.ItemContract;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.positouch.PositouchOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowPresenter;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.ProductCustomizationData;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.nutritionnallergens.NutritionAllergensAware;

/**
 * Created by asegurola on 3/19/18.
 */

public abstract class ItemCustomizationPresenter<ItemModel extends OrderItem>
        extends OOSFlowPresenter<ItemContract.View> implements ItemContract.Presenter {

    private static final String TAG = ItemCustomizationPresenter.class.getSimpleName();

    @Inject
    AppDataStorage mAppDataStorage;

    @Inject
    SettingsServices mSettingsServices;

    @Inject
    EventLogger mEventLogger;

    private Order mOrder;

    private ItemModel mModel;

    public ItemCustomizationPresenter(ItemContract.View view) {
        super(view);
    }

    @Override
    public void init() {
        mAppDataStorage.setOrderLastScreen(AppScreen.ITEM_CUSTOMIZATION);
    }

    @Override
    public void setModel(OrderItem model, boolean loadModifiers) {
        mModel = (ItemModel) model;
        if (loadModifiers) {
            loadModifiers();
        } else {
            getView().setModifiers(mModel.getModifierGroups());
            getView().updateSize(mModel.getSize(), mModel.getAvailableSizes());
            updateQuantities();
            updateCustomizations();
            NutritionAllergensAware.notifyAvailability(mModel.getMenuData(), getView());
        }

        getView()
                .showBulkOrderingMessage(
                        mSettingsServices.isBulkOrderingEnabled()
                                && mModel.getMenuData().isBulk());
    }

    @Override
    public OrderItem newModel(MenuCardItemModel menuCardItemModel) {
        setModel(new PositouchOrderItem(menuCardItemModel), true);
        return mModel;
    }

    @Override
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public void setOrder(Order data) {
        mOrder = data;
        getView().updateCartItemCount(data.getTotalItemsInCart());
    }

    @VisibleForTesting
    public void updateQuantities() {
        setQuantity(mModel.getQuantityLessThanMax());
        getView().updateQuantity(mModel);
    }

    private void updateCustomizations() {
        for (Object modifierGroupObject : mModel.getModifierGroups()) {
            ModifierGroup modifierGroup = (ModifierGroup) modifierGroupObject;
            getView().setCustomization(modifierGroup, mModel.calculateDefaultPlusCustomizations(modifierGroup));
        }
        getView().showModifiers(mSettingsServices.isShowItemCustomizationModifiers() && !mModel.getModifierGroups().isEmpty());
        getView().updateSubtotalText(getModel().getSubtotal());
    }

    private void loadModifiers() {
        String omsProdId = mModel.getMenuData().getOmsProdIdForCurrentLocation();

        if (omsProdId == null) {
            getView().showWarning(R.string.unknown_error);
            Log.e(TAG, new LogErrorException("Product has no OmsProdId data: " + mModel.getMenuData().getName()));
            return;
        }

        getOrderService().getProductCustomizations(omsProdId,
                new BaseViewResultCallback<ProductCustomizationData>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(ProductCustomizationData productCustomizationData) {
                        if (!productCustomizationData.isActive()) {
                            getView().productDisableError();
                            Log.e(TAG, new LogErrorException("Custom product disable : " + mModel.getMenuData().getName()));
                            return;
                        }

                        try {
                            mModel.loadModifiers(productCustomizationData);
                        } catch (RuntimeException e) {
                            Log.e(TAG, new LogErrorException("Couldn't load modifiers for Product: " + mModel.getMenuData().getName()));
                            getView().showError(e);
                            return;
                        }
                        getView().setModifiers(mModel.getModifierGroups());
                        getView().updateSize(mModel.getSize(), mModel.getAvailableSizes());
                        getView().updateQuantity(mModel);
                        updateCustomizations();
                    }
                });
    }

    @Override
    public void editModifier(ModifierGroup modifier) {
        switch (modifier.getModifierUiStyle()) {
            case INLINE:
                getView().enterInlineEditMode(modifier);
                break;
            case OPTION_PLUS_QUANTITY:
                getView().enterOptionPlusQuantityEditMode(modifier);
                break;
            case MULTIPLE_QUANTITIES:
                getView().enterMultipleQuantitiesEditMode(modifier);
                break;
        }
    }

    @Override
    public void setCustomization(ModifierGroup modifierGroup, ItemModifier selectedItemModifier, ItemOption selectedItemOption) {
        mModel.setCustomization(modifierGroup, selectedItemModifier, selectedItemOption);
        getView().setCustomization(modifierGroup, mModel.calculateDefaultPlusCustomizations(modifierGroup));
        getView().updateSubtotalText(getModel().getSubtotal());
    }

    @Override
    public void setCustomizations(ModifierGroup modifierGroup, Map<ItemModifier, ItemOption> selectedModifiersAndOptions) {
        mModel.setCustomizations(modifierGroup, selectedModifiersAndOptions);
        getView().setCustomization(modifierGroup, mModel.calculateDefaultPlusCustomizations(modifierGroup));
        getView().updateSubtotalText(getModel().getSubtotal());
    }

    @Override
    public Map<ItemModifier, ItemOption> getCustomizationFor(ModifierGroup modifierGroup) {
        Map<ItemModifier, ItemOption> customization = mModel.getCustomizationForGroup(modifierGroup);
        return customization == null ? new HashMap<>() : customization;
    }

    @Override
    public void setSize(SizeEnum size) {

        mModel.setSize(size);
        getView().setModifiers(mModel.getModifierGroups());
        updateCustomizations();
        updateQuantities();

    }

    @Override
    public void saveItem() {
        if (mModel.isNewItem()) {
            getOrderService().addItem(mModel, new BaseViewResultCallback<OrderItem>(getView()) {
                @Override
                protected void onSuccessViewUpdates(OrderItem data) {
                    mEventLogger.logAddToCart();
                    getView().goToCart();
                }
            });
        } else {
            getOrderService().updateItem(mModel, new BaseViewResultCallback<OrderItem>(getView()) {
                @Override
                protected void onSuccessViewUpdates(OrderItem data) {
                    getView().goToCart();
                }
            });
        }
    }

    @Override
    public void setQuantity(int newValue) {
        mModel.setQuantity(newValue);
        getView().updateSubtotalText(getModel().getSubtotal());
    }

    @Override
    public void addItemToCart() {
        if (mOrder.orderItemPassesMaxQuantityCheck(mModel, mModel.getQuantity())) {
            getView().showQuantityLimitDialog();
        } else if (mSettingsServices.isBulkOrderingEnabled() && mModel.getMenuData().isBulk()) {
            getView().showAddBulkItemToOrderDialog();
        } else {
            saveItem();
        }
    }

    @Override
    public int getPreparationTime() {
        return mSettingsServices.getBulkPrepTimeInMins();
    }

    public void setAppDataStorage(AppDataStorage appDataStorage) {
        mAppDataStorage = appDataStorage;
    }

    public void setSettingsServices(SettingsServices settingsServices) {
        mSettingsServices = settingsServices;
    }

    public void setEventLogger(EventLogger eventLogger) {
        mEventLogger = eventLogger;
    }

    public Order getOrder() {
        return mOrder;
    }

    public ItemModel getModel() {
        return mModel;
    }
}
