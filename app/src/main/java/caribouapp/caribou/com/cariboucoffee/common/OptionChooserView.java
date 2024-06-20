package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutOptionChooserViewBinding;

/**
 * Created by andressegurola on 12/6/17.
 */

public class OptionChooserView<V, T extends OptionItem<V>> extends LinearLayout implements OptionItemView.OptionChooserItemListener<T> {

    private LayoutOptionChooserViewBinding mBinding;
    private OptionChooserListener mListener;
    private List<OptionItemView> mOptionItems = new ArrayList<>();
    private OptionItem mSelected;

    public OptionChooserView(Context context) {
        super(context);
        init();
    }

    public OptionChooserView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OptionChooserView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBinding = LayoutOptionChooserViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    public void setListener(OptionChooserListener listener) {
        mListener = listener;
    }

    public void setSelectedValue(T selectedValue) {
        for (OptionItemView optionItemView : mOptionItems) {
            optionItemView.selectIfValueMatches(selectedValue);
        }
        mSelected = selectedValue;
    }

    public void deselectAllValues() {
        for (OptionItemView optionItemView : mOptionItems) {
            optionItemView.deselect();
        }
        mSelected = null;
    }

    @Override
    public void optionPressed(T selectedOption) {
        setSelectedValue(selectedOption);
        mListener.optionChosen(selectedOption);
    }

    public void setOptions(List<OptionItem> options, List<OptionItem> dropdownOptions) {
        // We first create views for the main options
        setOptions(options);

        if (dropdownOptions != null) {
            final OptionDropdownView dropdownView = new OptionDropdownView(getContext());
            dropdownView.setList(dropdownOptions);
            dropdownView.setListener(this);
            addOptionView(dropdownView);
        }
    }

    public void setOptionsWithNone(List<OptionItem> options) {
        mBinding.llOptionsContainer.removeAllViews();
        OptionNoneButtonView noneButtonView = new OptionNoneButtonView(getContext());
        noneButtonView.setListener(this);
        noneButtonView.setSelected(true);
        addOptionView(noneButtonView);

        setOptions(options);
    }

    public void setOptions(List<OptionItem> optionItems) {
        if (optionItems == null) {
            return;
        }
        int i = 1;
        int max = optionItems.size();
        for (OptionItem option : optionItems) {
            final OptionButtonView buttonView = new OptionButtonView(getContext());
            buttonView.setOptionValue(option, getContext().getString(R.string.option_cd, i, max, option.getContentDescription()));
            buttonView.setListener(this);
            addOptionView(buttonView);
            i++;
        }
    }

    public OptionItem<V> getMainOptionForValue(V value) {
        for (OptionItemView option : mOptionItems) {
            if (option instanceof OptionButtonView) {
                final OptionButtonView optionButtonView = (OptionButtonView) option;
                if (optionButtonView.getOptionItem().getValue().equals(value)) {
                    return optionButtonView.getOptionItem();
                }
            }
        }
        return null;
    }

    private void addOptionView(OptionItemView optionItemView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                1.0f);

        mBinding.llOptionsContainer.addView(optionItemView, layoutParams);
        mOptionItems.add(optionItemView);
    }

    public OptionItem getSelected() {
        return mSelected;
    }

    public interface OptionChooserListener<T extends OptionItem> {

        void optionChosen(T option);

    }

}
