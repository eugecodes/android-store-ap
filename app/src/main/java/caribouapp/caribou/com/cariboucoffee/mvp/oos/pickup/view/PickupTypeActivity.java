package caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.view;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityPickupBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.ContentPickupBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsidePickupData;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.PickupContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.model.PickupModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.presenter.PickupPresenter;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import icepick.Icepick;
import icepick.State;
import tech.hibk.searchablespinnerlibrary.SearchableItem;

public class PickupTypeActivity extends OOSFlowActivity<ActivityPickupBinding> implements PickupContract.View {

    private static final String TAG = PickupTypeActivity.class.getName();

    private PickupContract.Presenter mPresenter;
    private HorizontalPicker mCarTypePicker;
    private HorizontalPicker mCarColorPicker;

    public static Intent createIntentNextScreenMenu(Context context, Intent nextScreenIntent, String confirmButtonText,
                                                    boolean isFromCheckout) {
        Intent intent = new Intent(context, PickupTypeActivity.class);
        if (nextScreenIntent != null) {
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pendingIntent = PendingIntent.getActivity(context, 0, nextScreenIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(context, 0, nextScreenIntent, PendingIntent.FLAG_ONE_SHOT);
            }
            intent.putExtra(AppConstants.EXTRA_PENDING_INTENT_NEXT_SCREEN, pendingIntent);
        }
        intent.putExtra(AppConstants.EXTRA_MAIN_BUTTON_TEXT, confirmButtonText);
        intent.putExtra(AppConstants.EXTRA_IS_FROM_CHECKOUT, isFromCheckout);
        return intent;
    }

    @State
    PickupModel mModel;

    private HashMap<YextPickupType, View> mOptionToViewMap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pickup;
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

        if (savedInstanceState == null) {
            mModel = new PickupModel();
        }

        PickupPresenter pickupPresenter = new PickupPresenter(this, mModel);
        SourceApplication.get(this).getComponent().inject(pickupPresenter);
        mPresenter = pickupPresenter;
        setOOSFlowPresenter(mPresenter);

        ContentPickupBinding contentPickupBinding = getBinding().contentIncluded;

        mOptionToViewMap = new HashMap<>();
        mOptionToViewMap.put(YextPickupType.WalkIn, contentPickupBinding.optWalkin.getRoot());
        mOptionToViewMap.put(YextPickupType.DriveThru, contentPickupBinding.optDriveThru.getRoot());
        mOptionToViewMap.put(YextPickupType.Curbside, contentPickupBinding.optCurbside.getRoot());
        mOptionToViewMap.put(YextPickupType.Delivery, contentPickupBinding.deliveryOption.getRoot());

        getBinding().contentIncluded.btnContinue.setText(getMainButtonText());

        getBinding().setModel(mModel);

        initListeners();

        mPresenter.loadOrder();
    }

    private void initListeners() {
        for (Map.Entry<YextPickupType, View> optionEntry : mOptionToViewMap.entrySet()) {
            optionEntry.getValue().setOnClickListener(e -> {
                mPresenter.setPickupType(optionEntry.getKey());
                UIUtil.hideKeyboard(this);
            });
        }

        getBinding().contentIncluded.btnContinue.setOnClickListener(e -> mPresenter.validateAndContinue());
        setWalkInAsDefault();
    }

    private void setWalkInAsDefault() {
        if (mPresenter.isPickupTypeEnabled(YextPickupType.WalkIn)) {
            mPresenter.setPickupType(YextPickupType.WalkIn);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void navigateToNextOrderScreen(boolean requiresBounce) {
        setResult(AppConstants.RESULT_CODE_BOUNCE_REQUIRED);
        finish();

        if (getNextScreenPendingIntent() == null) {
            return;
        }

        try {
            getNextScreenPendingIntent().send();
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, new LogErrorException("Error launching next screen"));
        }
    }

    private PendingIntent getNextScreenPendingIntent() {
        return (PendingIntent) getIntent().getParcelableExtra(AppConstants.EXTRA_PENDING_INTENT_NEXT_SCREEN);
    }

    private boolean isFromCheckout() {
        return getIntent().getBooleanExtra(AppConstants.EXTRA_IS_FROM_CHECKOUT, false);
    }

    private String getMainButtonText() {
        String extraMainButtonText = getIntent().getStringExtra(AppConstants.EXTRA_MAIN_BUTTON_TEXT);
        return extraMainButtonText != null ? extraMainButtonText : getString(R.string.continue_text);
    }


    @Override
    public void updateCarOptions(List<String> carTypes, List<String> carColors, List<String> carMakeOptions, CurbsidePickupData curbsidePickupData) {
        loadCarTypeOptions(carTypes);
        loadCarColorOptions(carColors);
        getBinding().contentIncluded.curbsideData.tvCarMakeTitle.setVisibility(mModel.isCarMakeEnabled() ? View.VISIBLE : View.GONE);
        getBinding().contentIncluded.curbsideData.spnCarMake.setVisibility(mModel.isCarMakeEnabled() ? View.VISIBLE : View.GONE);
        if (mModel.isCarMakeEnabled()) {
            loadCarMakeOptions(carMakeOptions);
        }
        setCurbsideStoredValues(curbsidePickupData, carMakeOptions, carColors, carTypes);
    }

    private void loadCarTypeOptions(List<String> carTypes) {
        mCarTypePicker = getBinding().contentIncluded.curbsideData.carTypePicker;
        mCarTypePicker.setListName(getString(R.string.car_type));
        mCarTypePicker.setOptions(buildOptionList(carTypes));
        mCarTypePicker.setListener(singleOption -> mPresenter.setCarType(singleOption.getName()));
        mCarTypePicker.setCurrentSelection(0);
        mPresenter.setCarType(carTypes.get(0));
    }

    private void loadCarColorOptions(List<String> carColors) {
        mCarColorPicker = getBinding().contentIncluded.curbsideData.carColorPicker;
        mCarColorPicker.setListName(getString(R.string.car_color));
        mCarColorPicker.setOptions(buildOptionList(carColors));
        mCarColorPicker.setListener(singleOption -> mPresenter.setCarColor(singleOption.getName()));
        mCarColorPicker.setCurrentSelection(0);
        mPresenter.setCarColor(carColors.get(0));
    }

    private void loadCarMakeOptions(List<String> carMakeOptions) {
        carMakeOptions.add(0, getString(R.string.select_one));
        getBinding().contentIncluded.curbsideData.spnCarMake.setItems(generateSearchableList(carMakeOptions));
        getBinding().contentIncluded.curbsideData.spnCarMake.getBackground().setColorFilter(
                getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_ATOP);
        getBinding().contentIncluded.curbsideData.spnCarMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view == null) {
                    return;
                }
                if (position == 0) {
                    mPresenter.setCarMake(null);
                    return;
                }
                //Change selected view text color
                //https://stackoverflow.com/questions/15379851/change-text-color-of-selected-item-in-spinner
                ((TextView) view).setTextColor(ContextCompat.getColor(view.getContext(), R.color.whiteColor));
                mPresenter.setCarMake(carMakeOptions.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // NO-OP
            }
        });
    }

    private List<SearchableItem> generateSearchableList(List<String> carMakeOptions) {
        List<SearchableItem> searchableItems = new ArrayList<>();
        for (int i = 0; i <= carMakeOptions.size() - 1; i++) {
            searchableItems.add(new SearchableItem(i, carMakeOptions.get(i)));
        }
        return searchableItems;
    }

    private void setCurbsideStoredValues(CurbsidePickupData curbsidePickupData,
                                         List<String> carMakeOptions, List<String> carColorOptions, List<String> carTypeOptions) {
        if (curbsidePickupData == null) {
            return;
        }
        String curbsideCarMakeSelected = curbsidePickupData.getCarMake();
        String curbsideCarTypeSelected = curbsidePickupData.getCarType();
        String curbsideCarColorSelected = curbsidePickupData.getCarColor();

        if (carColorOptions.contains(curbsideCarTypeSelected)) {
            mCarColorPicker.setCurrentSelection(new SingleOption<>(curbsideCarTypeSelected));
        }
        if (carTypeOptions.contains(curbsideCarColorSelected)) {
            mCarTypePicker.setCurrentSelection(new SingleOption<>(curbsideCarColorSelected));
        }
        if (carMakeOptions.contains(curbsideCarMakeSelected)) {
            getBinding().contentIncluded.curbsideData.spnCarMake.setSelection(carMakeOptions.indexOf(curbsideCarMakeSelected));
        }
    }

    @Override
    public void updatePickupType(YextPickupType yextPickupType) {
        ContentPickupBinding contentPickupBinding = getBinding().contentIncluded;
        for (Map.Entry<YextPickupType, View> optionEntry : mOptionToViewMap.entrySet()) {
            optionEntry.getValue().setSelected(yextPickupType == optionEntry.getKey());
        }

        boolean deliveryEnabled = yextPickupType == YextPickupType.Delivery && mModel.isDeliveryEnabled();
        if (deliveryEnabled) {
            contentPickupBinding.deliveryOption.tvRules.setVisibility(View.VISIBLE);
            String minimumMoneyString = StringUtils.formatMoneyAmount(this, mModel.getDeliveryMinimum(), null);
            String deliveryFeeString = StringUtils.formatMoneyAmount(this, mModel.getDelvieryFee(), null);
            contentPickupBinding.deliveryOption.setPickupRules(
                    getString(R.string.pickup_delivery_rules, mModel.getDeliveryMaxDistance(), minimumMoneyString, deliveryFeeString));
        } else {
            contentPickupBinding.deliveryOption.tvRules.setVisibility(View.GONE);
        }
        contentPickupBinding.optCurbside.tvRules.setVisibility(yextPickupType == YextPickupType.Curbside ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showPickupTypes(List<YextPickupType> pickupTypes) {
        for (Map.Entry<YextPickupType, View> optionEntry : mOptionToViewMap.entrySet()) {
            optionEntry.getValue().setVisibility(pickupTypes.contains(optionEntry.getKey()) ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void disablePickupType(YextPickupType yextPickupType) {
        View optionView = mOptionToViewMap.get(yextPickupType);
        if (optionView != null) {
            optionView.setEnabled(false);
        }
    }

    @Override
    public void updatePickupData(PickupModel pickupModel) {
        updatePickupType(pickupModel.getSelectedPickupType());

        if (pickupModel.getSelectedCarColor() != null) {
            mCarColorPicker.setCurrentSelection(new SingleOption<>(pickupModel.getSelectedCarColor()));
        }
        if (pickupModel.getSelectedCarType() != null) {
            mCarTypePicker.setCurrentSelection(new SingleOption<>(pickupModel.getSelectedCarType()));
        }
    }

    @Override
    public boolean addressLine1ErrorEnabled(boolean enabled) {
        return fieldErrorEnabled(getBinding().contentIncluded.deliveryData.tilAddressLine1, R.string.delivery_address_line_1_error, enabled);
    }

    @Override
    public boolean zipErrorEnabled(boolean enabled) {
        return fieldErrorEnabled(getBinding().contentIncluded.deliveryData.tilZipCode, R.string.delivery_address_zip_code_error, enabled);
    }

    @Override
    public boolean deliveryPhoneNumberErrorEnabled(boolean enabled) {
        return fieldErrorEnabled(getBinding().contentIncluded.deliveryData.tilPhoneNumber, R.string.delivery_address_contact_phone_error, enabled);
    }

    @Override
    public void showOutOfDeliveryRangeDialog(BigDecimal distanceResultInMiles) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delivery_out_of_range, distanceResultInMiles))
                .setNegativeButton(R.string.okay, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void showContinueNotAvailableDialog() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.order_not_available))
                .setNegativeButton(R.string.okay, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void setCurbsideTipMessage(String curbsideTipMessage) {
        getBinding().contentIncluded.optCurbside.tvRules.setText(curbsideTipMessage);
    }

    @Override
    public boolean carMakeErrorEnable(boolean enable) {
        return curbsideTextViewErrorEnabled(getBinding().contentIncluded.curbsideData.tvSelectCarMakeError, enable);

    }

    @Override
    public boolean carTypeErrorEnable(boolean enable) {
        return curbsideTextViewErrorEnabled(getBinding().contentIncluded.curbsideData.tvSelectCarTypeError, enable);
    }

    @Override
    public boolean carColorErrorEnable(boolean enable) {
        return curbsideTextViewErrorEnabled(getBinding().contentIncluded.curbsideData.tvSelectCarColorError, enable);
    }

    @Override
    protected AppScreen getScreenName() {
        return isFromCheckout() ? AppScreen.PICKUP_TYPE_CHECKOUT : AppScreen.PICKUP_TYPE;
    }

    private boolean curbsideTextViewErrorEnabled(TextView textView, boolean enable) {
        textView.setVisibility(enable ? View.VISIBLE : View.GONE);
        return enable;
    }

    private boolean fieldErrorEnabled(TextInputLayout field, int errorResource, boolean enabled) {
        if (enabled) {
            field.setError(getString(errorResource));
        } else {
            field.setErrorEnabled(false);
        }
        return enabled;
    }

    private List<SingleOption> buildOptionList(List<String> options) {
        List<SingleOption> optionList = new ArrayList<>();
        for (String option : options) {
            optionList.add(new SingleOption<>(option, option));
        }
        return optionList;
    }
}
