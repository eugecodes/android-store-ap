package caribouapp.caribou.com.cariboucoffee.mvp.account.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.CardTransactionHistoryBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutTransactionDetailsViewBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.TransactionDetails;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.TransactionModel;

/**
 * Created by gonzalogelos on 8/1/18.
 */

public class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TransactionModel> mTransaction = new ArrayList<>();
    private static final String COLORIZE_TEXT = "__";
    private static final String BOLD_TEXT = "\\*\\*";
    private static final String DATE_FORMAT = "MM/dd/yy";

    private AccountContract.TransactionHistoryPresenter mPresenter;

    public List<TransactionModel> getTransaction() {
        return mTransaction;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<TransactionModel> transactions,
                        AccountContract.TransactionHistoryPresenter presenter) {
        mTransaction.clear();
        mTransaction.addAll(transactions);

        mPresenter = presenter;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(CardTransactionHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemHolder) holder).bindTo(mTransaction.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mTransaction.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private CardTransactionHistoryBinding mBindingItem;
        private Context mContext;

        ItemHolder(CardTransactionHistoryBinding binding) {
            super(binding.getRoot());
            mBindingItem = binding;
            mContext = mBindingItem.getRoot().getContext();
        }

        void bindTo(TransactionModel item, int position) {
            mBindingItem.setModel(item);

            String dateFormatted = item.getTransactionDate().toString(DATE_FORMAT);
            mBindingItem.tvTransactionDate.setText(dateFormatted);
            mBindingItem.llTransactionDetailsContainer.removeAllViews();
            String transactionDescriptions = "";
            for (TransactionDetails transactionDetails : item.getTransactionDetailsList()) {
                transactionDescriptions += loadTransactionDetails(transactionDetails) + ", ";
            }
            transactionDescriptions += "\n";
            String description = mContext.getString(R.string.transaction_cd,
                    position + 1,
                    dateFormatted,
                    item.getTransactionStoreName(), transactionDescriptions);
            if (description != null) {
                if (description.contains("_")) {
                    description = description.replace("_", "");
                }
                if (description.contains("*")) {
                    description = description.replace("*", "");
                }
            }

            mBindingItem.llTransactionContainer.setContentDescription(description);
        }

        private String loadTransactionDetails(TransactionDetails transactionDetails) {
            LayoutTransactionDetailsViewBinding transactionDetailsBinding =
                    LayoutTransactionDetailsViewBinding.inflate(
                            LayoutInflater.from(mContext), mBindingItem.llTransactionDetailsContainer, true);

            String transactionDescription = formatDescriptionBold(transactionDetails.getDescription());
            if (transactionDescription.contains(COLORIZE_TEXT)) {
                //NOTE https://stackoverflow.com/questions/5026995/android-get-color-as-string-value
                String labelColor = String.format("#%06x", ContextCompat.getColor(mContext, R.color.textAccentSecondaryColor) & 0xffffff);
                transactionDescription = formatDescriptionColor(transactionDescription, labelColor);
            }
            transactionDetailsBinding.tvTransactionDetails.setText(Html.fromHtml(transactionDescription));
            return transactionDetails.getDescription();
        }


        private String formatDescriptionBold(String transactionDescription) {
            return transactionDescription.replaceFirst(BOLD_TEXT, "<b>").replaceFirst(BOLD_TEXT, "</b>");

        }

        private String formatDescriptionColor(String transactionDescriptionToFormat, String colorString) {
            String transactionDescription = transactionDescriptionToFormat
                    .replaceFirst(COLORIZE_TEXT, String.format("<b><font color=\\\"%s\\\">", colorString));
            return transactionDescription.replaceFirst(COLORIZE_TEXT, "</font><b/>");
        }

    }
}
