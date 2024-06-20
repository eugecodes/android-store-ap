package caribouapp.caribou.com.cariboucoffee.util;

import org.junit.Assert;

import caribouapp.caribou.com.cariboucoffee.common.BaseResultCallback;

public class TestResultCallback<T> extends BaseResultCallback<T> {
    @Override
    public void onError(Throwable error) {
        throw new RuntimeException(error);
    }

    @Override
    public void onFail(int errorCode, String errorMessage) {
        Assert.fail();
    }
}