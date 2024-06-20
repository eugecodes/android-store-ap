package caribouapp.caribou.com.cariboucoffee.common;

/**
 * Created by andressegurola on 5/24/17.
 */

public interface ResultCallback<T> {

    void onSuccess(T data);

    void onFail(int errorCode, String errorMessage);

    void onError(Throwable error);
}
