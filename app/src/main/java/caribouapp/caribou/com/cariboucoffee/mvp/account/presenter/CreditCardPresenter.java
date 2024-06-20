package caribouapp.caribou.com.cariboucoffee.mvp.account.presenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsBillingResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsPaymentCard;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponseType;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CardEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CreditCardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import retrofit2.Response;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.AMOUNT_DIGITS_TO_SHOW;

/**
 * Created by jmsmuy on 11/21/17.
 */

public class CreditCardPresenter extends BasePresenter<AccountContract.CreditCardView> implements AccountContract.CreditCardPresenter {

    private static final String TAG = CreditCardPresenter.class.getSimpleName();
    @Inject
    UserServices mUserServices;

    @Inject
    AmsApi mAmsApi;

    @Inject
    ErrorMessageMapper mErrorMessageMapper;

    private List<CreditCardModel> mCardList;

    public CreditCardPresenter(AccountContract.CreditCardView view) {
        super(view);
    }

    @Override
    public void loadCards() {
        // TODO: Remove mSourceApp param when service stops using it
        mAmsApi.getBillingData(BuildConfig.SOURCE_APP, mUserServices.getUid())
                .enqueue(new BaseViewRetrofitErrorMapperCallback<AmsResponse>(getView()) {

                    @Override
                    protected void onSuccessBeforeViewUpdates(Response<AmsResponse> response) {
                        mCardList = new ArrayList<>();
                        for (AmsPaymentCard card : response.body().getResult().getAmsBillingInformation().getPaymentCardList()) {
                            if (card == null) { // Users who have no credit cards come with an empty null one from the AMS
                                continue;
                            }

                            mCardList.add(
                                    new CreditCardModel(CardEnum.getCardTypeFromApiName(card.getCardType()),
                                            StringUtils.getSuffix(card.getCardNumberPartial(), AMOUNT_DIGITS_TO_SHOW),
                                            card.getToken()));
                        }

                        mUserServices.setBillingInformation(response.body().getResult().getAmsBillingInformation());
                    }

                    @Override
                    protected void onSuccessViewUpdates(Response<AmsResponse> response) {
                        getView().setCardList(mCardList);
                        getView().updateCardList();
                    }
                });
    }

    @Override
    public void removeCard(CreditCardModel cardModel) {
        mAmsApi.removeCard(mUserServices.getUid()).enqueue(new BaseViewRetrofitCallback<AmsBillingResponse>(getView()) {

            @Override
            protected void onPostResponse() {
                super.onPostResponse();
                UIUtil.runWithBaseView(getView(), baseView -> loadCards());
            }

            @Override
            protected void onSuccessViewUpdates(Response<AmsBillingResponse> response) {
                AmsResponseType responseType = response.body().getType();
                if (responseType != null && responseType == AmsResponseType.SUCCESS) {
                    getView().showMessage(R.string.card_correctly_deleted);
                } else {
                    getView().showWarning(R.string.unknown_server_error);
                }
            }
        });
    }
}
