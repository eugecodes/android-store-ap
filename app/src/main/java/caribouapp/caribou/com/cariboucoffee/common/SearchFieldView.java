package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutSearchFieldBinding;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;

/**
 * Created by andressegurola on 10/17/17.
 */

public abstract class SearchFieldView extends FrameLayout {

    private LayoutSearchFieldBinding mBinding;

    private SearchFieldViewPresenter mPresenter;

    public SearchFieldView(Context context) {
        super(context);
        init(null);
    }

    public SearchFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SearchFieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        mBinding = LayoutSearchFieldBinding.inflate(LayoutInflater.from(getContext()), this, true);

        final EditText edtSearchField = mBinding.etSearchText;

        edtSearchField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                fireSearch();
                return true;
            }
            return false;
        });
        edtSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBinding.ivClearSearchTerms.setVisibility(s.toString().isEmpty() ? View.GONE : View.VISIBLE);
                mBinding.ivSearch.setVisibility(s.toString().isEmpty() ? View.VISIBLE : View.GONE);
                mPresenter.searchTextEmpty(s.toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.ivFilter.setOnClickListener(v -> mPresenter.showFilterDialog());

        setup();
    }

    protected abstract void setup();

    public void setPresenter(SearchFieldViewPresenter presenter) {
        mPresenter = presenter;
    }

    private void fireSearch() {
        UIUtil.hideKeyboard((android.app.Activity) getContext());
        mPresenter.search(mBinding.etSearchText.getText().toString());
    }

    public void setText(String text) {
        EditText editText = mBinding.etSearchText;
        editText.setText(text);
        editText.setSelection(editText.getText().length());
    }

    public LayoutSearchFieldBinding getBinding() {
        return mBinding;
    }

    public SearchFieldViewPresenter getPresenter() {
        return mPresenter;
    }
}
