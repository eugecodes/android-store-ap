
package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.MultipleModifiersAndQuantitiesModel;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OptionAndQuantityModel;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowContract;
import caribouapp.caribou.com.cariboucoffee.util.nutritionnallergens.NutritionAllergensAware;

/**
 * Created by asegurola on 3/14/18.
 */

public interface ItemContract {
    interface View extends OOSFlowContract.View, NutritionAllergensAware {
        void setModifiers(List<ModifierGroup> modifiers);

        void enterInlineEditMode(ModifierGroup modifier);

        void enterOptionPlusQuantityEditMode(ModifierGroup modifier);

        void enterMultipleQuantitiesEditMode(ModifierGroup modifier);

        void setCustomization(ModifierGroup modifierGroup, Map<ItemModifier, ItemOption> orderItemSelection);

        void updateSubtotalText(BigDecimal additionalCharges);

        void goToCart();

        void updateSize(SizeEnum size, Set<SizeEnum> availableSizes);

        void updateQuantity(OrderItem orderItem);

        void updateCartItemCount(int size);

        void productDisableError();

        void showModifiers(boolean showItemCustomizationModifiers);

        void showBulkOrderingMessage(boolean showBulk);

        void showAddBulkItemToOrderDialog();

        void showQuantityLimitDialog();
    }

    interface Presenter extends OOSFlowContract.Presenter {

        void init();

        void setModel(OrderItem model, boolean loadModifiers);

        void editModifier(ModifierGroup modifier);

        void setCustomization(ModifierGroup modifierGroup, ItemModifier selectedItemModifier, ItemOption selectedItemOption);

        void setCustomizations(ModifierGroup modifierGroup, Map<ItemModifier, ItemOption> selectedModifiersAndOptions);

        Map<ItemModifier, ItemOption> getCustomizationFor(ModifierGroup modifierGroup);

        void setSize(SizeEnum size);

        void saveItem();

        void addItemToCart();

        void setQuantity(int newValue);

        OrderItem newModel(MenuCardItemModel menuCardItemModel);

        int getPreparationTime();
    }

    interface OptionAndQuantityView extends OOSFlowContract.View {
        void setQuantities(List<ItemOption> quantityOptions, ItemOption itemOption);

        void setModifiers(List<ItemModifier> modifiers, ItemModifier selected);

        boolean quantityErrorEnabled(boolean errorEnabled);

        void goBackWithResult(ItemModifier selectedItemModifier, ItemOption selectedItemOption);
    }

    interface OptionAndQuantityPresenter extends OOSFlowContract.Presenter {
        void setModel(OptionAndQuantityModel model);

        void setSelectedModifier(ItemModifier itemModifier);

        void setSelectedOption(ItemOption itemOption);

        void reset();

        void saveSelection();
    }

    interface MultipleModifiersAndQuantitiesView extends OOSFlowContract.View {

        void goToCart();

        void updateModifiers(List<ItemModifier> modifiers);

        void goBackWithSelection(ModifierGroup modifierGroup, Map<ItemModifier, ItemOption> selectedOptions);
    }

    interface MultipleModifiersAndQuantitiesPresenter extends OOSFlowContract.Presenter {
        void setModel(MultipleModifiersAndQuantitiesModel model);

        void setSelected(ItemModifier itemModifier, ItemOption itemOption);

        void reset();

        void save();

        Map<ItemModifier, ItemOption> getSelection();
    }
}
