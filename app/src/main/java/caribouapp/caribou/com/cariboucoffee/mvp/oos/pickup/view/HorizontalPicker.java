package caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutSingleOptionSelectionBinding;

public class HorizontalPicker extends FrameLayout {

    private LayoutSingleOptionSelectionBinding mBinding;

    private HorizontalPickerAdapter mAdapter;

    public HorizontalPicker(Context context) {
        super(context);
        init();
    }

    public HorizontalPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBinding = LayoutSingleOptionSelectionBinding.inflate(LayoutInflater.from(getContext()), this, true);

        RecyclerView recyclerView = mBinding.rvItemModifier;
        mAdapter = new HorizontalPickerAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setListName(String optionListName) {
        mBinding.setListName(optionListName);
    }

    public void setOptions(List<SingleOption> options) {
        mAdapter.setData(options);
    }

    public void setListener(HorizontalPickerAdapter.SingleOptionListener listener) {
        mAdapter.setListener(listener);
    }

    public void setCurrentSelection(SingleOption selectedOption) {
        mAdapter.setSelected(selectedOption);

        if (selectedOption == null) {
            return;
        }

        if (!mAdapter.getOptions().contains(selectedOption)) {
            throw new IllegalArgumentException("Option " + selectedOption.getName() + " not available in option list.");
        }
        mBinding.rvItemModifier.scrollToPosition(mAdapter.getOptions().indexOf(selectedOption));
    }

    public void setCurrentSelection(int position) {
        mAdapter.setSelected(mAdapter.getOptions().get(0));
        mBinding.rvItemModifier.scrollToPosition(position);
    }
}

