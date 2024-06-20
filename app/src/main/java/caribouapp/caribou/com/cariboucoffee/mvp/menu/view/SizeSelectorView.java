package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Objects;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutSizeSelectorViewBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;

/**
 * Created by asegurola on 3/14/18.
 */

public class SizeSelectorView extends RadioGroup {
    private static final SizeEnum[] ALL_SIZES = new SizeEnum[]{SizeEnum.SMALL, SizeEnum.MEDIUM, SizeEnum.LARGE, SizeEnum.EXTRA_LARGE};

    private MenuItemSizeListener mListener;

    public interface MenuItemSizeListener {
        void itemSizeChanged(SizeEnum size);
    }

    private LayoutSizeSelectorViewBinding mBinding;

    public SizeSelectorView(Context context) {
        super(context);
        init();
    }

    public SizeSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mBinding = LayoutSizeSelectorViewBinding.inflate(LayoutInflater.from(getContext()), this, true);

        mBinding.rbExtraLargeSize.setOnCheckedChangeListener((compoundButton, value) -> {
            if (value) {
                fireSize(SizeEnum.EXTRA_LARGE);
            }
        });
        mBinding.rbLargeSize.setOnCheckedChangeListener((compoundButton, value) -> {
            if (value) {
                fireSize(SizeEnum.LARGE);
            }
        });
        mBinding.rbMediumSize.setOnCheckedChangeListener((compoundButton, value) -> {
            if (value) {
                fireSize(SizeEnum.MEDIUM);
            }
        });
        mBinding.rbSmallSize.setOnCheckedChangeListener((compoundButton, value) -> {
            if (value) {
                fireSize(SizeEnum.SMALL);
            }
        });

    }

    private void fireSize(SizeEnum size) {
        if (mListener != null) {
            mListener.itemSizeChanged(size);
        }
    }

    public void setItemSizeListener(MenuItemSizeListener listener) {
        mListener = listener;
    }


    public void setSize(SizeEnum size) {
        if (size == null) {
            return;
        }

        Objects.requireNonNull(getSizeView(size)).setChecked(true);
    }

    public void setEnabledSizes(Set<SizeEnum> enabledSizes) {
        for (SizeEnum sizeEnum : ALL_SIZES) {
            getSizeView(sizeEnum).setVisibility(enabledSizes.contains(sizeEnum) ? View.VISIBLE : View.GONE);
        }
    }

    private RadioButton getSizeView(SizeEnum sizeEnum) {
        switch (sizeEnum) {
            case SMALL:
                return mBinding.rbSmallSize;
            case MEDIUM:
                return mBinding.rbMediumSize;
            case LARGE:
                return mBinding.rbLargeSize;
            case EXTRA_LARGE:
                return mBinding.rbExtraLargeSize;
            default:
                return null;
        }
    }
}
