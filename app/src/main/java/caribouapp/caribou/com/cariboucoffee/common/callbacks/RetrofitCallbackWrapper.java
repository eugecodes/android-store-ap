package caribouapp.caribou.com.cariboucoffee.common.callbacks;

import java.io.IOException;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import retrofit2.Response;

public abstract class RetrofitCallbackWrapper<T, H> extends BaseRetrofitCallback<T> {

    private ResultCallback<H> mResultCallback;

    public RetrofitCallbackWrapper(ResultCallback<H> resultCallback) {
        mResultCallback = resultCallback;
    }

    @Override
    protected void onError(Throwable throwable) {
        super.onError(throwable);
        mResultCallback.onError(throwable);
    }

    @Override
    protected void onFail(Response<T> response) {
        super.onFail(response);
        mResultCallback.onFail(response.code(), response.message());
    }

    @Override
    protected void onNetworkFail(IOException throwable) {
        super.onNetworkFail(throwable);
        mResultCallback.onError(throwable);
    }
}
