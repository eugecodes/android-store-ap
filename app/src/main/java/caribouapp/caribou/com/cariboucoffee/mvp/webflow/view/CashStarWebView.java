package caribouapp.caribou.com.cariboucoffee.mvp.webflow.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivitySourceWebBinding;

/**
 * Created by gonzalogelos on 6/6/18.
 */

public class CashStarWebView extends BaseActivity<ActivitySourceWebBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_source_web;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getBinding().tbTitle.setText(getString(R.string.menu_purchase_an_egift_second));
        getBinding().tbTitle.setContentDescription(getString(R.string.heading_cd, getBinding().tbTitle.getText()));
        String url = getString(R.string.cstar_web_view_url);
        // init webview
        WebView webView = getBinding().contentIncluded.wvRoot;
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(url);
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.EGIFT;
    }
}
