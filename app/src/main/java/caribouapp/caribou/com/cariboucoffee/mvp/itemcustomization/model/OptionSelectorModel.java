package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model;

import java.util.List;

/**
 * Created by asegurola on 3/26/18.
 */

public class OptionSelectorModel {
    private int mSelectedOption = 0;

    private List<ItemOption> mOptions;

    private ItemOption mDefaultOption;

    public void selectNext() {
        moveSelectionBy(1);
    }

    public void selectPrevious() {
        moveSelectionBy(-1);
    }

    private void moveSelectionBy(int offset) {
        mSelectedOption = (mSelectedOption + mOptions.size() + offset) % mOptions.size();
    }

    public ItemOption getItemOptionSelected() {
        if (mSelectedOption == -1) {
            return null;
        }
        return mOptions.get(mSelectedOption);
    }

    public List<ItemOption> getOptions() {
        return mOptions;
    }

    public void setOptions(List<ItemOption> options) {
        mOptions = options;
    }

    public boolean isDefaultSelected() {
        return !mOptions.isEmpty() && mDefaultOption != null && mOptions.get(mSelectedOption).getId().equals(mDefaultOption.getId());
    }

    public ItemOption getDefaultOption() {
        return mDefaultOption;
    }

    public void setDefaultOption(ItemOption defaultOption) {
        mDefaultOption = defaultOption;
        mSelectedOption = mOptions.indexOf(defaultOption);
    }

    public void selectIndex(int index) {
        mSelectedOption = index;
    }

    public void reset() {
        if (mDefaultOption == null) {
            mSelectedOption = 0;
        }
        mSelectedOption = mOptions.indexOf(mDefaultOption);
    }

    public void select(ItemOption itemOption) {
        selectIndex(mOptions.indexOf(itemOption));
    }
}
