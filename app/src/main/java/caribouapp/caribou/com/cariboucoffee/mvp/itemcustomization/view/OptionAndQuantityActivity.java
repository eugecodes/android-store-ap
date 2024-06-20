package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityOptionAndQuantityBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.ItemContract;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OptionAndQuantityModel;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.OptionAndQuantityPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowActivity;
import icepick.Icepick;
import icepick.State;

public class OptionAndQuantityActivity extends OOSFlowActivity<ActivityOptionAndQuantityBinding> implements ItemContract.OptionAndQuantityView {

    @State
    OptionAndQuantityModel mModel;
    private ModifierVerticalAdapter mModifierVerticalAdapter;

    private ItemOptionsAdapter mItemOptionsAdapter;

    private ItemContract.OptionAndQuantityPresenter mPresenter;

    public static Intent createIntent(Context context, ModifierGroup modifierGroup, ItemModifier selectedItemModifier, ItemOption itemOption) {
        Intent intent = new Intent(context, OptionAndQuantityActivity.class);
        intent.putExtra(AppConstants.EXTRA_MODIFIER_GROUP, modifierGroup);
        intent.putExtra(AppConstants.EXTRA_ITEM_MODIFIER, selectedItemModifier);
        intent.putExtra(AppConstants.EXTRA_ITEM_OPTION, itemOption);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_option_and_quantity;
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

        mModifierVerticalAdapter = new ModifierVerticalAdapter();
        getBinding().contentIncluded.rvModifiers.setAdapter(mModifierVerticalAdapter);
        getBinding().contentIncluded.rvModifiers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mModifierVerticalAdapter.setModifierListener(itemModifier -> mPresenter.setSelectedModifier(itemModifier));

        mItemOptionsAdapter = new ItemOptionsAdapter();
        getBinding().contentIncluded.rvQuantityOptions.setAdapter(mItemOptionsAdapter);
        getBinding().contentIncluded.rvQuantityOptions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mItemOptionsAdapter.setOptionListener(itemOption -> mPresenter.setSelectedOption(itemOption));

        OptionAndQuantityPresenter presenter = new OptionAndQuantityPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;
        setOOSFlowPresenter(presenter);
        if (mModel == null) {
            mModel = new OptionAndQuantityModel();
            mModel.setModifierGroup(getModifierGroup());
            mModel.setSelectedItemModifier(getSelectedItemModifier());
            mModel.setSelectedItemOption(getSelectedItemOption());
        }
        getBinding().setModel(mModel);

        getBinding().tbTitle.setText(mModel.getModifierGroup().getName());
        getBinding().tbTitle.setContentDescription(getString(R.string.heading_cd, getBinding().tbTitle.getText()));
        getBinding().contentIncluded.tvResetModifier.setOnClickListener((view) -> mPresenter.reset());
        mPresenter.setModel(mModel);

        getBinding().contentIncluded.btnSaveModifier.setOnClickListener((view) -> mPresenter.saveSelection());

        mPresenter.loadOrder();
    }

    private ModifierGroup getModifierGroup() {
        return (ModifierGroup) getIntent().getSerializableExtra(AppConstants.EXTRA_MODIFIER_GROUP);
    }

    private ItemModifier getSelectedItemModifier() {
        return (ItemModifier) getIntent().getSerializableExtra(AppConstants.EXTRA_ITEM_MODIFIER);
    }

    private ItemOption getSelectedItemOption() {
        return (ItemOption) getIntent().getSerializableExtra(AppConstants.EXTRA_ITEM_OPTION);
    }

    @Override
    protected void onResume() {
        mPresenter.loadOrder();
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void setQuantities(List<ItemOption> quantityOptions, ItemOption selectedItemOption) {
        mItemOptionsAdapter.setData(quantityOptions, selectedItemOption);
    }

    @Override
    public void setModifiers(List<ItemModifier> modifiers, ItemModifier selectedItemModifier) {
        mModifierVerticalAdapter.setSelected(selectedItemModifier);
        mModifierVerticalAdapter.setData(modifiers);
    }

    @Override
    public boolean quantityErrorEnabled(boolean errorEnabled) {
        if (errorEnabled) {
            getBinding().contentIncluded.tilQuantity.setError(getString(R.string.empty_quantity_error));
        } else {
            getBinding().contentIncluded.tilQuantity.setErrorEnabled(false);
        }
        return errorEnabled;
    }

    @Override
    public void goBackWithResult(ItemModifier selectedItemModifier, ItemOption selectedItemOption) {
        Intent data = new Intent();
        data.putExtra(AppConstants.EXTRA_MODIFIER_GROUP, mModel.getModifierGroup());
        data.putExtra(AppConstants.EXTRA_ITEM_MODIFIER, selectedItemModifier);
        data.putExtra(AppConstants.EXTRA_ITEM_OPTION, selectedItemOption);
        setResult(RESULT_OK, data);
        finish();
    }
}
