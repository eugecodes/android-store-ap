package caribouapp.caribou.com.cariboucoffee.mvp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;

public abstract class BaseDialogFragment<T extends ViewDataBinding> extends DialogFragment implements MvpView {

    private T mBinding;

    private FragmentMvpViewDelegate mMvpViewDelegate;

    protected abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mMvpViewDelegate = new FragmentMvpViewDelegate(this);
        return mBinding.getRoot();
    }

    public T getBinding() {
        return mBinding;
    }

    @Override
    public void showMessage(int messageResId) {
        mMvpViewDelegate.showMessage(messageResId);
    }

    @Override
    public void showMessage(String message) {
        mMvpViewDelegate.showMessage(message);
    }

    @Override
    public void showWarning(int messageResId) {
        mMvpViewDelegate.showWarning(messageResId);
    }

    @Override
    public void showWarning(int messageResId, Object... args) {
        mMvpViewDelegate.showWarning(messageResId, args);
    }

    @Override
    public void showWarning(String message) {
        mMvpViewDelegate.showWarning(message);
    }

    @Override
    public void showError(Throwable throwable) {
        mMvpViewDelegate.showError(throwable);
    }

    @Override
    public void showDebugDialog(String title, String message) {
        mMvpViewDelegate.showDebugDialog(title, message);
    }

    @Override
    public void showLoadingLayer() {
        mMvpViewDelegate.showLoadingLayer();
    }

    @Override
    public void showLoadingLayer(boolean showProcessingText) {
        mMvpViewDelegate.showLoadingLayer(showProcessingText);
    }

    @Override
    public void hideLoadingLayer() {
        mMvpViewDelegate.hideLoadingLayer();
    }

    @Override
    public boolean isActive() {
        return mMvpViewDelegate.isActive();
    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        mMvpViewDelegate.runOnUiThread(runnable);
    }
}
