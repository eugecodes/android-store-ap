package caribouapp.caribou.com.cariboucoffee.mvp.oos;

import android.content.Intent;
import android.view.MenuItem;

import androidx.databinding.ViewDataBinding;

import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity;

/**
 * Created by jmsmuy on 17/04/18.
 */

public abstract class OOSFlowActivity<T extends ViewDataBinding> extends BaseActivity<T> implements OOSFlowContract.View {

    private OOSFlowContract.Presenter mOOSFlowPresenter;

    @Override
    protected void onResume() {
        super.onResume();
        mOOSFlowPresenter.orderTimeoutEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOOSFlowPresenter.orderTimeoutEnabled(false);
        mOOSFlowPresenter.updateLastActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mOOSFlowPresenter.checkForOrderDeletion();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mOOSFlowPresenter.checkForOrderDeletion();
    }


    @Override
    public void goToDashboard() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public OOSFlowContract.Presenter getOOSFlowPresenter() {
        return mOOSFlowPresenter;
    }

    public void setOOSFlowPresenter(OOSFlowContract.Presenter oosFlowPresenter) {
        mOOSFlowPresenter = oosFlowPresenter;
    }
}
