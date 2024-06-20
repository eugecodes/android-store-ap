package caribouapp.caribou.com.cariboucoffee.mvp.termsandprivacy.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.common.WebContentSection;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentWebContentBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment;

/**
 * Created by andressegurola on 11/14/17.
 */
@FragmentWithArgs
public class WebContentFragment extends BaseFragment<FragmentWebContentBinding> {

    @Arg
    WebContentSection mWebContentSection;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_web_content;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this); // read @Arg fields
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        getBinding().setModel(mWebContentSection);
        return rootView;
    }
}
