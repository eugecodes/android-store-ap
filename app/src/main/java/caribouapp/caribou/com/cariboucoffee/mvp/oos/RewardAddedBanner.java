package caribouapp.caribou.com.cariboucoffee.mvp.oos;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutRewardsBannerBinding;
import caribouapp.caribou.com.cariboucoffee.order.PreSelectedReward;

public class RewardAddedBanner extends LinearLayout {

    private static final String TAG = RewardAddedBanner.class.getSimpleName();
    private LayoutRewardsBannerBinding mBinding;
    private RemoveRewardListener mListener;

    public RewardAddedBanner(Context context) {
        super(context);
        init();
    }

    public RewardAddedBanner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RewardAddedBanner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBinding = LayoutRewardsBannerBinding.inflate(LayoutInflater.from(getContext()), this, true);
        mBinding.ivRemoveReward.setOnClickListener(v -> {
            if (mListener == null) {
                Log.d(TAG, "Error remove reward listener is null");
                return;
            }
            new AlertDialog.Builder(mBinding.getRoot().getContext())
                    .setMessage(R.string.dialog_remove_reward)
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        mListener.removeReward();
                        dialog.dismiss();
                    })
                    .create()
                    .show();

        });
    }

    public void setRemoveRewardListener(RemoveRewardListener listener) {
        mListener = listener;
    }

    public void setSecondaryText(int secondaryTextString) {
        mBinding.tvChooseOneToAdd.setText(secondaryTextString);
    }

    public void setReward(PreSelectedReward preSelectedReward) {
        mBinding.setModel(preSelectedReward);
    }
}
