package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.databinding.CustomTabMenuBinding;
import caribouapp.caribou.com.cariboucoffee.util.DelegatedFragmentPagerAdapter;

/**
 * Created by jmsmuy on 11/22/17.
 */

public final class TabFactory {

    private static final int TOTAL_ANIMATION_TIME_IN_MS = 500;
    private static final float UNSELECTED_ALPHA = 0.5f;
    private static final float VISIBLE_ALPHA = 1;
    private static final float HIDDEN_ALPHA = 0;

    private TabFactory() {
    }

    public static void modTab(int drawable, int text, int tabNumber, boolean selectedTab,
                              LayoutInflater layoutInflater, Context context, TabLayout tabLayout,
                              boolean showTabTextView, String contentDescription) {
        CustomTabMenuBinding tab = createTabView(layoutInflater, context, text, drawable, selectedTab, showTabTextView);
        tabLayout.getTabAt(tabNumber).setCustomView(tab.getRoot());
        tabLayout.getTabAt(tabNumber).setTag(context.getString(text));
        tab.getRoot().setContentDescription(contentDescription);
        if (selectedTab) {
            tabLayout.getTabAt(tabLayout.getTabCount() - 1).select();
        }
    }

    public static CustomTabMenuBinding createTabView(LayoutInflater layoutInflater, Context context,
                                                     int text, int drawable, boolean selectedTab, boolean showTabTexView) {
        CustomTabMenuBinding tab = CustomTabMenuBinding.inflate(layoutInflater);
        tab.ivTab.setImageDrawable(ContextCompat.getDrawable(context, drawable));
        tab.tvTab.setText(context.getString(text));
        if (selectedTab) {
            tab.ivTab.setAlpha(VISIBLE_ALPHA);
            tab.tvTab.setAlpha(VISIBLE_ALPHA);
            tab.tvTab.setVisibility(View.VISIBLE);
        } else if (showTabTexView) {
            tab.tvTab.setAlpha(UNSELECTED_ALPHA);
            tab.tvTab.setVisibility(View.VISIBLE);
        }
        return tab;
    }

    public static TabLayout.OnTabSelectedListener createFadeTabListener(boolean showTabTexView) {
        return createFadeTabListener(null, showTabTexView);
    }

    public static TabLayout.OnTabSelectedListener createFadeTabListener(TabLayout.OnTabSelectedListener listener, boolean showTabTexView) {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                CustomTabMenuBinding binding = DataBindingUtil.getBinding(tab.getCustomView());
                if (binding != null) {
                    binding.tvTab.setVisibility(View.VISIBLE);
                    binding.tvTab.animate().alpha(VISIBLE_ALPHA).setDuration(TOTAL_ANIMATION_TIME_IN_MS).start();
                    binding.ivTab.animate().alpha(VISIBLE_ALPHA).setDuration(TOTAL_ANIMATION_TIME_IN_MS).start();
                }

                if (listener != null) {
                    listener.onTabSelected(tab);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                CustomTabMenuBinding binding = DataBindingUtil.getBinding(tab.getCustomView());
                if (binding != null) {
                    if (!showTabTexView) {
                        binding.tvTab.setAlpha(HIDDEN_ALPHA);
                        binding.tvTab.setVisibility(View.GONE);
                    } else {
                        binding.tvTab.setAlpha(UNSELECTED_ALPHA);
                    }
                    binding.ivTab.animate().alpha(UNSELECTED_ALPHA).setDuration(TOTAL_ANIMATION_TIME_IN_MS).start();
                }

                if (listener != null) {
                    listener.onTabUnselected(tab);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (listener != null) {
                    listener.onTabReselected(tab);
                }
            }
        };
    }

    public static <VIEW extends View, FRAGMENT extends Fragment> DelegatedFragmentPagerAdapter createWithContent(
            FragmentManager fragmentManager, List<TabContent<VIEW, FRAGMENT>> tabContents) {
        return new DelegatedFragmentPagerAdapter(
                fragmentManager,
                position -> tabContents.get(position).getContent(),
                tabContents::size,
                position -> tabContents.get(position).getTitle()
        );
    }

    public static class TabContent<VIEW extends View, FRAGMENT extends Fragment> {
        private VIEW tab;
        private String title;
        private FRAGMENT content;

        public TabContent(FRAGMENT content) {
            this(null, content, null);
        }

        public TabContent(String title, FRAGMENT content) {
            this(null, content, title);
        }

        public TabContent(VIEW tab, FRAGMENT content) {
            this(tab, content, null);
        }

        public TabContent(VIEW tab, FRAGMENT content, String title) {
            this.tab = tab;
            this.content = content;
            this.title = title;
        }

        public VIEW getTab() {
            return tab;
        }

        public String getTitle() {
            return title;
        }

        public FRAGMENT getContent() {
            return content;
        }
    }

}
