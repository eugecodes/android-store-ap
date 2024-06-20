package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;

/**
 * Created by asegurola on 3/14/18.
 */

public class QuantitySelectorModel extends BaseObservable implements Serializable {

    private int mQuantity = 1;
    private int mMax = 10;
    private int mMin = 0;

    @Bindable
    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
        notifyPropertyChanged(BR.quantity);
    }

    @Bindable
    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
        notifyPropertyChanged(BR.max);
    }

    @Bindable
    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        mMin = min;
        notifyPropertyChanged(BR.min);
    }
}
