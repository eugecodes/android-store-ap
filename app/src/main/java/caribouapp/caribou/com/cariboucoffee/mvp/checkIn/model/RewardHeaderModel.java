package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class RewardHeaderModel extends RewardModel {

    private int mStringResource;

    public RewardHeaderModel(int stringResource) {
        mStringResource = stringResource;
    }

    @Bindable
    public int getStringResource() {
        return mStringResource;
    }

    public void setStringResource(int stringResource) {
        mStringResource = stringResource;
        notifyPropertyChanged(BR.stringResource);
    }
}
