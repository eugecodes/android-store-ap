package caribouapp.caribou.com.cariboucoffee.mvp.webflow.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivitySourceWebBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.SourceWebContract;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.presenter.SourceWebPresenter;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

public class SourceWebActivity extends BaseActivity<ActivitySourceWebBinding> implements SourceWebContract.View {

    private static final String TAG = SourceWebActivity.class.getSimpleName();

    private static final String EXTRA_URL = "url";
    private static final String EXTRA_REDIRECT_URL = "redirectUrl";
    private static final String EXTRA_SEND_AUTH_TOKEN = "sendAuthToken";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_FULLSCREEN = "fullscreen";

    private static final String QUERY_PARAM_FULLSCREEN = "mAppFullscreen";

    private static final String HOST_DEEPLINK = "deeplink";
    private static final String INTENT_TEXT_PLAIN_TYPE = "text/plain";
    private static final String MAILTO_SCHEME = "mailto";
    private static final String PATH_FINISH_ACTIVITY = "/finish";

    private SourceWebContract.Presenter mPresenter;

    public static Intent createIntentFromPath(Context context, String title, String webPath,
                                              String redirectUrl, boolean sendToken, boolean fullscreen) {
        Intent intent =  createIntentWithOutUrl(context, title, redirectUrl, sendToken, fullscreen);
        intent.putExtra(EXTRA_URL, BuildConfig.REWARDS_URL + webPath);
        return intent;
    }

    public static Intent createIntentFromUrl(Context context, String title, String webUrl,
                                             String redirectUrl, boolean sendToken, boolean fullscreen) {
        Intent intent =  createIntentWithOutUrl(context, title, redirectUrl, sendToken, fullscreen);
        intent.putExtra(EXTRA_URL, webUrl);
        return intent;
    }

    private static Intent createIntentWithOutUrl(Context context, String title,
                                                 String redirectUrl, boolean sendToken, boolean fullscreen) {
        Intent intent = new Intent(context, SourceWebActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_REDIRECT_URL, redirectUrl);
        intent.putExtra(EXTRA_SEND_AUTH_TOKEN, sendToken);
        intent.putExtra(EXTRA_FULLSCREEN, fullscreen);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_source_web;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && isDeepLinkIntent()) {
            // We receive a deep link but the webview activity wasn't being displayed. We should ignore it.
            finish();
            return;
        }

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        getBinding().tbTitle.setText(title == null ? getText(R.string.brand_app_name) : title);
        getBinding().tbTitle.setContentDescription(getString(R.string.heading_cd, getBinding().tbTitle.getText()));

        SourceWebPresenter sourceWebPresenter = new SourceWebPresenter(this);
        SourceApplication.get(this).getComponent().inject(sourceWebPresenter);
        mPresenter = sourceWebPresenter;


        setWebviewFullscreen(isFullscreen());

        String webUrl = getIntent().getStringExtra(EXTRA_URL);
        String redirectUtl = getIntent().getStringExtra(EXTRA_REDIRECT_URL);
        boolean sendAuthToken = getIntent().getBooleanExtra(EXTRA_SEND_AUTH_TOKEN, false);
        mPresenter.loadUrl(sendAuthToken, webUrl, redirectUtl);
    }

    private boolean isFullscreen() {
        return getIntent().getBooleanExtra(EXTRA_FULLSCREEN, false);
    }

    private boolean isDeepLinkIntent() {
        return getIntent().getData() != null && getString(R.string.applink_scheme).equals(getIntent().getData().getScheme());
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void updateUrl(String authToken, String url, String redirectUrl) {

        try {
            // Add the return url as a query parameter. This way the mobile app will now where to redirect the user after the use case finishes.
            String baseUrl = url + (url.contains("?") ? "&" : "?") + "mobileAppReturnUrl=" + URLEncoder.encode(redirectUrl, "UTF-8");
            String urlWithAuthToken = baseUrl + "&authToken=" + URLEncoder.encode(authToken, "UTF-8");

            // init webview
            WebView webView = getBinding().contentIncluded.wvRoot;
            webView.setWebViewClient(new CustomWebViewClient());
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            Log.d(TAG, "Loading website: " + urlWithAuthToken);
            webView.loadUrl(urlWithAuthToken);

        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, new LogErrorException("Error loading web content", e));
            showError(e);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Manage deep link actions called from the embedded mobile webapp
        if (getString(R.string.applink_scheme).equalsIgnoreCase(intent.getScheme())) {
            checkDeeplinkResult(intent.getData());
        } else {
            super.onNewIntent(intent);
        }
    }

    private boolean checkDeeplinkResult(String deeplinkUrl) {
        return checkDeeplinkResult(Uri.parse(deeplinkUrl));
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.SHARE_A_PERK;
    }

    private boolean checkDeeplinkResult(Uri uri) {
        String url = uri.toString();
        if (uri.isOpaque() && MAILTO_SCHEME.equalsIgnoreCase(uri.getScheme())) {
            Intent sendIntent = new Intent();
            MailTo mailTo = MailTo.parse(url);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, mailTo.getSubject());
            sendIntent.putExtra(Intent.EXTRA_TEXT, mailTo.getBody());
            sendIntent.setType(INTENT_TEXT_PLAIN_TYPE);
            startActivity(Intent.createChooser(sendIntent, getString(R.string.share_a_perk_action)));
            return true;
        }
        // Action to finish the current activity called by the embedded webapp when the user reaches the end of the use case.
        if (PATH_FINISH_ACTIVITY.equals(uri.getPath())) {
            Intent data = new Intent();
            data.setData(uri);
            setResult(RESULT_OK, data);
            finish();
            return true;
        } else if (HOST_DEEPLINK.equals(uri.getHost())) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);
            finish();
            startActivity(intent);
            return true;
        }
        return false;
    }

    private void setWebviewFullscreen(boolean goToFullscreen) {
        runOnUiThread(() -> {
            boolean isFullscreenActive = getBinding().ablAppBarLayout.getVisibility() == View.GONE;
            if (goToFullscreen && isFullscreenActive || !goToFullscreen && !isFullscreenActive) {
                // Nothing to do. The current fullscreen status is the desired one.
                return;
            }
            getBinding().ablAppBarLayout.setVisibility(goToFullscreen ? View.GONE : View.VISIBLE);
            getBinding().getRoot().requestLayout();
        });
    }

    private class CustomWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (checkDeeplinkResult(url)) {
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (url.startsWith(getString(R.string.applink_scheme))) {
                checkDeeplinkResult(url);
                return;
            }

            Uri uri = Uri.parse(url);
            String fullscreenString = uri.getQueryParameter(QUERY_PARAM_FULLSCREEN);
            if (fullscreenString != null) {
                setWebviewFullscreen(Boolean.parseBoolean(fullscreenString));

            }

            showLoadingLayer();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideLoadingLayer();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            hideLoadingLayer();
        }
    }
}
