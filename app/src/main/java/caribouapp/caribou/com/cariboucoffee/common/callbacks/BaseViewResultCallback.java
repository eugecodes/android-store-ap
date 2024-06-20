package caribouapp.caribou.com.cariboucoffee.common.callbacks;

import java.lang.ref.WeakReference;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;

/**
 * Created by andressegurola on 4/6/17.
 */

public abstract class BaseViewResultCallback<T> implements ResultCallback<T> {

    private static final String TAG = BaseViewResultCallback.class.getSimpleName();

    private final boolean mShowLoadingIndicator;
    private WeakReference<MvpView> mBaseView;

    public BaseViewResultCallback(MvpView baseView) {
        this(baseView, true);
    }

    public BaseViewResultCallback(MvpView baseView, boolean showLoadingIndicator) {
        this(baseView, showLoadingIndicator, false);
    }

    public BaseViewResultCallback(MvpView baseView, boolean showLoadingIndicator, boolean showProcessingText) {
        mBaseView = new WeakReference<>(baseView);
        mShowLoadingIndicator = showLoadingIndicator;
        if (mShowLoadingIndicator) {
            baseView.showLoadingLayer(showProcessingText);
        }
    }

    @Override
    public void onSuccess(T data) {
        onPostResponse();
        UIUtil.runWithBaseView(mBaseView, baseView -> onSuccessViewUpdates(data));
    }

    protected abstract void onSuccessViewUpdates(T data);

    protected void onPostResponse() {
        if (!mShowLoadingIndicator) {
            return;
        }
        UIUtil.runWithBaseView(mBaseView, MvpView::hideLoadingLayer);
    }

    @Override
    public final void onError(final Throwable throwable) {
        onPostResponse();
        UIUtil.runWithBaseView(mBaseView, baseView -> onErrorView(throwable));
    }

    protected void onErrorView(final Throwable throwable) {
        Log.e(TAG, new LogErrorException("onError", throwable));
        mBaseView.get().showError(throwable);
    }

    @Override
    public final void onFail(int errorCode, String errorMessage) {
        onPostResponse();
        UIUtil.runWithBaseView(mBaseView, baseView -> onFailView(errorCode, errorMessage));
    }

    protected void onFailView(int errorCode, String errorMessage) {
        Log.e(TAG, new LogErrorException("onFail: " + errorCode + "-" + errorMessage));
        mBaseView.get().showWarning(R.string.unknown_server_error);
    }

    public WeakReference<MvpView> getBaseView() {
        return mBaseView;
    }
}
