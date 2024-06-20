package caribouapp.caribou.com.cariboucoffee.mvp.faq.view;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.common.WebContentWithSectionsModel;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityTabbedCmsContentBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.faq.FaqContract;
import caribouapp.caribou.com.cariboucoffee.mvp.faq.presenter.FaqPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.termsandprivacy.view.WebContentFragmentBuilder;

public class FaqActivity extends BaseActivity<ActivityTabbedCmsContentBinding> implements FaqContract.View {

    private FaqContract.Presenter mPresenter;
    private ViewPagerAdapter mTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getBinding().tbTitle.setText(R.string.title_activity_faq_title);
        getBinding().tbTitle.setContentDescription(getString(R.string.heading_cd, getBinding().tbTitle.getText()));

        FaqPresenter presenter = new FaqPresenter(this, null);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;

        mPresenter.loadContent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tabbed_cms_content;
    }

    @Override
    public void updateContent(WebContentWithSectionsModel faqModel) {
        getBinding().tlTabs.setVisibility(View.VISIBLE);
        mTabAdapter = new FaqActivity.ViewPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = getBinding().contentIncluded.vpContent;
        viewPager.setAdapter(mTabAdapter);
        getBinding().tlTabs.setupWithViewPager(viewPager);
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.FAQS;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return
                    WebContentFragmentBuilder
                            .newWebContentFragment(
                                    mPresenter.getModel().getWebContentSections().get(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPresenter.getModel().getWebContentSections().get(position).getTitle();
        }

        @Override
        public int getCount() {
            return mPresenter.getModel().getWebContentSections().size();
        }
    }
}
