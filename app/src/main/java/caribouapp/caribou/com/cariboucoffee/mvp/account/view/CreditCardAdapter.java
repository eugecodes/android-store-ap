package caribouapp.caribou.com.cariboucoffee.mvp.account.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutCreditCardItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CreditCardModel;

/**
 * Created by jmsmuy on 11/22/17.
 */

public class CreditCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final AccountContract.CreditCardView mParent;
    private List<CreditCardModel> mCreditCardList = new ArrayList<>();

    public CreditCardAdapter(AccountContract.CreditCardView creditCardFragment) {
        mParent = creditCardFragment;
    }

    public List<CreditCardModel> getList() {
        return mCreditCardList;
    }

    public void setList(List<CreditCardModel> list) {
        mCreditCardList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutCreditCardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemHolder) holder).setItemModel(mCreditCardList.get(position));
    }

    @Override
    public int getItemCount() {
        return mCreditCardList.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private LayoutCreditCardItemBinding mBindingItem;

        ItemHolder(LayoutCreditCardItemBinding binding) {
            super(binding.getRoot());
            mBindingItem = binding;
        }

        void setItemModel(CreditCardModel item) {
            mBindingItem.setModel(item);
            mBindingItem.ivCardDrawable
                    .setImageDrawable(
                            ContextCompat.getDrawable(mBindingItem.getRoot().getContext(), item.getCard().getCardLogo()));
            mBindingItem.getRoot().setTag(item);
            mBindingItem.tvEditCard.setOnClickListener(view -> mParent.editCreditCard(item));
            mBindingItem.tvDeleteCard.setOnClickListener(view -> new AlertDialog.Builder(mBindingItem.getRoot().getContext())
                    .setCancelable(true)
                    .setMessage(R.string.confirmation_delete_card)
                    .setNegativeButton(R.string.not_delete_card, (dialogInterface, i) -> {
                    })
                    .setPositiveButton(R.string.delete_card, (dialogInterface, i) -> mParent.removeCard(item))
                    .create().show());
            mBindingItem.executePendingBindings();
        }

    }

}
