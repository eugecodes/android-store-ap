package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.common.SearchFieldView;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;

/**
 * Created by jmsmuy on 30/03/18.
 */

public class LocationSearchBarView extends SearchFieldView {
    public LocationSearchBarView(Context context) {
        super(context);
    }

    public LocationSearchBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LocationSearchBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setup() {
        final EditText edtSearchField = getBinding().etSearchText;
        getBinding().etSearchText.setHint(R.string.location_search_hint);
        getBinding().etSearchText.setContentDescription(getContext().getString(R.string.location_search_cc));
        getBinding().ivSearch.setClickable(true);
        getBinding().ivSearch.setContentDescription(getResources().getString(R.string.search_icon_content_description));
        getBinding().ivClearSearchTerms.setOnClickListener(v -> {
            edtSearchField.setText(null);
            edtSearchField.requestFocus();
            UIUtil.showKeyboard(edtSearchField, (android.app.Activity) getContext());
            getBinding().ivClearSearchTerms.setVisibility(View.GONE);
        });
    }
}
