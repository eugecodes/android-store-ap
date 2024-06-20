package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.SplashContract;
import caribouapp.caribou.com.cariboucoffee.SplashPresenter;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentSplashBinding;

public class SplashFragment extends BaseUpdateFragment<FragmentSplashBinding> implements SplashContract.View {

    private SplashPresenter mSplashPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_splash;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        SplashPresenter presenter = new SplashPresenter(this);
        SourceApplication.get(requireContext()).getComponent().inject(presenter);
        mSplashPresenter = presenter;
        return view;
    }

    @Override
    public void updatesFinished() {
        mSplashPresenter.doStartupChecks();
    }

    @Override
    public void startupFinished() {
        if (getActivity() == null) {
            return;
        }
        ((MainActivity) getActivity()).startupFinished();
    }
}
