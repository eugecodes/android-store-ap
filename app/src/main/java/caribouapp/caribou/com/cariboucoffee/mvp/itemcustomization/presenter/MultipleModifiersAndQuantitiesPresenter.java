package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter;

import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.ItemContract;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.MultipleModifiersAndQuantitiesModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowPresenter;
import caribouapp.caribou.com.cariboucoffee.order.Order;

/**
 * Created by asegurola on 3/26/18.
 */

public class MultipleModifiersAndQuantitiesPresenter extends OOSFlowPresenter<ItemContract.MultipleModifiersAndQuantitiesView>
        implements ItemContract.MultipleModifiersAndQuantitiesPresenter {
    private MultipleModifiersAndQuantitiesModel mModel;

    public MultipleModifiersAndQuantitiesPresenter(ItemContract.MultipleModifiersAndQuantitiesView view) {
        super(view);
    }

    @Override
    public void setModel(MultipleModifiersAndQuantitiesModel model) {
        mModel = model;
        getView().updateModifiers(model.getModifierGroup().getItemModifiers());
    }

    @Override
    public void setSelected(ItemModifier itemModifier, ItemOption itemOption) {
        mModel.getSelectedOptions().put(itemModifier, itemOption);
    }

    @Override
    public void reset() {
        mModel.getSelectedOptions().clear();
        getView().updateModifiers(mModel.getModifierGroup().getItemModifiers());
    }

    @Override
    public void save() {
        getView().goBackWithSelection(mModel.getModifierGroup(), mModel.getSelectedOptions());
    }

    @Override
    public Map<ItemModifier, ItemOption> getSelection() {
        return mModel.getSelectedOptions();
    }

    @Override
    protected void setOrder(Order data) {
        // NO-OP
    }
}
