package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutQuantitySelectorViewBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.QuantitySelectorModel;

/**
 * Created by asegurola on 3/14/18.
 */

public abstract class QuantitySelectorView extends LinearLayout {

    private QuantitySelectorListener mListener;
    private QuantitySelectorModel mModel;

    public interface QuantitySelectorListener {
        void quantityChanged(int newValue);
    }

    private LayoutQuantitySelectorViewBinding mBinding;


    public QuantitySelectorView(Context context) {
        super(context);
        init();
    }

    public QuantitySelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuantitySelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mModel = new QuantitySelectorModel();
        mBinding = LayoutQuantitySelectorViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
        mBinding.setModel(mModel);
        mBinding.rbMinus.setOnClickListener(v -> fireQuantityChange(-1));
        mBinding.rbPlus.setOnClickListener(v -> fireQuantityChange(+1));
        updateQuantity(mModel.getQuantity());
    }

    public void setModel(QuantitySelectorModel model) {
        mModel = model;
        mBinding.setModel(mModel);
        updateQuantity(mModel.getQuantity());
    }

    public void setQuantityListener(QuantitySelectorListener listener) {
        mListener = listener;
    }

    private void fireQuantityChange(int changeBy) {
        mModel.setQuantity(mModel.getQuantity() + changeBy);
        updateQuantity(mModel.getQuantity());
        if (mListener != null) {
            mListener.quantityChanged(mModel.getQuantity());
        }
    }

    public LayoutQuantitySelectorViewBinding getBinding() {
        return mBinding;
    }

    protected abstract void updateQuantity(int quantity);
}
