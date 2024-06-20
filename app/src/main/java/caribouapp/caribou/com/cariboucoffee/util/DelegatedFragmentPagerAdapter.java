package caribouapp.caribou.com.cariboucoffee.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class DelegatedFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private ItemDelegate mItemDelegate;
    private CountDelegate mCountDelegate;
    private TitleDelegate mTitleDelegate;

    public DelegatedFragmentPagerAdapter(FragmentManager fragmentManager,
                                         @NonNull ItemDelegate itemDelegate,
                                         @NonNull CountDelegate countDelegate) {
        this(fragmentManager, itemDelegate, countDelegate, position -> null);
    }

    public DelegatedFragmentPagerAdapter(FragmentManager fragmentManager,
                                         @NonNull ItemDelegate itemDelegate,
                                         @NonNull CountDelegate countDelegate,
                                         @NonNull TitleDelegate titleDelegate) {
        super(fragmentManager);
        mItemDelegate = itemDelegate;
        mCountDelegate = countDelegate;
        mTitleDelegate = titleDelegate;
    }

    @Override
    public int getCount() {
        return mCountDelegate.getCount();
    }

    @Override
    public Fragment getItem(int position) {
        return mItemDelegate.getItem(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = mTitleDelegate.getTitle(position);

        return title != null ? title : super.getPageTitle(position);
    }

    public interface ItemDelegate {
        Fragment getItem(int position);
    }

    public interface CountDelegate {
        int getCount();
    }

    public interface TitleDelegate {
        String getTitle(int position);
    }
}
