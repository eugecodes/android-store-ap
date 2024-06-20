package caribouapp.caribou.com.cariboucoffee.common.callbacks;

import java.io.IOException;

import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by andressegurola on 4/6/17.
 */

public abstract class BaseRetrofitCallback<T> implements Callback<T> {

    private static final String TAG = BaseRetrofitCallback.class.getSimpleName();

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        onPostResponse();
        if (response.isSuccessful()) {
            onSuccess(response);
        } else {
            onFail(response);
        }
        onCallFinished();
    }

    @Override
    public final void onFailure(Call<T> call, Throwable throwable) {
        onPostResponse();
        if (throwable instanceof IOException) {
            onNetworkFail((IOException) throwable);
        } else {
            onError(throwable);
        }
        onCallFinished();
    }

    protected void onCallFinished() {

    }

    protected void onPostResponse() {

    }

    protected abstract void onSuccess(Response<T> response);

    protected void onFail(Response<T> response) {
        Log.w(TAG, StringUtils.format("onFail - %d: %s", response.code(), response.message()));
    }

    protected void onError(Throwable throwable) {
        Log.e(TAG, new LogErrorException("onError", throwable));
    }

    protected void onNetworkFail(IOException throwable) {
        Log.w(TAG, "onNetworkFail", throwable);
    }
}
