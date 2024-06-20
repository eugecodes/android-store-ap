package caribouapp.caribou.com.cariboucoffee.mvp;

import androidx.fragment.app.Fragment;

import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;

public class FragmentMvpViewDelegate implements MvpView {

    private Fragment mFragment;

    public FragmentMvpViewDelegate(Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void showMessage(int messageResId) {
        BaseActivity activity = (BaseActivity) mFragment.getActivity();
        if (activity != null) {
            activity.showMessage(messageResId);
        }
    }

    @Override
    public void showMessage(String message) {
        BaseActivity activity = (BaseActivity) mFragment.getActivity();
        if (activity != null) {
            activity.showMessage(message);
        }
    }

    @Override
    public void showWarning(int messageResId) {
        BaseActivity activity = (BaseActivity) mFragment.getActivity();
        if (activity != null) {
            activity.showWarning(messageResId);
        }
    }

    @Override
    public void showWarning(int messageResId, Object... args) {
        BaseActivity activity = (BaseActivity) mFragment.getActivity();
        if (activity != null) {
            activity.showWarning(messageResId, args);
        }
    }

    @Override
    public void showWarning(String message) {
        BaseActivity activity = (BaseActivity) mFragment.getActivity();
        if (activity != null) {
            activity.showWarning(message);
        }
    }

    @Override
    public void showError(Throwable throwable) {
        BaseActivity activity = (BaseActivity) mFragment.getActivity();
        if (activity != null) {
            activity.showError(throwable);
        }
    }

    @Override
    public void showDebugDialog(String title, String message) {
        BaseActivity activity = (BaseActivity) mFragment.getActivity();
        if (activity != null) {
            activity.showDebugDialog(title, message);
        }
    }

    @Override
    public void showLoadingLayer() {
        showLoadingLayer(false);
    }

    @Override
    public void showLoadingLayer(boolean showProcessingText) {
        BaseActivity activity = (BaseActivity) mFragment.getActivity();
        if (activity != null) {
            activity.showLoadingLayer(showProcessingText);
        }
        // TODO maybe we should have a parameter to call the loading state within the fragment
    }

    @Override
    public void hideLoadingLayer() {
        BaseActivity activity = (BaseActivity) mFragment.getActivity();
        if (activity != null) {
            activity.hideLoadingLayer();
        }
        // TODO maybe we should have a parameter to call the loading state within the fragment
    }

    @Override
    public boolean isActive() {
        BaseActivity activity = (BaseActivity) mFragment.getActivity();
        return activity != null && activity.isActive();
    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        if (mFragment.getActivity() == null) {
            return;
        }
        mFragment.getActivity().runOnUiThread(runnable);
    }
}
