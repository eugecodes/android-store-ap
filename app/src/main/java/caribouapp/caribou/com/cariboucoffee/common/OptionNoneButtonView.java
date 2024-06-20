package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutOptionNoneButtonViewBinding;

/**
 * Created by fernando on 9/18/20.
 */
public class OptionNoneButtonView<T extends OptionItem> extends OptionItemView<T> {

    private LayoutOptionNoneButtonViewBinding mBinding;

    public OptionNoneButtonView(@NonNull Context context) {
        super(context);
        init();
    }

    public OptionNoneButtonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OptionNoneButtonView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBinding = LayoutOptionNoneButtonViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
        mBinding.tvNoneOption.setOnClickListener(v -> getListener().optionPressed(null));
    }

    @Override
    public void deselect() {
        mBinding.tvNoneOption.setSelected(false);
    }

    @Override
    public void selectIfValueMatches(T selectedOption) {
        deselect();
        if (selectedOption == null) {
            mBinding.tvNoneOption.setSelected(true);
        }
    }
}
