package caribouapp.caribou.com.cariboucoffee.mvp.webflow.view;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.content.ContextCompat;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivitySourceWebBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.SourceWebContract;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.presenter.CaptivePortalWebPresenter;

/**
 * Created by gonzalogelos on 3/5/18.
 */

public class CaptivePortalWebActivity extends BaseActivity<ActivitySourceWebBinding> implements SourceWebContract.CaptivePortalWeb.View {


    private CaptivePortalWebPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_source_web;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getBinding().tbTitle.setText(R.string.captive_portal_web_view_title);

        CaptivePortalWebPresenter captivePortalWebPresenter = new CaptivePortalWebPresenter(this);
        SourceApplication.get(this).getComponent().inject(captivePortalWebPresenter);
        mPresenter = captivePortalWebPresenter;

        WebView webView = getBinding().contentIncluded.wvRoot;
        getBinding().ablAppBarLayout.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        getBinding().tbTitle.setTextColor(getResources().getColor(R.color.blackColor));
        getBinding().tb.getNavigationIcon()
                .setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primaryMidDarkColor), PorterDuff.Mode.SRC_ATOP);
        webView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(AppConstants.CHECK_CAPTIVE_PORTAL_URL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void finishCaptivePortalWeb() {
        finish();
    }

    @Override
    protected boolean isCaptivePortalCheckEnabled() {
        return false;
    }

    private class CustomWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mPresenter.checkCaptivePortal();
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mPresenter.checkCaptivePortal();
        }
    }
}
