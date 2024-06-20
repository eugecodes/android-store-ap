package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.common.CurbsideStatusEnum;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.util.Log;

public class ConfirmationModel extends BaseObservable implements Serializable {

    private static final String TAG = "ConfirmationModel";
    private Order mOrder;

    private String mCurbsideIamHereMessage;

    private CurbsideStatusEnum mCurbsideIamHereState = CurbsideStatusEnum.NONE;

    private String mCurbsideLocationPhone;

    public ConfirmationModel() {

    }

    @Bindable
    public Order getOrder() {
        return mOrder;
    }

    public void setOrder(Order order) {
        mOrder = order;
        notifyChange();
    }

    @Bindable
    public String getCurbsideIamHereMessage() {
        final String curbsideMessage = getOrder().getStoreLocation().getCurbsideInstruction();
        if (TextUtils.isEmpty(curbsideMessage)) {
            return mCurbsideIamHereMessage;
        } else {
            return curbsideMessage;
        }
    }

    @Bindable
    public void setCurbsideIamHereMessage(String curbsideIamHereMessage) {
        mCurbsideIamHereMessage = curbsideIamHereMessage;
        notifyPropertyChanged(BR.curbsideIamHereMessage);
    }

    @Bindable
    public void setCurbsideIamHereState(CurbsideStatusEnum curbsideIamHereState) {
        this.mCurbsideIamHereState = curbsideIamHereState;
        Log.d(TAG, "curbsideIamHere State: " + curbsideIamHereState);
        notifyPropertyChanged(BR.curbsideIamHereState);
    }

    @Bindable
    public CurbsideStatusEnum getCurbsideIamHereState() {
        return mCurbsideIamHereState;
    }

    @Bindable
    public String getCurbsideLocationPhone() {
        return mCurbsideLocationPhone;
    }

    public void setCurbsideLocationPhone(String curbsideLocationPhone) {
        mCurbsideLocationPhone = curbsideLocationPhone;
        notifyPropertyChanged(BR.curbsideLocationPhone);
    }
}
