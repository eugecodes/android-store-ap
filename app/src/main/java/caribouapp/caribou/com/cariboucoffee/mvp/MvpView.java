package caribouapp.caribou.com.cariboucoffee.mvp;

import androidx.annotation.StringRes;

public interface MvpView {

    void showMessage(@StringRes int messageResId);

    void showMessage(String message);

    void showWarning(@StringRes int messageResId);

    void showWarning(@StringRes int messageResId, Object... args);

    void showWarning(String message);

    void showError(Throwable throwable);

    void showLoadingLayer();

    void showDebugDialog(String title, String message);

    void showLoadingLayer(boolean showProcessingText);

    void hideLoadingLayer();

    boolean isActive();

    void runOnUiThread(Runnable runnable);
}
