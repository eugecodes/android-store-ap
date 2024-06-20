package caribouapp.caribou.com.cariboucoffee;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.urbanairship.messagecenter.MessageCenterActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import caribouapp.caribou.com.cariboucoffee.analytics.TriviaEventSource;
import caribouapp.caribou.com.cariboucoffee.mvp.account.view.AccountActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SignInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.AddFundsActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.AutoReloadActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.CheckInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view.SignUpActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.faq.view.FaqActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationsActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.view.PickLocationActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.view.RecentOrderActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.termsandprivacy.view.TermsAndPrivacyActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.view.TriviaCountdownActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.view.CashStarWebView;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.view.SourceWebActivity;
import caribouapp.caribou.com.cariboucoffee.util.DeeplinkUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;

public class DeeplinkParser {

    private static final String TAG = DeeplinkUtil.class.getSimpleName();

    private static final String SEGMENT_INBOX = "inbox";

    private static final String SEGMENT_CHECK_IN = "checkin";

    private static final String SEGMENT_ADD_FUNDS = "add_funds";

    private static final String SEGMENT_AUTO_RELOAD = "auto_reload";

    private static final String SEGMENT_FEATURED = "featured";

    private static final String SEGMENT_LOCATIONS_MAP = "locations_map";

    private static final String SEGMENT_TRANSACTION_HISTORY = "transaction_history";

    private static final String SEGMENT_PERSONAL_INFO = "personal_info";

    private static final String SEGMENT_REWARDS_CARD = "rewards_card";

    private static final String SEGMENT_CREDIT_CARD = "credit_card";

    private static final String SEGMENT_EGIFT = "egift";

    private static final String SEGMENT_TRIVIA = "trivia";

    private static final String SEGMENT_TERMS_AND_CONDITIONS = "terms";

    private static final String SEGMENT_FAQS = "faq";

    private static final String SEGMENT_TRANSFER_REPLACE_REWARDS_CARD = "transfer_replace_card";

    private static final String SEGMENT_ORDER_AHEAD = "order_ahead";

    private static final String SEGMENT_QUICK_REORDER = "quick_reorder";

    private static final String SEGMENT_ADD_PERK = "claim-reward";

    private static final String SEGMENT_SHARE_A_PERK = "share-a-perk";

    private static final String SEGMENT_SIGN_UP = "sign_up";

    @Inject
    AppDataStorage mAppDataStorage;

    @Inject
    UserServices mUserServices;

    public void openDeepLink(Context context, Uri deepLink) {
        SourceApplication.get(context).getComponent().inject(this);


        Intent intent;
        if (!mUserServices.isUserLoggedIn()) {
            intent = buildCustomLogOutDeepLinks(context, deepLink);
            if (intent != null) {
                context.startActivity(intent);
            } else {
                Intent deeplinkIntent = new Intent(Intent.ACTION_VIEW, deepLink);
                context.startActivity(SignInActivity.createIntent(context, deeplinkIntent));
            }
            return;
        }

        String scheme = deepLink.getScheme();
        if ("http".equals(scheme) || "https".equals(scheme)) {
            intent = buildWebDeeplinkIntent(context, deepLink);
        } else {
            intent = buildCustomDeeplink(context, deepLink);
        }

        if (intent == null) {
            return;
        }

        context.startActivity(intent);
    }

    private Intent buildWebDeeplinkIntent(Context context, Uri deepLink) {
        String firstPathSegment = deepLink.getPathSegments().get(0);
        if (SEGMENT_ADD_PERK.equals(firstPathSegment)) {
            String pathPlusQuery = deepLink.getPath();
            if (deepLink.getQuery() != null) {
                pathPlusQuery = pathPlusQuery + "?" + deepLink.getQuery();
            }
            return SourceWebActivity
                    .createIntentFromPath(context,
                            context.getString(R.string.share_a_perk_title),
                            pathPlusQuery,
                            DeeplinkUtil.buildSourceAppFinishActivity(context),
                            true, true);
        } else {
            Log.e(TAG, new LoginException("Unknown web deeplink: " + deepLink.toString()));
            return null;
        }
    }

    private Intent buildCustomDeeplink(Context context, Uri deepLink) {
        List<String> segments = deepLink.getPathSegments();

        if (segments.isEmpty()) {
            return null;
        }

        Intent intent = null;
        for (String segment : segments) {
            if (SEGMENT_INBOX.equals(segment)) {
                // Launch the inbox activity
                intent = new Intent(context, MessageCenterActivity.class);
            } else if (SEGMENT_CHECK_IN.equals(segment)) {
                intent = new Intent(context, CheckInActivity.class);
            } else if (SEGMENT_ADD_FUNDS.equals(segment)) {
                intent = AddFundsActivity.createIntent(context);
            } else if (SEGMENT_AUTO_RELOAD.equals(segment)) {
                intent = AutoReloadActivity.createIntent(context);
            } else if (SEGMENT_FEATURED.equals(segment)) {
                intent = MenuActivity.createIntent(context, false, MenuActivity.MenuOrigin.OTHER);
            } else if (SEGMENT_LOCATIONS_MAP.equals(segment)) {
                intent = new Intent(context, LocationsActivity.class);
            } else if (SEGMENT_TRANSACTION_HISTORY.equals(segment)) {
                intent = AccountActivity.createIntent(context, AccountActivity.TRANSACTION_HISTORY_INDEX);
            } else if (SEGMENT_PERSONAL_INFO.equals(segment)) {
                intent = AccountActivity.createIntent(context, AccountActivity.PROFILE_INDEX);
            } else if (SEGMENT_REWARDS_CARD.equals(segment)) {
                intent = AccountActivity.createIntent(context, AccountActivity.REWARD_CARD_INDEX);
            } else if (SEGMENT_CREDIT_CARD.equals(segment)) {
                intent = AccountActivity.createIntent(context, AccountActivity.CREDIT_CARD_INDEX);
            } else if (SEGMENT_EGIFT.equals(segment)) {
                intent = new Intent(context, CashStarWebView.class);
            } else if (SEGMENT_TERMS_AND_CONDITIONS.equals(segment)) {
                intent = new Intent(context, TermsAndPrivacyActivity.class);
            } else if (SEGMENT_FAQS.equals(segment)) {
                intent = new Intent(context, FaqActivity.class);
            } else if (SEGMENT_TRIVIA.equals(segment)) {
                mAppDataStorage.setTriviaEventSource(TriviaEventSource.Deeplink);
                intent = new Intent(context, TriviaCountdownActivity.class);
            } else if (SEGMENT_TRANSFER_REPLACE_REWARDS_CARD.equals(segment)) {
                intent =
                        SourceWebActivity
                                .createIntentFromPath(context, null,
                                        context.getString(R.string.managecard_path),
                                        DeeplinkUtil.buildSourceAppFinishActivity(context),
                                        true, false);
            } else if (SEGMENT_ORDER_AHEAD.equals(segment)) {
                intent = new Intent(context, PickLocationActivity.class);
            } else if (SEGMENT_QUICK_REORDER.equals(segment)) {
                intent = new Intent(context, RecentOrderActivity.class);
            } else if (SEGMENT_SHARE_A_PERK.equals(segment)) {
                intent = SourceWebActivity
                        .createIntentFromPath(context, context.getString(R.string.share_a_perk_title),
                                context.getString(R.string.share_a_perk_path),
                                DeeplinkUtil.buildSourceAppFinishActivity(context),
                                true, false);
            }
        }

        Bundle extras = parseOptions(deepLink);
        if (intent != null && extras != null) {
            intent.putExtras(extras);
        }
        return intent;
    }

    private Intent buildCustomLogOutDeepLinks(Context context, Uri deepLink) {
        List<String> segments = deepLink.getPathSegments();

        if (segments.isEmpty()) {
            return null;
        }

        Intent intent = null;
        for (String segment : segments) {
            if (SEGMENT_SIGN_UP.equals(segment)) {
                intent = new Intent(context, SignUpActivity.class);
            }
        }
        Bundle extras = parseOptions(deepLink);
        if (intent != null && extras != null) {
            intent.putExtras(extras);
        }
        return intent;
    }

    private Bundle parseOptions(Uri deepLink) {
        Bundle options = new Bundle();

        for (String key : deepLink.getQueryParameterNames()) {
            List<String> values = deepLink.getQueryParameters(key);
            if (values == null) {
                continue;
            }

            if (values.size() == 1) {
                options.putString(key, values.get(0));
            } else if (values.size() > 1) {
                options.putStringArrayList(key, new ArrayList<>(values));
            }
        }

        return options;
    }
}
