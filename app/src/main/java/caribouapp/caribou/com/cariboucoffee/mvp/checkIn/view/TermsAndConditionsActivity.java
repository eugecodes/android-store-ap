package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityAutoreloadTNCBinding;

/**
 * Created by jmsmuy on 12/1/17.
 */

public class TermsAndConditionsActivity extends BaseActivity<ActivityAutoreloadTNCBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().ivClose.setOnClickListener((view) -> finish());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_autoreload_t_n_c;
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.TERMS;
    }
}
