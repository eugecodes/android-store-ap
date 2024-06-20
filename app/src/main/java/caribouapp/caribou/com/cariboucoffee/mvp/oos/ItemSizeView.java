package caribouapp.caribou.com.cariboucoffee.mvp.oos;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutItemSizeViewBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;

/**
 * Created by asegurola on 5/7/18.
 */

public class ItemSizeView extends FrameLayout {
    private LayoutItemSizeViewBinding mBinding;
    private SizeEnum mSize;

    public ItemSizeView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ItemSizeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSizeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mBinding = LayoutItemSizeViewBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public void setSize(SizeEnum size) {
        if (size == null || size.equals(SizeEnum.ONE_SIZE)) {
            mBinding.tvCartItemSize.setVisibility(GONE);
            return;
        }
        mSize = size;
        mBinding.setSize(size);
    }

    public SizeEnum getSize() {
        return mSize;
    }
}
