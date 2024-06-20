package caribouapp.caribou.com.cariboucoffee.mvp.account.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentTransactionHistoryBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.TransactionModel;
import caribouapp.caribou.com.cariboucoffee.mvp.account.presenter.TransactionHistoryPresenter;

/**
 * Created by jmsmuy on 11/21/17.
 */
@FragmentWithArgs
public class TransactionHistoryFragment extends BaseFragment<FragmentTransactionHistoryBinding> implements AccountContract.TransactionHistoryView {

    private AccountContract.TransactionHistoryPresenter mPresenter;
    private TransactionHistoryAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_transaction_history;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        TransactionHistoryPresenter presenter = new TransactionHistoryPresenter(this);
        SourceApplication.get(getContext()).getComponent().inject(presenter);
        mPresenter = presenter;

        mAdapter = new TransactionHistoryAdapter();
        getBinding().rvTransactionHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().rvTransactionHistory.setAdapter(mAdapter);
        getBinding().rvTransactionHistory.setNestedScrollingEnabled(false);
        mPresenter.loadTransactionHistory();
        return view;

    }

    @Override
    public void displayTransactionHistory(List<TransactionModel> transactions) {
        getBinding().rvTransactionHistory.setVisibility(View.VISIBLE);
        getBinding().emptyTransactionHistory.setVisibility(View.GONE);
        mAdapter.setData(transactions, mPresenter);
    }

    @Override
    public void displayEmptyTransactionHistory() {
        getBinding().rvTransactionHistory.setVisibility(View.GONE);
        getBinding().emptyTransactionHistory.setVisibility(View.VISIBLE);
    }
}
