package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class StateEnumSpinnerAdapter<T extends StateEnum> extends EnumSpinnerAdapter<T> {

    public StateEnumSpinnerAdapter(@NonNull Context context, String hint, T[] values) {
        super(context, hint, values);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return setContentDescription(super.getDropDownView(position, convertView, parent), position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return setContentDescription(super.getView(position, convertView, parent), position);
    }

    private View setContentDescription(View view, int position) {
        StateEnum stateEnum = getItem(position);
        if (stateEnum != null) {
            view.setContentDescription(stateEnum.name());
        }
        return view;
    }
}
