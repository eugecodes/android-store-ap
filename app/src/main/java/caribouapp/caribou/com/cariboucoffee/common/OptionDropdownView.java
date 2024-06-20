package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutOptionDropdownListBinding;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;

public class OptionDropdownView<T extends OptionItem> extends OptionItemView<T> implements AdapterView.OnItemSelectedListener {

    private LayoutOptionDropdownListBinding mBinding;
    private List<OptionItem> mOptions;

    public OptionDropdownView(@NonNull Context context) {
        super(context);
        init();
    }

    public OptionDropdownView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OptionDropdownView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBinding = LayoutOptionDropdownListBinding.inflate(LayoutInflater.from(getContext()), this, true);
        mBinding.spnOtherOptions.setOnItemSelectedListener(this);
        mBinding.cbOtherOptions.setOnClickListener(v -> mBinding.spnOtherOptions.performClick());
    }

    public void setList(List<OptionItem> options) {
        UIUtil.setupOptionsSpinner(mBinding.spnOtherOptions, getContext().getString(R.string.other).toUpperCase(Locale.US), options);
        mOptions = options;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            // Ignore empty option selection
            return;
        }
        OptionItem option = mOptions.get(position - 1);
        getListener().optionPressed(option);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        getListener().optionPressed(null);
    }

    @Override
    public void deselect() {
        mBinding.cbOtherOptions.setSelected(false);
        mBinding.cbOtherOptions.setText(R.string.other);
    }

    @Override
    public void selectIfValueMatches(T selectedOptionItem) {
        deselect();
        if (selectedOptionItem == null) {
            return;
        }
        for (OptionItem optionItem : mOptions) {
            if (selectedOptionItem.getValue().equals(optionItem.getValue())) {
                mBinding.cbOtherOptions.setText(optionItem.getDescription());
                mBinding.cbOtherOptions.setContentDescription(optionItem.getContentDescription());
                mBinding.cbOtherOptions.setSelected(true);
            }
        }
    }
}
