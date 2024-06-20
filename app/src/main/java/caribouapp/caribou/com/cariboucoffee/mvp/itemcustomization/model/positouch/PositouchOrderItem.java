package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.positouch;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsCustomProduct;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsDisplayStyle;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsEntityRef;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsGroupModifier;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsOrderGroupModifier;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsOrderItem;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsOrderItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.order.ProductCustomizationData;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;

/**
 * Created by asegurola on 3/14/18.
 */

public class PositouchOrderItem extends OrderItem<Long> {

    private static final String TAG = PositouchOrderItem.class.getSimpleName();

    // Special modifier for sizes options
    @SerializedName("sizesModifier")
    private PositouchModifierGroup mSizesGroupModifier;

    @SerializedName("size")
    private PositouchItemModifier mSizeModifier;

    @SerializedName("omsCustomProduct")
    private OmsCustomProduct mOmsCustomProduct;

    @SerializedName("availableSizes")
    private Set<SizeEnum> mAvailableSizes;

    public PositouchOrderItem(MenuCardItemModel menuCardItemModel) {
        super(menuCardItemModel);
        setId(-1L);
    }

    @Override
    public void loadModifiers(ProductCustomizationData productCustomizationData) {
        mOmsCustomProduct = (OmsCustomProduct) productCustomizationData;
        for (OmsGroupModifier omsGroupModifier : mOmsCustomProduct.getOmsGroupModifiers()) {
            if (omsGroupModifier.getOmsDisplayStyle() == OmsDisplayStyle.SIZES) {
                mSizesGroupModifier = new PositouchModifierGroup(omsGroupModifier, mOmsCustomProduct.getDefaultModifiers());
                mSizeModifier = (PositouchItemModifier) mSizesGroupModifier.getDefaultItemModifier();
                if (mSizeModifier == null) {
                    mSizeModifier = (PositouchItemModifier) mSizesGroupModifier.getModifierByName(AppConstants.OMS_SIZE_NAME_MEDIUM);
                }
                if (mSizeModifier == null && !mSizesGroupModifier.getItemModifiers().isEmpty()) {
                    mSizeModifier = (PositouchItemModifier) mSizesGroupModifier.getItemModifiers().get(0);
                }

                super.setSize(SizeEnum.fromOmsOrderName(mSizeModifier.getName()));
                continue;
            }
            ModifierGroup modifierGroup = new PositouchModifierGroup(omsGroupModifier, mOmsCustomProduct.getDefaultModifiers());
            getModifierGroups().add(modifierGroup);
        }
        loadAvailableSizes();
    }

    private void loadAvailableSizes() {
        mAvailableSizes = new HashSet<>();
        if (mSizesGroupModifier == null) {
            mAvailableSizes.add(SizeEnum.ONE_SIZE);
            return;
        }

        for (ItemModifier itemSizeModifier : mSizesGroupModifier.getItemModifiers()) {
            switch (itemSizeModifier.getName()) {
                case AppConstants.OMS_SIZE_NAME_SMALL:
                    mAvailableSizes.add(SizeEnum.SMALL);
                    break;
                case AppConstants.OMS_SIZE_NAME_MEDIUM:
                    mAvailableSizes.add(SizeEnum.MEDIUM);
                    break;
                case AppConstants.OMS_SIZE_NAME_LARGE:
                    mAvailableSizes.add(SizeEnum.LARGE);
                    break;
                case AppConstants.OMS_SIZE_NAME_EXTRA_LARGE:
                    mAvailableSizes.add(SizeEnum.EXTRA_LARGE);
                    break;
            }
        }

        if (mAvailableSizes.isEmpty()) {
            mAvailableSizes.add(SizeEnum.ONE_SIZE);
        }
    }

    @Override
    public Set<SizeEnum> getAvailableSizes() {
        return mAvailableSizes;
    }

    @Override
    public int getMaxQuantity() {
        return AppConstants.ORDER_AHEAD_MAX_ITEM_QUANTITY;
    }

    public void setSize(SizeEnum size) {
        switch (size) {
            case SMALL:
                mSizeModifier = (PositouchItemModifier) mSizesGroupModifier.getModifierByName(AppConstants.OMS_SIZE_NAME_SMALL);
                break;
            case MEDIUM:
                mSizeModifier = (PositouchItemModifier) mSizesGroupModifier.getModifierByName(AppConstants.OMS_SIZE_NAME_MEDIUM);
                break;
            case LARGE:
                mSizeModifier = (PositouchItemModifier) mSizesGroupModifier.getModifierByName(AppConstants.OMS_SIZE_NAME_LARGE);
                break;
            case EXTRA_LARGE:
                mSizeModifier = (PositouchItemModifier) mSizesGroupModifier.getModifierByName(AppConstants.OMS_SIZE_NAME_EXTRA_LARGE);
                break;
            default:
                // Single size
                mSizeModifier = (PositouchItemModifier) mSizesGroupModifier.getItemModifiers().get(0);
        }
        super.setSize(size);
    }

    @Override
    public boolean isNewItem() {
        return getId() == null || getId() == -1L;
    }


    public OmsOrderItem toOms() {
        OmsOrderItem omsOrderItem = new OmsOrderItem();
        omsOrderItem.setId(getId() == -1 ? null : getId());
        omsOrderItem.setProduct(new OmsEntityRef(mOmsCustomProduct.getId(), mOmsCustomProduct.getUuid(), getMenuData().getName()));
        omsOrderItem.setQuantity(getQuantity());

        // Add Size modifier
        if (mSizesGroupModifier != null) {
            OmsOrderGroupModifier sizeGroupModifier = mSizesGroupModifier.toOms();
            OmsOrderItemModifier omsCustomModifier = mSizeModifier.toOms();
            PositouchItemOption positouchSizeOption = (PositouchItemOption) mSizeModifier.getOptions().get(0);
            omsCustomModifier.setOption(positouchSizeOption.toOms());
            sizeGroupModifier.getModifiers().add(omsCustomModifier);
            omsOrderItem.getGroups().add(sizeGroupModifier);
        }

        for (ModifierGroup modifierGroup : getModifierGroups()) {
            PositouchModifierGroup positouchModifierGroup = (PositouchModifierGroup) modifierGroup;

            OmsOrderGroupModifier omsGroupModifier = positouchModifierGroup.toOms();

            for (Map.Entry<ItemModifier, ItemOption> entry : calculateDefaultPlusCustomizations(modifierGroup).entrySet()) {
                PositouchItemModifier itemModifier = (PositouchItemModifier) entry.getKey();
                OmsOrderItemModifier omsCustomModifier = itemModifier.toOms();
                PositouchItemOption positouchItemOption = (PositouchItemOption) entry.getValue();
                omsCustomModifier.setOption(positouchItemOption.toOms());
                omsGroupModifier.getModifiers().add(omsCustomModifier);
            }

            omsOrderItem.getGroups().add(omsGroupModifier);
        }

        return omsOrderItem;
    }


    public void updateTo(OmsOrderItem item) {
        setId(item.getId());
        setPrice(item.getPrice());
        setQuantity(item.getQuantity());

        List<OmsOrderGroupModifier> groupModifiers = item.getGroups();

        if (groupModifiers == null) {
            return;
        }

        updateOptionPrices(groupModifiers);
    }

    private void updateOptionPrices(List<OmsOrderGroupModifier> groupModifiers) {
        for (OmsOrderGroupModifier omsGroupModifier : groupModifiers) {
            if (omsGroupModifier.getOmsDisplayStyle() == OmsDisplayStyle.SIZES) {
                continue;
            }
            ModifierGroup modifierGroup = getModifierGroupById(String.valueOf(omsGroupModifier.getId()));
            if (omsGroupModifier.getModifiers().size() > 0) {
                for (OmsOrderItemModifier omsOrderItemModifier : omsGroupModifier.getModifiers()) {
                    ItemModifier itemModifier = modifierGroup.getModifierById(String.valueOf(omsOrderItemModifier.getId()));
                    ItemOption itemOption = itemModifier.getOptionById(String.valueOf(omsOrderItemModifier.getOption().getId()));
                    itemOption.setPrice(omsOrderItemModifier.getOption().getPrice());
                    Log.d(TAG, "Setting price for additional charges "
                            + omsGroupModifier.getName()
                            + "-" + omsOrderItemModifier.getName() + "-"
                            + omsOrderItemModifier.getOption().getLabel() + " // "
                            + modifierGroup.getName() + "-" + itemModifier.getName() + "-"
                            + itemOption.getLabel() + ": " + omsOrderItemModifier.getOption().getPrice());
                }
            }
        }
    }

    public OmsCustomProduct getOmsCustomProduct() {
        return mOmsCustomProduct;
    }


    public void setCustomizations(OmsOrderItem omsOrderItem, OmsCustomProduct oldCustomProduct) {
        getCustomizations().clear();
        for (OmsOrderGroupModifier omsOrderGroupModifier : omsOrderItem.getGroups()) {
            if (omsOrderGroupModifier.getOmsDisplayStyle() == OmsDisplayStyle.SIZES) {
                if (!omsOrderGroupModifier.getModifiers().isEmpty()) {
                    setSize(SizeEnum.fromOmsOrderName(omsOrderGroupModifier.getModifiers().get(0).getName()));
                }
                continue;
            }

            Map<String, ItemOption> mSelection = new HashMap<>();
            for (OmsOrderItemModifier omsOrderItemModifier : omsOrderGroupModifier.getModifiers()) {

                if (wasDefault(oldCustomProduct,
                        omsOrderGroupModifier.getId(),
                        omsOrderItemModifier.getId(),
                        omsOrderItemModifier.getOption().getId())) {
                    Log.d(TAG, "Was Default: " + GsonUtil.defaultGson().toJson(omsOrderItemModifier));
                    continue;
                }

                ModifierGroup modifierGroup = getModifierGroupById(String.valueOf(omsOrderGroupModifier.getId()));
                if (modifierGroup == null) {
                    throw new RuntimeException("Missing group " + GsonUtil.defaultGson().toJson(omsOrderGroupModifier));
                }

                ItemModifier itemModifier = modifierGroup.getModifierById(String.valueOf(omsOrderItemModifier.getId()));
                if (itemModifier == null) {
                    throw new RuntimeException("Missing item modifier " + GsonUtil.defaultGson().toJson(omsOrderItemModifier));
                }

                ItemOption itemOption = itemModifier.getOptionById(String.valueOf(omsOrderItemModifier.getOption().getId()));
                if (itemOption == null) {
                    throw new RuntimeException("Missing item option " + GsonUtil.defaultGson().toJson(omsOrderItemModifier.getOption()));
                }

                mSelection.put(String.valueOf(omsOrderItemModifier.getId()), itemOption);
            }

            getCustomizations().put(String.valueOf(omsOrderGroupModifier.getId()), mSelection);
        }
    }

    public static boolean wasDefault(OmsCustomProduct oldCustomProduct, long groupId, long modifierId, long optionId) {
        if (oldCustomProduct == null) {
            return false;
        }

        PositouchModifierGroup positouchModifierGroup =
                new PositouchModifierGroup(
                        oldCustomProduct.getGroupById(groupId),
                        oldCustomProduct.getDefaultModifiers());
        ItemModifier itemModifier = positouchModifierGroup.getModifierById(String.valueOf(modifierId));
        ItemOption defaultItemOption = itemModifier.getDefaultOption();

        return defaultItemOption != null && defaultItemOption.getId().equals(String.valueOf(optionId));
    }
}
