package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutOptionButtonViewBinding;

public class OptionButtonView<T extends OptionItem> extends OptionItemView<T> implements View.OnClickListener {
    private LayoutOptionButtonViewBinding mBinding;
    private OptionItem mOptionItem;

    public OptionButtonView(@NonNull Context context) {
        super(context);
        init();
    }

    public OptionButtonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OptionButtonView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBinding = LayoutOptionButtonViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    public void setOptionValue(T option) {
        setOptionValue(option, null);
    }

    public void setOptionValue(T option, String contentDescription) {
        mOptionItem = option;
        mBinding.tvOption.setText(option.getDescription());
        mBinding.tvOption.setOnClickListener(this);
        if (contentDescription == null) {
            mBinding.tvOption.setContentDescription(option.getContentDescription());
        } else {
            mBinding.tvOption.setContentDescription(contentDescription);
        }
    }

    @Override
    public void onClick(View v) {
        getListener().optionPressed(mOptionItem);
    }

    @Override
    public void deselect() {
        mBinding.tvOption.setSelected(false);
    }

    @Override
    public void selectIfValueMatches(T selectedOption) {
        deselect();
        if (selectedOption == null) {
            return;
        }
        if (selectedOption.getValue().equals(mOptionItem.getValue())) {
            mBinding.tvOption.setSelected(true);
        }
    }

    public OptionItem getOptionItem() {
        return mOptionItem;
    }
}
