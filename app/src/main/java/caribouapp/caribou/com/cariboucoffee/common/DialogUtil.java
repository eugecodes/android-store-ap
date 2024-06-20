package caribouapp.caribou.com.cariboucoffee.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import caribouapp.caribou.com.cariboucoffee.R;

/**
 * Created by jmsmuy on 5/3/18.
 */

public final class DialogUtil {

    private DialogUtil() {
    }

    public static void showDismissableDialog(Activity activity, @StringRes int titleId, @StringRes int messageId) {
        new AlertDialog.Builder(activity)
                .setTitle(titleId)
                .setMessage(messageId)
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss()).create().show();
    }

    public static void showDismissableDialog(Activity activity, @StringRes int titleId, String message) {
        new AlertDialog.Builder(activity)
                .setTitle(titleId)
                .setMessage(message)
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss()).create().show();
    }

    public static void showStoreClosedAlert(Activity activity, Runnable runnable) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.store_closed_alert_title)
                .setMessage(activity.getString(R.string.store_closed_alert_text))
                .setPositiveButton(R.string.okay, (dialog, which) -> runnable.run()).create().show();
    }

    public static void showStoreAlmostClosedAlert(Activity activity, Runnable runnable) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.store_almost_closed_alert_title)
                .setMessage(activity.getString(R.string.store_almost_closed_alert_text))
                .setPositiveButton(R.string.okay, (dialog, which) -> runnable.run()).create().show();
    }

    public static void showStoreNotOrderOutOfStoreAlert(Activity activity, Runnable runnable) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.sorry)
                .setMessage(activity.getString(R.string.store_not_oos))
                .setPositiveButton(R.string.got_it, (dialog, which) -> runnable.run()).create().show();
    }

    public static void storeNotAvailableDialog(Activity activity) {
        new android.app.AlertDialog.Builder(activity)
                .setTitle(R.string.sorry)
                .setMessage(R.string.store_not_taking_orders)
                .setPositiveButton(R.string.okay, (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    public static void storeTemporaryOff(Activity activity) {
        new android.app.AlertDialog.Builder(activity)
                .setTitle(R.string.sorry)
                .setMessage(activity.getString(R.string.sorry_store_current_out_of_order_ahead))
                .setNeutralButton(R.string.okay, (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    public static void showStartingBulkOrderDialog(Activity activity,
                                                   DialogInterface.OnClickListener negativeButtonClick,
                                                   DialogInterface.OnClickListener positiveButtonClick) {
        new android.app.AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.add_bulk_item_dialog_message))
                .setNegativeButton(R.string.nevermind, negativeButtonClick)
                .setPositiveButton(R.string.works_for_me, positiveButtonClick).create().show();
    }

    public static void showStoreNearClosingForBulk(Activity activity) {
        new android.app.AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.bulk_order_not_enough_time))
                .setPositiveButton(R.string.okay, (dialog, which) -> dialog.dismiss()).create().show();
    }

    public static void showCancelOrder(Activity activity, DialogInterface.OnClickListener positiveButtonClick) {
        new android.app.AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.cancelling_order))
                .setNegativeButton(R.string.keep_my_order, null)
                .setPositiveButton(R.string.yes_cancel, positiveButtonClick)
                .show();
    }

    public static void showImHereModal(Activity activity, String message) {
        String msg = TextUtils.isEmpty(message) ? activity.getString(R.string.remember_to_check_in_im_here) : message;
        new android.app.AlertDialog.Builder(activity)
                .setTitle(R.string.curbside_pickup)
                .setMessage(msg)
                .setPositiveButton(R.string.works_for_me, (dialog, which) -> dialog.dismiss()).create().show();
    }

}
