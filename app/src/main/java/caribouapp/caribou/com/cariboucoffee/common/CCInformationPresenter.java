package caribouapp.caribou.com.cariboucoffee.common;

import android.text.TextUtils;

import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsAddress;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsAutoReloadSettings;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsPaymentCard;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.UpdateBillingData;
import caribouapp.caribou.com.cariboucoffee.api.model.account.UpdateBillingRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.account.UpdateBillingResponse;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.ResultCallbackErrorMapper;
import caribouapp.caribou.com.cariboucoffee.cybersource.BillToData;
import caribouapp.caribou.com.cariboucoffee.cybersource.CardData;
import caribouapp.caribou.com.cariboucoffee.cybersource.CybersourceService;
import caribouapp.caribou.com.cariboucoffee.cybersource.TokenResponse;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CardEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import retrofit2.Response;

/**
 * Created by jmsmuy on 11/28/17.
 */

public class CCInformationPresenter extends BasePresenter<CCInformationContract.View> implements CCInformationContract.Presenter {

    private static final String TAG = CCInformationActivity.class.getSimpleName();
    @Inject
    UserServices mUserServices;

    @Inject
    AmsApi mAmsApi;

    @Inject
    UserAccountService mUserAccountService;

    @Inject
    ErrorMessageMapper mErrorMessageMapper;

    @Inject
    CybersourceService mCybersourceService;

    @Inject
    EventLogger mEventLogger;

    private CCInformationModel mModel;
    private boolean mEditingMode = false;

    public CCInformationPresenter(CCInformationContract.View view) {
        super(view);
    }

    @Override
    public void setModel(CCInformationModel model) {
        mModel = model;
        mModel.setReplaceCardEnabled(getView().hasOptionalReplaceCard());

        mModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int fieldId) {
                if (getView() == null) {
                    return;
                }
                if (fieldId == BR.expirationMonth) {
                    getView().expirationMonthErrorEnabled(!validateExpirationMonth());
                } else if (fieldId == BR.expirationYear) {
                    getView().expirationYearErrorEnabled(!validateExpirationYear());
                }
            }
        });
    }

    private void resetErrors() {
        getView().firstNameErrorEnabled(false);
        getView().lastNameErrorEnabled(false);
        getView().cardNumberErrorEnabled(false);
        getView().expirationMonthErrorEnabled(false);
        getView().expirationYearErrorEnabled(false);
        getView().billingAddressErrorEnabled(false);
        getView().cityErrorEnabled(false);
        getView().stateErrorEnabled(false);
        getView().zipErrorEnabled(false);
        getView().cvvErrorEnabled(false);
        getView().cvvUnknownErrorEnabled(false);
        getView().cardTypeUnknownErrorEnabled(false);
    }

    @Override
    public void addOrUpdateCard() {
        resetErrors();
        boolean unknownCCError = false;
        boolean unknownCvvError = false;
        if (mModel.getCcNumber() != null) {
            mModel.setCcNumber(removeWhiteSpace(mModel.getCcNumber()));
            unknownCCError = validateNumber(mModel.getCcNumber());
            getView().validCardNumberErrorEnabled(!unknownCCError);
            if (!unknownCCError) {
                return;
            }
        }
        if (mModel.getCvv() != null) {
            mModel.setCvv(removeWhiteSpace(mModel.getCvv()));
            unknownCvvError = validateNumber(mModel.getCvv());
            getView().validCvvErrorEnabled(!unknownCvvError);
            if (!unknownCvvError) {
                return;
            }
        }
        boolean errorDetected = getView().firstNameErrorEnabled(TextUtils.isEmpty(mModel.getFirstName()))
                | getView().lastNameErrorEnabled(TextUtils.isEmpty(mModel.getLastName()))
                | getView().cardNumberErrorEnabled(TextUtils.isEmpty(mModel.getCcNumber()))
                | getView().expirationMonthErrorEnabled(!validateExpirationMonth())
                | getView().expirationYearErrorEnabled(!validateExpirationYear())
                | getView().billingAddressErrorEnabled(TextUtils.isEmpty(mModel.getBillingAddress()))
                | getView().cityErrorEnabled(TextUtils.isEmpty(mModel.getCity()))
                | getView().stateErrorEnabled(mModel.getState() == null)
                | getView().zipErrorEnabled(TextUtils.isEmpty(mModel.getZip()))
                | getView().cvvErrorEnabled(TextUtils.isEmpty(mModel.getCvv()))
                || getView().cvvUnknownErrorEnabled(CardEnum.getCardTypeFromCardNumber(mModel.getCcNumber(), mModel.getCvv()) == CardEnum.UNKNOWN)
                | getView().cardTypeUnknownErrorEnabled(CardEnum.getCardTypeFromCardNumber(mModel.getCcNumber(), null) == CardEnum.UNKNOWN);

        if (errorDetected) {
            return;
        }

        CardData cardData = buildCardData(mModel);
        BillToData billToData = buildBillToData(mModel, mUserServices.getEmail());

        getView().showLoadingLayer(true);

        ResultCallback<TokenResponse> cybersourceCallback = new ResultCallback<TokenResponse>() {
            @Override
            public void onSuccess(TokenResponse data) {
                if (getView() == null) {
                    return;
                }

                getView().runOnUiThread(() -> {
                    getView().hideLoadingLayer();


                    if (mModel.isApproveChangeCard() || !mModel.isReplaceCardEnabled()) {
                        replaceCardOnFile(data);
                    } else {
                        finishAndSendResult(data, false, true);
                    }
                });

            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                if (getView() == null) {
                    return;
                }

                getView().runOnUiThread(() -> {
                    getView().hideLoadingLayer();
                    Log.e(TAG, new LogErrorException("Failure on processing cybersource request: " + errorCode));

                    if (AppUtils.isProductionBuild()) {
                        getView().showWarning(R.string.credit_card_processor_transaction_not_approved);
                    } else {
                        getView().showDebugDialog("DEBUG ERROR", StringUtils.format("DEBUG:: %d - %s", errorCode, errorMessage));
                    }
                });
            }

            @Override
            public void onError(Throwable error) {
                if (getView() == null) {
                    return;
                }
                getView().runOnUiThread(() -> {
                    getView().hideLoadingLayer();
                    getView().showError(error);
                });
            }
        };

        if (mEditingMode || (mModel.isApproveChangeCard() && hasUserGotCard())) {
            mCybersourceService.updateToken(mModel.getToken(), cardData, billToData, cybersourceCallback);
        } else {
            mCybersourceService.createToken(cardData, billToData, cybersourceCallback);
        }
    }

    private BillToData buildBillToData(CCInformationModel model, String userEmail) {

        BillToData billToData = new BillToData();
        billToData.setFirstName(model.getFirstName());
        billToData.setLastName(model.getLastName());
        billToData.setAddressLine1(model.getBillingAddress());

        if (model.getBillingAddress2() != null
                && !model.getBillingAddress2().isEmpty()) {
            billToData.setAddressLine2(model.getBillingAddress2());
        }

        billToData.setAddressCity(model.getCity());
        billToData.setAddressState(model.getState().getAbbreviation());
        billToData.setAddressPostalCode(model.getZip());
        billToData.setAddressCountry("US");
        billToData.setEmail(userEmail);

        billToData.setPhone(""); // This is needed in cybersource!

        return billToData;
    }

    protected CardData buildCardData(CCInformationModel model) {

        CardData cardData = new CardData();
        cardData.setNumber(model.getCcNumber());
        cardData.setExpireDate(StringUtils.format("%s-%s",
                StringUtils.formatMonth(model.getExpirationMonth()),
                StringUtils.formatYear(model.getExpirationYear())));
        cardData.setCvn(model.getCvv());
        cardData.setCardType(CardEnum.getCardTypeFromCardNumber(model.getCcNumber(), null));

        return cardData;
    }

    @Override
    public void setUpForEditing() {
        mEditingMode = true;
        mModel.setReplaceCardEnabled(false);
    }

    @Override
    public void fillModel() {
        if (!mUserServices.hasCreditCardBasicInfo()) {
            return;
        }
        mModel.setFirstName(mUserServices.getPaymentCardFirstName());
        mModel.setLastName(mUserServices.getPaymentCardLastName());

        if (!mUserServices.hasCreditCardAddress()) {
            return;
        }
        mModel.setBillingAddress(mUserServices.getPaymentCardAddressLine1());
        mModel.setBillingAddress2(mUserServices.getPaymentCardAddressLine2());
        mModel.setCity(mUserServices.getPaymentCardCity());
        mModel.setState(StateEnum.getFromName(mUserServices.getPaymentCardState()));
        mModel.setZip(mUserServices.getPaymentCardZip());
    }

    @Override
    public void updateLocalBillingData() {
        // TODO: Remove mSourceApp param when service stops using it
        mAmsApi.getBillingData(BuildConfig.SOURCE_APP, mUserServices.getUid())
                .enqueue(new BaseViewRetrofitErrorMapperCallback<AmsResponse>(getView()) {

                    @Override
                    protected void onSuccessBeforeViewUpdates(Response<AmsResponse> response) {
                        mUserServices.setBillingInformation(response.body().getResult().getAmsBillingInformation());
                    }

                    @Override
                    protected void onSuccessViewUpdates(Response<AmsResponse> response) {
                        if (mModel.isReplaceCardEnabled()) {
                            return;
                        }
                        fillModel();
                    }
                });
    }

    @Override
    public boolean hasUserGotCard() {
        return mUserServices.hasCreditCardBasicInfo();
    }

    @Override
    public void setState(StateEnum stateEnum) {
        mModel.setState(stateEnum);
    }

    private boolean validateExpirationMonth() {
        if (TextUtils.isEmpty(mModel.getExpirationMonth())) {
            return false;
        }
        try {
            int value = Integer.parseInt(mModel.getExpirationMonth());
            return value >= 1 && value <= 12;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean validateExpirationYear() {
        if (TextUtils.isEmpty(mModel.getExpirationYear())) {
            return false;
        }

        try {
            Integer.parseInt(mModel.getExpirationYear());
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }


    @Override
    public void setToken(String token) {
        mModel.setToken(token);
    }

    public void replaceCardOnFile(TokenResponse token) {
        mUserAccountService.getProfileData(new ResultCallbackErrorMapper<AmsResponse>(getView()) {

            @Override
            protected void onSuccessViewUpdates(AmsResponse data) {
                executeUpdateBilling(token);
            }
        });

    }

    private AmsAddress buildAddress() {
        return new AmsAddress(mModel.getBillingAddress(), mModel.getBillingAddress2(),
                mModel.getCity(), mModel.getState(), mModel.getZip(), AppConstants.US_COUNTRY_ABREV);
    }

    private AmsPaymentCard buildCard(AmsAddress address, TokenResponse token) {
        return new AmsPaymentCard(mModel.getFirstName(), mModel.getLastName(), token.getMaskedCardNumber(),
                StringUtils.formatMonth(mModel.getExpirationMonth()), StringUtils.formatYear(mModel.getExpirationYear()),
                address, token.getCardType(), token.getPaymentToken());
    }

    private void executeUpdateBilling(TokenResponse token) {

        UpdateBillingRequest request = new UpdateBillingRequest();
        UpdateBillingData updateBillingData = new UpdateBillingData();

        AmsAddress billingAddress = buildAddress();
        AmsPaymentCard paymentCard = buildCard(billingAddress, token);

        request.setUid(mUserServices.getUid());
        request.setUpdateBillingData(updateBillingData);
        updateBillingData.setAmsPaymentCard(paymentCard);
        updateBillingData.setAffirmativeConsent(true);

        AmsAutoReloadSettings amsAutoReloadSettings = new AmsAutoReloadSettings();
        amsAutoReloadSettings.setThresholdAmount(mUserServices.getAutoReloadThresholdAmount());
        amsAutoReloadSettings.setPaymentCardId(mUserServices.getPaymentCardId());
        amsAutoReloadSettings.setIncrementAmount(mUserServices.getAutoReloadIncrementAmount());
        amsAutoReloadSettings.setErrorCount(mUserServices.getAutoReloadErrorCount());
        amsAutoReloadSettings.setEnabled(mUserServices.getAutoreloadEnabled());
        amsAutoReloadSettings.setCurrency(mUserServices.getAutoReloadCurrency());
        updateBillingData.setAmsAutoReloadSettings(amsAutoReloadSettings);

        mAmsApi.updateBilling(request).enqueue(new BaseViewRetrofitCallback<UpdateBillingResponse>(getView(), true, true) {
            @Override
            protected void onSuccessBeforeViewUpdates(Response<UpdateBillingResponse> response) {
                Log.i(TAG, "Billing information correctly updated in the server");
            }

            @Override
            protected void onSuccessViewUpdates(Response<UpdateBillingResponse> response) {
                finishAndSendResult(token, true, !hasUserGotCard());
            }
        });
    }

    private void finishAndSendResult(TokenResponse token, boolean cardOnFile, boolean newCard) {
        if (cardOnFile && newCard) {
            mEventLogger.logAddedPaymentInfo();
        }

        getView().finishAndSendResult(mModel, token, cardOnFile, newCard);
    }


    private boolean validateNumber(String ccNumber) {
        return ccNumber.matches("[0-9]+");
    }

    private String removeWhiteSpace(String value) {
        if (value.contains(" ")) {
            return value.replace(" ", "");
        } else {
            return value;
        }
    }
}
