package caribouapp.caribou.com.cariboucoffee.mvp.account.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.common.TabFactory;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityAccountBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;

/**
 * Created by jmsmuy on 11/21/17.
 */

public class AccountActivity extends BaseActivity<ActivityAccountBinding> implements AccountContract.View {

    public static final int TRANSACTION_HISTORY_INDEX = 0;
    public static final int REWARD_CARD_INDEX = 1;
    public static final int PROFILE_INDEX = 2;
    public static final int CREDIT_CARD_INDEX = 3;

    private static final String EXTRA_SELECTED_TAB = "selectedTab";

    public static Intent createIntent(Context context, int selectedTab) {
        Intent intent = new Intent(context, AccountActivity.class);
        intent.putExtra(EXTRA_SELECTED_TAB, selectedTab);
        return intent;
    }

    private PagerAdapter mAdapterViewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        List<TabFactory.TabContent<View, Fragment>> pagerFragmentTabs = setupPagerFragmentTabs();

        mAdapterViewPager = TabFactory.createWithContent(getSupportFragmentManager(), pagerFragmentTabs);

        getBinding().contentIncluded.vpContent.setOffscreenPageLimit(getSupportFragmentManager().getFragments().size());
        getBinding().contentIncluded.vpContent.setAdapter(mAdapterViewPager);
        getBinding().tlTabs.setupWithViewPager(getBinding().contentIncluded.vpContent);
        addCustomLayoutToTabs(pagerFragmentTabs);

        getBinding().tlTabs.addOnTabSelectedListener(TabFactory.createFadeTabListener(true));

        Objects.requireNonNull(getBinding().tlTabs
                .getTabAt(getIntent().getIntExtra(EXTRA_SELECTED_TAB, TRANSACTION_HISTORY_INDEX)))
                .select();
    }

    private void addCustomLayoutToTabs(List<TabFactory.TabContent<View, Fragment>> pagerFragmentTabs) {
        for (int i = 0; i < getBinding().tlTabs.getTabCount(); i++) {
            TabLayout.Tab tab = getBinding().tlTabs.getTabAt(i);
            tab.setCustomView(pagerFragmentTabs.get(i).getTab());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_CARIBOU_WEBSITE) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.MY_ACCOUNT;
    }

    private List<TabFactory.TabContent<View, Fragment>> setupPagerFragmentTabs() {
        List<TabFactory.TabContent<View, Fragment>> pagerFragmentsTabs = new ArrayList<>();
        pagerFragmentsTabs.add(
                TRANSACTION_HISTORY_INDEX,
                new TabFactory.TabContent(
                        TabFactory.createTabView(AccountActivity.this.getLayoutInflater(),
                                AccountActivity.this, R.string.transaction_title,
                                R.drawable.transaction_history_tab, true, true).getRoot(),
                        new TransactionHistoryFragment()
                )
        );

        pagerFragmentsTabs.add(
                REWARD_CARD_INDEX,
                new TabFactory.TabContent(
                        TabFactory.createTabView(AccountActivity.this.getLayoutInflater(),
                                AccountActivity.this, R.string.reward_card_title,
                                R.drawable.reward_card_tab, false, true).getRoot(),
                        new RewardCardFragment()
                )
        );

        pagerFragmentsTabs.add(
                PROFILE_INDEX,
                new TabFactory.TabContent(
                        TabFactory.createTabView(AccountActivity.this.getLayoutInflater(),
                                AccountActivity.this, R.string.my_profile_title,
                                R.drawable.my_profile_tab, false, true).getRoot(),
                        new ProfileFragment()
                )
        );

        pagerFragmentsTabs.add(
                CREDIT_CARD_INDEX,
                new TabFactory.TabContent(
                        TabFactory.createTabView(AccountActivity.this.getLayoutInflater(),
                                AccountActivity.this, R.string.credit_card_title,
                                R.drawable.credit_card_tab, false, true).getRoot(),
                        new CreditCardFragment()
                )
        );
        return pagerFragmentsTabs;
    }


}
