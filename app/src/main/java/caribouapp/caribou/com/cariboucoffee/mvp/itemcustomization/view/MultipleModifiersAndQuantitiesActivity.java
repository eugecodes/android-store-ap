package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityMultipleModifiersAndQuantitiesBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.ItemContract;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.MultipleModifiersAndQuantitiesModel;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.MultipleModifiersAndQuantitiesPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowActivity;
import icepick.Icepick;
import icepick.State;

public class MultipleModifiersAndQuantitiesActivity extends OOSFlowActivity<ActivityMultipleModifiersAndQuantitiesBinding>
        implements ItemContract.MultipleModifiersAndQuantitiesView {

    @State
    MultipleModifiersAndQuantitiesModel mModel;
    private ModifierAndQuantityAdapter mModifierAndQuantityAdapter;

    private ItemContract.MultipleModifiersAndQuantitiesPresenter mPresenter;

    public static Intent createIntent(Context context, ModifierGroup modifierGroup, Map<ItemModifier, ItemOption> orderItemSelections) {
        Intent intent = new Intent(context, MultipleModifiersAndQuantitiesActivity.class);
        intent.putExtra(AppConstants.EXTRA_MODIFIER_GROUP, modifierGroup);
        intent.putExtra(AppConstants.EXTRA_MULTIPLE_MODIFIER_OPTIONS, (Serializable) orderItemSelections);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_multiple_modifiers_and_quantities;
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

        mModifierAndQuantityAdapter = new ModifierAndQuantityAdapter();
        mModifierAndQuantityAdapter.setModifierListener((itemModifier, itemOption) -> mPresenter.setSelected(itemModifier, itemOption));
        getBinding().contentIncluded.rvModifiers.setAdapter(mModifierAndQuantityAdapter);
        getBinding().contentIncluded.rvModifiers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        getBinding().cvCart.setOnClickListener((view) -> goToCart());

        MultipleModifiersAndQuantitiesPresenter presenter = new MultipleModifiersAndQuantitiesPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;
        setOOSFlowPresenter(presenter);
        if (mModel == null) {
            mModel = new MultipleModifiersAndQuantitiesModel();
            mModel.setModifierGroup(getModifierGroup());
            mModel.setSelectedOptions(getOrderItemSelection());
        }
        getBinding().setModel(mModel);

        getBinding().contentIncluded.tvResetModifier.setOnClickListener((view) -> mPresenter.reset());
        mPresenter.setModel(mModel);

        getBinding().contentIncluded.btnSaveModifier.setOnClickListener((view) -> mPresenter.save());

        mPresenter.loadOrder();
    }

    @Override
    public void goToCart() {
        setResult(AppConstants.RESULT_CODE_GO_TO_CART);
        finish();
    }

    @Override
    protected void onResume() {
        mPresenter.loadOrder();
        super.onResume();
    }

    private ModifierGroup getModifierGroup() {
        return (ModifierGroup) getIntent().getSerializableExtra(AppConstants.EXTRA_MODIFIER_GROUP);
    }

    private Map<ItemModifier, ItemOption> getOrderItemSelection() {
        return (Map<ItemModifier, ItemOption>) getIntent().getSerializableExtra(AppConstants.EXTRA_MULTIPLE_MODIFIER_OPTIONS);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void updateModifiers(List<ItemModifier> modifiers) {
        mModifierAndQuantityAdapter.setData(modifiers, mPresenter.getSelection());
    }

    @Override
    public void goBackWithSelection(ModifierGroup modifierGroup, Map<ItemModifier, ItemOption> selectedOptions) {
        Intent data = new Intent();
        data.putExtra(AppConstants.EXTRA_MODIFIER_GROUP, modifierGroup);
        data.putExtra(AppConstants.EXTRA_MULTIPLE_MODIFIER_OPTIONS, (Serializable) selectedOptions);
        setResult(RESULT_OK, data);
        finish();
    }
}
