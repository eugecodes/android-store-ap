package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter;

import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.ItemContract;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OptionAndQuantityModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowPresenter;
import caribouapp.caribou.com.cariboucoffee.order.Order;

/**
 * Created by asegurola on 3/22/18.
 */

public class OptionAndQuantityPresenter extends OOSFlowPresenter<ItemContract.OptionAndQuantityView>
        implements ItemContract.OptionAndQuantityPresenter {

    private OptionAndQuantityModel mModel;

    public OptionAndQuantityPresenter(ItemContract.OptionAndQuantityView view) {
        super(view);
    }

    @Override
    public void setModel(OptionAndQuantityModel model) {
        mModel = model;
        mModel.setSelectedItemModifier(mModel.getSelectedItemModifier() != null
                ? mModel.getSelectedItemModifier() : mModel.getModifierGroup().getDefaultItemModifier());
        mModel.setSelectedItemOption(mModel.getSelectedItemOption() != null
                ? mModel.getSelectedItemOption() : mModel.getModifierGroup().getDefaultItemModifier().getDefaultOption());

        getView().setModifiers(mModel.getModifierGroup().getItemModifiers(), mModel.getSelectedItemModifier());
        getView().setQuantities(mModel.getSelectedItemModifier().getOptions(), mModel.getSelectedItemOption());
    }

    @Override
    public void setSelectedModifier(ItemModifier itemModifier) {
        mModel.setSelectedItemModifier(itemModifier);
        if (itemModifier.equals(mModel.getModifierGroup().getDefaultItemModifier())) {
            mModel.setSelectedItemOption(itemModifier.getDefaultOption());
        } else {
            mModel.setSelectedItemOption(null);
        }
        getView().setModifiers(mModel.getModifierGroup().getItemModifiers(), mModel.getSelectedItemModifier());
        getView().setQuantities(itemModifier.getOptions(), mModel.getSelectedItemOption());
    }

    @Override
    public void setSelectedOption(ItemOption itemOption) {
        mModel.setSelectedItemOption(itemOption);
        getView().setQuantities(mModel.getSelectedItemModifier().getOptions(), mModel.getSelectedItemOption());
    }

    @Override
    public void reset() {
        mModel.setSelectedItemModifier(mModel.getModifierGroup().getDefaultItemModifier());
        mModel.setSelectedItemOption(mModel.getModifierGroup().getDefaultItemModifier().getDefaultOption());

        getView().setModifiers(mModel.getModifierGroup().getItemModifiers(), mModel.getSelectedItemModifier());
        getView().setQuantities(mModel.getModifierGroup().getDefaultItemModifier().getOptions(), mModel.getSelectedItemOption());
    }

    @Override
    public void saveSelection() {
        boolean errorFound = getView().quantityErrorEnabled(!checkQuantity());

        if (errorFound) {
            return;
        }

        getView().goBackWithResult(mModel.getSelectedItemModifier(), mModel.getSelectedItemOption());
    }

    private boolean checkQuantity() {
        return mModel.getSelectedItemOption() != null;
    }

    @Override
    protected void setOrder(Order data) {
        // NO-OP
    }
}
