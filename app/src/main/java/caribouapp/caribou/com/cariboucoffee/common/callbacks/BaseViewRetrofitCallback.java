package caribouapp.caribou.com.cariboucoffee.common.callbacks;

import java.io.IOException;
import java.lang.ref.WeakReference;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import retrofit2.Response;

/**
 * Created by andressegurola on 4/6/17.
 */

public abstract class BaseViewRetrofitCallback<T> extends BaseRetrofitCallback<T> {

    private static final String TAG = BaseViewRetrofitCallback.class.getSimpleName();

    private final boolean mShowLoadingIndicator;
    private WeakReference<MvpView> mBaseView;

    public BaseViewRetrofitCallback(MvpView baseView) {
        this(baseView, true);
    }

    public BaseViewRetrofitCallback(MvpView baseView, boolean showLoadingIndicator) {
        this(baseView, showLoadingIndicator, false);
    }

    public BaseViewRetrofitCallback(MvpView baseView, boolean showLoadingIndicator, boolean showProcessingText) {
        mBaseView = new WeakReference<>(baseView);
        mShowLoadingIndicator = showLoadingIndicator;
        MvpView view = getMvpView();
        if (mShowLoadingIndicator && view != null) {
            view.showLoadingLayer(showProcessingText);
        }
    }

    protected MvpView getMvpView() {
        MvpView view = mBaseView.get();
        return view != null && view.isActive() ? view : null;
    }

    @Override
    protected void onSuccess(Response<T> response) {
        onSuccessBeforeViewUpdates(response);
        if (getMvpView() == null) {
            return;
        }
        onSuccessViewUpdates(response);
    }

    protected void onSuccessBeforeViewUpdates(Response<T> response) {

    }

    protected abstract void onSuccessViewUpdates(Response<T> response);

    @Override
    protected void onPostResponse() {
        super.onPostResponse();
        if (!mShowLoadingIndicator) {
            return;
        }
        UIUtil.runWithBaseView(mBaseView, MvpView::hideLoadingLayer);
    }

    protected void onError(final Throwable throwable) {
        Log.e(TAG, new LogErrorException("onError", throwable));
        UIUtil.runWithBaseView(mBaseView, baseView -> baseView.showError(throwable));
    }

    @Override
    protected void onFail(final Response<T> response) {
        Log.e(TAG, new LogErrorException("onFail: " + response.code() + "-" + response.message()));
        UIUtil.runWithBaseView(mBaseView, baseView -> baseView.showWarning(R.string.unknown_server_error));
    }

    protected void onNetworkFail(IOException throwable) {
        Log.e(TAG, new LogErrorException("onNetworkFail", throwable));
        if (mBaseView.get() == null) {
            return;
        }
        mBaseView.get().showError(throwable);
    }

    public WeakReference<MvpView> getBaseView() {
        return mBaseView;
    }
}
