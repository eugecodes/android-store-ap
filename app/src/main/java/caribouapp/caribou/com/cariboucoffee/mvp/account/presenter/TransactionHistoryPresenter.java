package caribouapp.caribou.com.cariboucoffee.mvp.account.presenter;

import android.app.Application;

import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.TransactionHistory;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.TransactionModel;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import retrofit2.Response;

/**
 * Created by jmsmuy on 11/21/17.
 */

public class TransactionHistoryPresenter extends BasePresenter<AccountContract.TransactionHistoryView>
        implements AccountContract.TransactionHistoryPresenter {

    private static final String TAG = TransactionHistoryPresenter.class.getSimpleName();

    @Inject
    Application mApplication;

    @Inject
    AmsApi mAmsApi;

    @Inject
    UserServices mUserServices;

    @Inject
    SettingsServices mSettingsServices;

    public TransactionHistoryPresenter(AccountContract.TransactionHistoryView view) {
        super(view);
    }

    @Override
    public void loadTransactionHistory() {
        mAmsApi.getTransactionHistory(mUserServices.getUid())
                .enqueue(new BaseViewRetrofitErrorMapperCallback<TransactionHistory>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(Response<TransactionHistory> response) {
                        TransactionHistory transactionHistory = response.body();
                        List<TransactionModel> transactionModelList = transactionHistory.getTransactions().getTransactionModelList();
                        if (transactionModelList == null || transactionModelList.isEmpty()) {
                            getView().displayEmptyTransactionHistory();
                        } else {
                            getView().displayTransactionHistory(
                                    transactionHistory.getTransactions().getTransactionModelList()
                            );
                        }
                    }
                });

    }

}
