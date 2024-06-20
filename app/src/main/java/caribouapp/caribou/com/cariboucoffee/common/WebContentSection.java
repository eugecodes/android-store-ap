package caribouapp.caribou.com.cariboucoffee.common;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;

/**
 * Created by andressegurola on 11/14/17.
 */

public class WebContentSection extends BaseObservable implements Serializable {
    private String mTitle;
    private String mHtml;

    @Bindable
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getHtml() {
        return mHtml;
    }

    public void setHtml(String html) {
        mHtml = html;
        notifyPropertyChanged(BR.html);
    }
}
