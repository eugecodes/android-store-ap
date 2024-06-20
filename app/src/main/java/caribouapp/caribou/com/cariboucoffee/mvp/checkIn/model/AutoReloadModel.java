package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.math.BigDecimal;


/**
 * Created by jmsmuy on 11/28/17.
 */

public class AutoReloadModel extends BaseFundsModel {

    private BigDecimal mLowerThreshold;

    private boolean mTermsAndConditions;

    private String mToken;

    @Bindable
    public BigDecimal getLowerThreshold() {
        return mLowerThreshold;
    }

    public void setLowerThreshold(BigDecimal lowerThreshold) {
        mLowerThreshold = lowerThreshold;
    }

    @Bindable
    public boolean isTermsAndConditions() {
        return mTermsAndConditions;
    }

    public void setTermsAndConditions(boolean termsAndConditions) {
        mTermsAndConditions = termsAndConditions;
        notifyPropertyChanged(BR.termsAndConditions);
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }
}
