package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import caribouapp.caribou.com.cariboucoffee.databinding.CustomMenuFilterButtonBinding;

/**
 * Created by jmsmuy on 10/6/17.
 */

public final class MenuTypeButtonBuilder {

    private MenuTypeButtonBuilder() {
    }

    public static TextView createToggleButton(Context context, LinearLayout parent) {
        return (TextView) CustomMenuFilterButtonBinding.inflate(LayoutInflater.from(context), parent, false).getRoot();
    }

}
