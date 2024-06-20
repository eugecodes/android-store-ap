package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;

/**
 * Created by andressegurola on 12/18/17.
 */

public class EnumSpinnerAdapter<T> extends ArrayAdapter<T> {
    private final String mHint;

    public EnumSpinnerAdapter(@NonNull Context context, String hint, T[] values) {
        super(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, createListOfOptions(values));
        mHint = hint;
    }

    private static <T> List<T> createListOfOptions(T[] values) {
        List<T> listValues = new ArrayList<>();
        listValues.add(null);
        listValues.addAll(Arrays.asList(values));
        return listValues;
    }


    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) LayoutInflater.from(getContext())
                .inflate(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, parent, false);
        if (position == 0) {
            textView.setTextColor(getContext().getResources().getColor(R.color.textSecondaryLightColor));
            textView.setText(mHint);
        } else {
            textView.setTextColor(getContext().getResources().getColor(R.color.textSecondaryColor));
            textView.setText(getItem(position).toString());
        }

        return textView;
    }

}
