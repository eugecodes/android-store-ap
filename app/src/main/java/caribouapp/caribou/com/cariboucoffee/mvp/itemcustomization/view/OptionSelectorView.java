package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutOptionSelectorViewBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OptionSelectorModel;

/**
 * Created by asegurola on 3/14/18.
 */

public class OptionSelectorView extends LinearLayout {

    private OptionSelectorListener mListener;
    private OptionSelectorModel mModel;

    public interface OptionSelectorListener {
        void optionChanged(ItemOption itemOption);
    }

    private LayoutOptionSelectorViewBinding mBinding;


    public OptionSelectorView(Context context) {
        super(context);
        init();
    }

    public OptionSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OptionSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBinding = LayoutOptionSelectorViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    public void setModel(OptionSelectorModel optionSelectorModel) {
        mModel = optionSelectorModel;
        mBinding.setModel(mModel);
        mBinding.rbPrevious.setOnClickListener(v -> {
            mModel.selectPrevious();
            fireQuantityChange();
        });
        mBinding.rbNext.setOnClickListener(v -> {
            mModel.selectNext();
            fireQuantityChange();
        });
        updateSelection();
    }

    public void setOptionListener(OptionSelectorListener listener) {
        mListener = listener;
    }

    private void fireQuantityChange() {
        updateSelection();
        if (mListener != null) {
            mListener.optionChanged(mModel.getItemOptionSelected());
        }
    }

    private void updateSelection() {
        if (mModel.getItemOptionSelected() == null) {
            mModel.selectIndex(0);
        }
        mBinding.tvOption.setText(mModel.getItemOptionSelected().getLabel());
        mBinding.tvOption.setTypeface(ResourcesCompat.getFont(getContext(),
                mModel.isDefaultSelected() ? R.font.description_font_family : R.font.primary_font_family));
    }


    public void reset() {
        mModel.reset();
        updateSelection();
    }
}
