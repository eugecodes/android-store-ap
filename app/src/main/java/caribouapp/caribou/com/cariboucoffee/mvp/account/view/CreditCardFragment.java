package caribouapp.caribou.com.cariboucoffee.mvp.account.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.common.CCInformationActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentCreditCardBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CreditCardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.account.presenter.CreditCardPresenter;

/**
 * Created by jmsmuy on 11/21/17.
 */
@FragmentWithArgs
public class CreditCardFragment extends BaseFragment<FragmentCreditCardBinding> implements AccountContract.CreditCardView {

    private AccountContract.CreditCardPresenter mPresenter;
    private CreditCardAdapter mCardAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_credit_card;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CreditCardPresenter presenter = new CreditCardPresenter(this);
        SourceApplication.get(getContext()).getComponent().inject(presenter);
        mPresenter = presenter;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Sets up the adapter for the perks
        mCardAdapter = new CreditCardAdapter(this);
        getBinding().rvCards.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().rvCards.setAdapter(mCardAdapter);

        getBinding().btnAddCard.setOnClickListener(view1 -> addCreditCard());

        mPresenter.loadCards();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_ADD_CARD && resultCode != Activity.RESULT_CANCELED) {
            mPresenter.loadCards();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void setCardList(List<CreditCardModel> cardList) {
        mCardAdapter.setList(cardList);
    }

    @Override
    public void updateCardList() {
        if (mCardAdapter.getItemCount() > 0) {
            mCardAdapter.notifyDataSetChanged();
            getBinding().rvCards.setVisibility(View.VISIBLE);
            getBinding().btnAddCard.setVisibility(View.GONE);
            getBinding().ivNoCreditCard.setVisibility(View.GONE);
        } else {
            getBinding().rvCards.setVisibility(View.GONE);
            getBinding().btnAddCard.setVisibility(View.VISIBLE);
            getBinding().ivNoCreditCard.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void removeCard(CreditCardModel cardModel) {
        mPresenter.removeCard(cardModel);
    }

    @Override
    public void editCreditCard(CreditCardModel cardModel) {
        startActivityForResult(
                CCInformationActivity
                        .createIntent(getContext(), CCInformationActivity.AddPaymentOrigin.EDIT_CARD,
                                false, cardModel.getToken(), false), AppConstants.REQUEST_CODE_ADD_CARD);
    }

    @Override
    public void addCreditCard() {
        startActivityForResult(
                CCInformationActivity
                        .createIntent(getContext(), CCInformationActivity.AddPaymentOrigin.ADD_CARD,
                                false, null, false), AppConstants.REQUEST_CODE_ADD_CARD);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
