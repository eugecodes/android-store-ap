package caribouapp.caribou.com.cariboucoffee.mvp.oos;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutCartItemsViewBinding;

/**
 * Created by gonzalogelos on 3/21/18.
 */

public class CartIconItemsView extends RelativeLayout {

    private LayoutCartItemsViewBinding mBinding;


    public CartIconItemsView(Context context) {
        super(context);
        init();
    }

    public CartIconItemsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CartIconItemsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBinding = LayoutCartItemsViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    public void setItemCount(Integer itemCount) {
        mBinding.setModel(itemCount);
        setContentDescription(itemCount);
    }

    private void setContentDescription(Integer itemCount) {
        if (itemCount != null && itemCount > 0) {
            setContentDescription(getResources()
                    .getQuantityString(R.plurals.my_cart_cd, itemCount, itemCount));
        } else {
            setContentDescription(getContext().getString(R.string.my_cart_no_items_cd));
        }
    }
}
