package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class OptionItemView<T extends OptionItem> extends FrameLayout {

    private OptionChooserItemListener mListener;

    public OptionItemView(@NonNull Context context) {
        super(context);
    }

    public OptionItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OptionItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void deselect();

    public OptionChooserItemListener getListener() {
        return mListener;
    }

    public void setListener(OptionChooserItemListener listener) {
        mListener = listener;
    }

    public abstract void selectIfValueMatches(T selectedOption);

    public interface OptionChooserItemListener<T> {
        void optionPressed(T selectedOption);
    }
}
