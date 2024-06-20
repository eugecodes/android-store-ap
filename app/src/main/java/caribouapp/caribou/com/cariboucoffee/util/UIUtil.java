package caribouapp.caribou.com.cariboucoffee.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.common.OptionItem;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRunnable;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

/**
 * Created by andressegurola on 10/11/17.
 */

public final class UIUtil {

    public static final String TAG = UIUtil.class.getSimpleName();

    private UIUtil() {
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(activity.findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyboard(EditText editText, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void setupOptionsSpinner(Spinner spinner, String hintText, List<OptionItem> options) {
        List<String> spinnerOptions = new ArrayList<>();

        // Adds default first "empty" option. This will be the one selected by default in the spinner.
        spinnerOptions.add(hintText);

        for (OptionItem option : options) {
            spinnerOptions.add(option.getDescription());
        }

        List<String> optionsContentDescription = new ArrayList();
        optionsContentDescription.add(hintText);
        for (OptionItem optionItem : options) {
            optionsContentDescription.add(optionItem.getContentDescription());
        }


        ArrayAdapter<String> otherOptionsValuesAdapter =
                new ArrayAdapter<String>(spinner.getContext(), R.layout.layout_option_spinner_item, spinnerOptions) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        view.setContentDescription(optionsContentDescription.get(position));
                        view.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                        return view;
                    }
                };
        spinner.setAdapter(otherOptionsValuesAdapter);
    }

    /**
     * Sets all child views as GONE except for the only indicated
     *
     * @param visibleView the only view that should be visible
     */
    public static void setOnlyChildVisible(View visibleView) {
        ViewGroup viewGroup = (ViewGroup) visibleView.getParent();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setVisibility(child.getId() == visibleView.getId() ? View.VISIBLE : View.GONE);
        }
    }

    public static void setPasswordCharVisible(TextInputLayout textInputLayout) {
        FrameLayout frameLayout = (FrameLayout) textInputLayout.getChildAt(0);
        View child = frameLayout.getChildAt(1);
        if (child == null) {
            return;
        }
        child.performClick();
    }

    public static void runWithBaseView(WeakReference<MvpView> baseViewWeakReference, BaseViewRunnable runnable) {
        MvpView baseView = baseViewWeakReference.get();
        if (baseView == null || !baseView.isActive()) {
            return;
        }
        baseView.runOnUiThread(() -> runnable.run(baseView));
    }

    public static void runWithBaseView(MvpView mvpView, BaseViewRunnable runnable) {
        if (mvpView == null || !mvpView.isActive()) {
            return;
        }
        mvpView.runOnUiThread(() -> runnable.run(mvpView));
    }

    public static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static void saveViewScreenshot(View view, String name) {
        saveBitmapToPng(view.getContext(), name, loadBitmapFromView(view, view.getWidth(), view.getHeight()));
    }

    public static Bitmap loadBitmapFromView(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getLayoutParams().width, view.getLayoutParams().height);
        view.draw(canvas);
        return bitmap;
    }

    public static void saveBitmapToPng(Context context, String fileName,
                                       Bitmap mBitmap) {
        try {
            File outputFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath()
                    + "/" + fileName + ".png");
            Log.d(TAG, "Saving view screenshot. To pull use -> adb pull " + outputFile.getAbsolutePath());
            FileOutputStream fOut =
                    new FileOutputStream(outputFile);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
