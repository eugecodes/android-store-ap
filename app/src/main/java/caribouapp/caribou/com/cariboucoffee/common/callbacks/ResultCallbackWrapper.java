package caribouapp.caribou.com.cariboucoffee.common.callbacks;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;

/**
 * Created by asegurola on 5/3/18.
 */

public abstract class ResultCallbackWrapper<T> implements ResultCallback<T> {

    private ResultCallback mCallback;

    public ResultCallbackWrapper(ResultCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onFail(int errorCode, String errorMessage) {
        mCallback.onFail(errorCode, errorMessage);
    }

    @Override
    public void onError(Throwable error) {
        mCallback.onError(error);
    }
}
