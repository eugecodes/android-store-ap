package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.common.SearchFieldView;

/**
 * Created by jmsmuy on 30/03/18.
 */

public class BasicSearchView extends SearchFieldView {
    public BasicSearchView(Context context) {
        super(context);
    }

    public BasicSearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BasicSearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setup() {
        final EditText edtSearchField = getBinding().etSearchText;
        getBinding().etSearchText.setHint(R.string.menu_search_hint);
        getBinding().etSearchText.setLongClickable(false);
        getBinding().etSearchText.setClickable(true);
        getBinding().ivFilter.setVisibility(GONE);
        getBinding().ivClearSearchTerms.setOnClickListener(v -> {
            edtSearchField.setText(null);
            getPresenter().searchCleared();
            getBinding().ivClearSearchTerms.setVisibility(View.GONE);
        });
        getBinding().ivClearSearchTerms.setClickable(true);
        getBinding().ivClearSearchTerms.setContentDescription(getContext().getString(R.string.clear_search_icon_content_description));
        getBinding().ivSearch.setVisibility(View.VISIBLE);
        getBinding().ivSearch.setClickable(true);
        getBinding().ivSearch.setContentDescription(getResources().getString(R.string.search_icon_content_description));
    }

    public void setHint(@StringRes int hint) {
        getBinding().etSearchText.setHint(hint);
    }

    public void setContentDescription(@StringRes int hint) {
        getBinding().etSearchText.setContentDescription(getContext().getString(hint));
    }
}
