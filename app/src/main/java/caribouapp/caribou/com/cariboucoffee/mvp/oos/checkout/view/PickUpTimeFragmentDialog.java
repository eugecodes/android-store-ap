package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import java.util.ArrayList;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.DialogPickUpTimeBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutPickUpTimeItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.PickUpTimeListener;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel;

/**
 * Created by jmsmuy on 16/07/18.
 */
@FragmentWithArgs
public class PickUpTimeFragmentDialog extends DialogFragment {

    private DialogPickUpTimeBinding mBinding;

    @Arg
    ArrayList<PickUpTimeModel> mPickUpOptions;

    private PickUpTimeListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_pick_up_time)
                .setPositiveButton(R.string.dialog_close,
                        (dialog, which) -> dismiss())
                .setView(createView()).create();
    }

    private View createView() {
        mListener = (PickUpTimeListener) getActivity();
        FragmentArgs.inject(this);
        mBinding = DialogPickUpTimeBinding.inflate(LayoutInflater.from(getContext()));
        int pickUpIndex = 0;
        for (final PickUpTimeModel pickUpTime : mPickUpOptions) {
            LayoutPickUpTimeItemBinding binding = LayoutPickUpTimeItemBinding
                    .inflate(LayoutInflater.from(getContext()),
                            mBinding.llPickUpTimes, true);

            String text = pickUpTime.isAsap() ? getString(R.string.asap) : pickUpTime.toString();
            binding.tvPickUpTime.setText(text);
            binding.tvPickUpTime.setContentDescription(getString(R.string.pick_up_time_content_description, String.valueOf(pickUpIndex + 1), text));
            binding.setListener(v -> selectPickUpTime(pickUpTime));
            pickUpIndex++;
        }
        mBinding.executePendingBindings();
        return mBinding.getRoot();
    }

    private void selectPickUpTime(PickUpTimeModel pickUpTime) {
        mListener.applyPickUpTime(pickUpTime);
        dismiss();
    }

}
