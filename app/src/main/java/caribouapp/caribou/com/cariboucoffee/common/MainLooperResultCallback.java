package caribouapp.caribou.com.cariboucoffee.common;

import android.os.Handler;
import android.os.Looper;

import caribouapp.caribou.com.cariboucoffee.util.Log;

/**
 * Created by asegurola on 4/13/18.
 */

public abstract class MainLooperResultCallback<T> implements ResultCallback<T> {

    private static final String TAG = MainLooperResultCallback.class.getSimpleName();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onSuccess(T data) {
        Log.d(TAG, "onSuccess");
        mHandler.post(() -> onMainSuccess(data));
    }

    @Override
    public void onFail(int errorCode, String errorMessage) {
        Log.d(TAG, "onFail - " + errorCode + ":" + errorMessage);
        mHandler.post(() -> onMainFail(errorCode, errorMessage));
    }

    @Override
    public void onError(Throwable error) {
        Log.d(TAG, "onError", error);
        mHandler.post(() -> onMainError(error));
    }

    public abstract void onMainSuccess(T data);

    public abstract void onMainFail(int errorCode, String errorMessage);

    public abstract void onMainError(Throwable error);

}
