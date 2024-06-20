package caribouapp.caribou.com.cariboucoffee.mvp.checkIn;

import android.graphics.Bitmap;

import java.math.BigDecimal;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardModel;

/**
 * Created by jmsmuy on 11/15/17.
 */

public interface CheckInContract {

    interface View extends MvpView {

        void setCards(List<RewardModel> rewards);

        void setBarcode(Bitmap barcodeBitmap);

        void goToAddFunds();

        void showSuccessOnRedeem();

        void showStartOrderAfterRedeeming(RewardItemModel rewardItemModel);

        void goToStartNewOrder(RewardItemModel rewardItemModel);

        void showRedeemConfirmation(RewardItemModel rewardItemModel);
    }

    interface Presenter extends MvpPresenter, RewardsPresenter {

        void loadData(boolean useCache);

        void setCardNumber(String cardNumber, boolean newCardNumber);

        void setBalance(BigDecimal balance);

        void addFundsClicked();

        void loadRewards();

        void redeemReward(RewardItemModel rewardItemModel);
    }
}
