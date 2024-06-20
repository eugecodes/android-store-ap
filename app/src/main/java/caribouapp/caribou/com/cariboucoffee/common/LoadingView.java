package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.bumptech.glide.Glide;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.ViewLoadScreenBinding;

/**
 * Created by jmsmuy on 10/2/17.
 */

public class LoadingView extends FrameLayout {

    private ViewLoadScreenBinding mBinding;

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBinding = ViewLoadScreenBinding.inflate(inflater, this, true);

        setVisibility(View.GONE);

        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                info.setClickable(false);
                info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_LONG_CLICK);
                info.setLongClickable(false);
                info.addAction(AccessibilityNodeInfoCompat.ACTION_FOCUS);
                info.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(AccessibilityNodeInfoCompat.ACTION_CLICK, " "));
                info.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(AccessibilityNodeInfoCompat.ACTION_LONG_CLICK, " "));
            }
        });

        Glide.with(getContext()).load(R.drawable.loading_animation).into(mBinding.ivLoadingAnimation);
    }

    public void loadLoadingScreen() {
        setVisibility(View.VISIBLE);
    }

    public void showProcessingText() {
        mBinding.llProcessingText.setVisibility(VISIBLE);
        mBinding.background.setAlpha(0.9f);
    }

    public void unloadLoadingScreen() {
        mBinding.llProcessingText.setVisibility(GONE);
        mBinding.background.setAlpha(0.4f);
        setVisibility(View.GONE);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        AccessibilityEvent event = AccessibilityEvent.obtain();
        event.setEventType(AccessibilityEvent.TYPE_ANNOUNCEMENT);
        if (View.GONE == visibility) {
            announceForAccessibility(getContext().getString(R.string.loading_finished_cd));
            clearFocus();
        } else {
            requestFocus();
            announceForAccessibility(getContext().getString(R.string.screen_loading_cd));
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
        info.setClickable(false);
        info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
        info.setLongClickable(false);
        info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_FOCUS);
    }
}
