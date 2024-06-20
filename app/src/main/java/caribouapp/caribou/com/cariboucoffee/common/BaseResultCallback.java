package caribouapp.caribou.com.cariboucoffee.common;

import caribouapp.caribou.com.cariboucoffee.util.Log;

/**
 * Created by asegurola on 4/13/18.
 */

public class BaseResultCallback<T> implements ResultCallback<T> {

    private static final String TAG = BaseResultCallback.class.getSimpleName();

    @Override
    public void onSuccess(T data) {
        Log.d(TAG, "onSuccess");
    }

    @Override
    public void onFail(int errorCode, String errorMessage) {
        Log.d(TAG, "onFail - " + errorCode + ":" + errorMessage);
    }

    @Override
    public void onError(Throwable error) {
        Log.d(TAG, "onError", error);
    }
}
