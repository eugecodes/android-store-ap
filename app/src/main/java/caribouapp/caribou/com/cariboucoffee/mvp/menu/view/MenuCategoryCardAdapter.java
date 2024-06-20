package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.CardItemMenuBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutSectionHeaderBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardHeaderModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuPresenter;

/**
 * Created by jmsmuy on 10/4/17.
 */

public class MenuCategoryCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER_TYPE = 1;
    public static final int ITEM_TYPE = 2;
    public static final float MENU_ITEM_NOT_AVAILABLE_IMAGE_ALPHA = 0.20f;
    public static final float MENU_ITEM_NOT_AVAILABLE_DESCRIPTION_ALPHA = 0.60f;
    private final MenuContract.Fragments.View mFragment;
    private SettingsServices mSettingsServices;
    private List<MenuCardModel> mItems = new ArrayList<>();

    public MenuCategoryCardAdapter(MenuProductFragment fragment, SettingsServices settingsServices) {
        mFragment = fragment;
        mSettingsServices = settingsServices;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MenuPresenter.HEADER) {
            return new HeaderHolder(LayoutSectionHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == MenuPresenter.ITEM) {
            return new ItemHolder(CardItemMenuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            throw new IllegalStateException("Unknown viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).setHeaderModel((MenuCardHeaderModel) mItems.get(position));
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).setItemModel((MenuCardItemModel) mItems.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof MenuCardHeaderModel) {
            return HEADER_TYPE;
        } else if (mItems.get(position) instanceof MenuCardItemModel) {
            return ITEM_TYPE;
        }
        return -1;
    }

    public void updateData(List<MenuCardModel> modelCards) {
        mItems = new ArrayList<>(modelCards);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private CardItemMenuBinding mBindingItem;

        ItemHolder(CardItemMenuBinding binding) {
            super(binding.getRoot());
            mBindingItem = binding;
        }

        void setItemModel(MenuCardItemModel menuItem) {
            mBindingItem.setMenu(menuItem);
            mBindingItem.getRoot().setTag(menuItem);
            if (menuItem.isAvailableForCurrentLocation()) {
                setMenuItemLayoutAvailable(mBindingItem, menuItem);
            } else {
                setMenuItemLayoutNotAvailable(mBindingItem, menuItem);
            }
            mBindingItem.executePendingBindings();
        }

    }

    private class HeaderHolder extends RecyclerView.ViewHolder {

        private LayoutSectionHeaderBinding mBindingHeader;

        HeaderHolder(LayoutSectionHeaderBinding binding) {
            super(binding.getRoot());
            mBindingHeader = binding;
        }

        void setHeaderModel(MenuCardHeaderModel menuItem) {
            Context context = mBindingHeader.getRoot().getContext();
            mBindingHeader.setSectionTitle(menuItem.getHeader());
            mBindingHeader.setSectionTitleTextSize(context.getResources().getDimension(R.dimen.section_header_menu_item_text_size));
            mBindingHeader.executePendingBindings();
        }

    }

    private void setMenuItemLayoutNotAvailable(CardItemMenuBinding cardItemMenuBinding, MenuCardItemModel menuItem) {
        cardItemMenuBinding.includeMenuImage.menuImage.setAlpha(MENU_ITEM_NOT_AVAILABLE_IMAGE_ALPHA);
        cardItemMenuBinding.llMenuItemDescriptionContainer.setAlpha(MENU_ITEM_NOT_AVAILABLE_DESCRIPTION_ALPHA);
        cardItemMenuBinding.tvItemNotAvailable.setVisibility(View.VISIBLE);
        cardItemMenuBinding.tvItemNotAvailable.setText(mSettingsServices.getMenuItemNotAvailableMessage());
        cardItemMenuBinding.getRoot().setOnClickListener(view -> mFragment.showItemNotAvailableDialog());
        Context context = cardItemMenuBinding.getRoot().getContext();
        cardItemMenuBinding.getRoot().setContentDescription(
                context.getString(R.string.menu_product_content_description_not_available,
                        menuItem.getName(), mSettingsServices.getMenuItemNotAvailableMessage()));
    }

    private void setMenuItemLayoutAvailable(CardItemMenuBinding cardItemMenuBinding, MenuCardItemModel menuItem) {
        cardItemMenuBinding.includeMenuImage.menuImage.setAlpha(1f);
        cardItemMenuBinding.llMenuItemDescriptionContainer.setAlpha(1f);
        cardItemMenuBinding.tvItemNotAvailable.setVisibility(View.GONE);
        cardItemMenuBinding.getRoot().setOnClickListener(view -> mFragment.goToMenuDetails((MenuCardItemModel) view.getTag()));
        Context context = cardItemMenuBinding.getRoot().getContext();
        cardItemMenuBinding.getRoot().setContentDescription(context.getString(R.string.menu_product_content_description, menuItem.getName()));
    }
}
