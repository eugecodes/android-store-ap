package caribouapp.caribou.com.cariboucoffee.mvp.trivia.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentTriviaPauseBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment;

public class FragmentTriviaPause extends BaseFragment<FragmentTriviaPauseBinding> {

    private OnPauseListener mOnPauseListener;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_trivia_pause;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        getBinding().tvNotReadyToPlay.setOnClickListener(v -> getActivity().finish());
        getBinding().ibResumeTrivia.setOnClickListener(v ->
                mOnPauseListener.unpauseClicked()
        );
        if (getActivity() instanceof OnPauseListener) {
            mOnPauseListener = (OnPauseListener) getActivity();
        }
        return view;
    }

    public interface OnPauseListener {
        void unpauseClicked();
    }
}
