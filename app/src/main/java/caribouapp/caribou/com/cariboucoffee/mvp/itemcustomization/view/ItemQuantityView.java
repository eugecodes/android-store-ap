package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.R;

/**
 * Created by asegurola on 3/23/18.
 */

public class ItemQuantityView extends QuantitySelectorView {
    public ItemQuantityView(Context context) {
        super(context);
    }

    public ItemQuantityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemQuantityView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void updateQuantity(int quantity) {
        final String quantityText = getContext().getString(R.string.quantity_selector_text, quantity);
        getBinding().tvQuantity.setText(quantityText);
        getBinding().tvQuantity.setContentDescription(quantityText);
        getBinding().tvQuantity.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
    }
}
