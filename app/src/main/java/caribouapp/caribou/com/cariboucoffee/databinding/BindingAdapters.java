package caribouapp.caribou.com.cariboucoffee.databinding;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.model.LatLng;
import com.redmadrobot.inputmask.MaskedTextChangedListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.EndingDateFormatter;
import caribouapp.caribou.com.cariboucoffee.common.ScaleTypeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationScheduleModel;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.CartIconItemsView;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.RewardAddedBanner;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.view.TriviaAnswersView;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.view.SourceWebActivity;
import caribouapp.caribou.com.cariboucoffee.order.PreSelectedReward;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.DeeplinkUtil;
import caribouapp.caribou.com.cariboucoffee.util.IntentUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursCheckUtil;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by andressegurola on 9/25/17.
 */

public final class BindingAdapters {

    private static final String TAG = BindingAdapters.class.getSimpleName();
    private static final int EXPANDABLE_TEXT_SHORT_LENGTH = 90;
    private static final String DOT = " . ";

    private BindingAdapters() {
    }

    @BindingAdapter("bindAppVersionName")
    public static void setAppVersionName(TextView textView, boolean loadAppVersion) {
        if (!loadAppVersion) {
            return;
        }
        textView.setText(AppUtils.getAppVersionText());
    }

    @BindingAdapter("bindDistanceInMiles")
    public static void setDistanceInMiles(TextView textView, Double distanceInMiles) {
        if (distanceInMiles == null) {
            textView.setText(null);
            return;
        }
        textView.setText(textView.getContext().getString(R.string.distance_in_miles, distanceInMiles));
        textView.setContentDescription(textView.getContext().getString(R.string.distance_in_miles_away, distanceInMiles));
    }

    @BindingAdapter("bindShowDirections")
    public static void setShowDirectionsTo(final View view, final double[] coordinates) {
        view.setOnClickListener(v -> IntentUtil.showDirectionsTo(view.getContext(), new LatLng(coordinates[0], coordinates[1])));
    }

    @BindingAdapter(value = {"bindCall", "bindCallTemplateCD"}, requireAll = false)
    public static void setCall(final TextView textView, final String phoneNumber, String callTemplateCD) {
        textView.setOnClickListener(v -> IntentUtil.callPhoneNumber(textView.getContext(), phoneNumber));
        if (TextUtils.isEmpty(callTemplateCD)) {
            textView.setContentDescription(textView.getContext().getString(R.string.call_cd, phoneNumber));
        } else {
            String callContentDescription = String.format(callTemplateCD, phoneNumber);
            textView.setContentDescription(callContentDescription);
        }
    }

    @BindingAdapter(value = {"bindOnlyCall"}, requireAll = false)
    public static void setOnlyCall(final TextView textView, final String phoneNumber, String callTemplateCD) {
        textView.setOnClickListener(v -> IntentUtil.callPhoneNumber(textView.getContext(), phoneNumber));
    }


    @BindingAdapter("bindPercentageWidth")
    public static void setWidthPercentageOfScreen(View view, float percentage) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        view.getLayoutParams().width = Math.round(width * percentage);
        view.requestLayout();
    }

    @BindingAdapter("bindOpenStatus")
    public static void setOpenStatus(TextView textView, StoreLocation storeLocation) {
        if (storeLocation == null) {
            textView.setText("");
            return;
        }
        ClockWrapper clockWrapper = new ClockWrapper();
        SourceApplication.get(textView.getContext()).getComponent().inject(clockWrapper);
        StoreHoursCheckUtil.OpenStatus openStatus =
                StoreHoursCheckUtil.getStoreOpenStatus(clockWrapper.getClock(), storeLocation);
        if (openStatus == null) {
            textView.setText("");
            return;
        }

        String openStatusText;
        @ColorRes
        int textColorRes;

        DateTimeFormatter formatter = DateTimeFormat.forPattern("h:mm a");
        Context context = textView.getContext();
        if (openStatus.isOpen() && openStatus.getNextTime() != null) {
            openStatusText = context.getString(R.string.store_open_status_with_time, formatter.print(openStatus.getNextTime()));
            textColorRes = R.color.openHours;
        } else if (!openStatus.isOpen() && openStatus.getNextTime() != null) {
            openStatusText = context.getString(R.string.store_closed_status_with_time, formatter.print(openStatus.getNextTime()));
            textColorRes = R.color.closedHours;
        } else {
            openStatusText = context.getString(R.string.store_closed_status);
            textColorRes = R.color.closedHours;
        }
        textView.setText(openStatusText);
        textView.setTextColor(context.getResources().getColor(textColorRes));
    }

    @BindingAdapter("bindDayOfWeekName")
    public static void setWeekDayName(TextView textView, int weekDay) {
        textView.setText(getWeekDayName(textView.getContext(), weekDay));
    }

    public static String getWeekDayName(Context context, int weekDay) {

        @StringRes
        int textRes;

        switch (weekDay) {
            case DateTimeConstants.MONDAY:
                textRes = R.string.weekday_monday;
                break;
            case DateTimeConstants.TUESDAY:
                textRes = R.string.weekday_tuesday;
                break;
            case DateTimeConstants.WEDNESDAY:
                textRes = R.string.weekday_wednesday;
                break;
            case DateTimeConstants.THURSDAY:
                textRes = R.string.weekday_thursday;
                break;
            case DateTimeConstants.FRIDAY:
                textRes = R.string.weekday_friday;
                break;
            case DateTimeConstants.SATURDAY:
                textRes = R.string.weekday_saturday;
                break;
            case DateTimeConstants.SUNDAY:
                textRes = R.string.weekday_sunday;
                break;
            case LocationScheduleModel.WEEK_DAY_HOLIDAYS:
                textRes = R.string.holidays;
                break;
            default:
                textRes = -1;
        }
        return context.getString(textRes);
    }

    @BindingAdapter(value = {"bindDayOfWeekStoreOpenHours"})
    public static void setTimeRange(TextView textView, LocationScheduleModel model) {
        textView.setText(buildTimeRange(textView.getContext(), model));
    }

    @BindingAdapter(value = {"bindDayOfWeekStoreOpenHoursCD"})
    public static void setDayOfWeekStoreOpenHoursContentDescription(View view, LocationScheduleModel model) {
        Context context = view.getContext();
        view.setContentDescription(
                context.getString(R.string.concat_cd, getWeekDayName(context, model.getWeekDay()), buildTimeRange(context, model)));
    }

    public static String buildTimeRange(Context context, LocationScheduleModel model) {

        if (model.getWeekDay() == LocationScheduleModel.WEEK_DAY_HOLIDAYS) {
            return context.getString(R.string.holiday_hours_may_vary);
        }

        if (model.getOpens() == null) {
            return context.getString(R.string.store_closed);
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern("h:mm a");
        return StringUtils.format("%s - %s", formatter.print(model.getOpens()), formatter.print(model.getCloses()));
    }


    @BindingAdapter("bindAdditionalCharges")
    public static void setAdditionalCharge(final TextView view, final ModifierGroup modifierGroup) {
        if (modifierGroup.isAdditionalCharges()) {
            view.setVisibility(View.VISIBLE);
            return;
        }
        for (ItemModifier itemModifier : modifierGroup.getItemModifiers()) {
            for (ItemOption itemOption : itemModifier.getOptions()) {
                if (itemOption.isAdditionalCharges()) {
                    view.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }
        view.setVisibility(View.GONE);
    }

    @BindingAdapter(value = {"bindContentDescription"})
    public static void bindContentDescription(final ImageView view, final String contentDescription) {
        if (TextUtils.isEmpty(contentDescription)) {
            view.setContentDescription(null);
        } else {
            view.setContentDescription(view.getContext().getString(R.string.image_cd, contentDescription));
        }
    }

    @BindingAdapter(value = {"bindImageUrl", "bindImageAltUrl", "bindImageScaleType", "bindBlurImage"}, requireAll = false)
    public static void setImageUrl(final ImageView view, final String url, final String altUrl,
                                   final ScaleTypeEnum imageScaleTypeEnum, final Boolean blur) {
        if (url == null && altUrl != null) {
            setImageUrl(view, altUrl, null, null, blur);
            return;
        } else if (url == null) {
            view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            view.setImageResource(R.drawable.ic_broken_image_grey_400_24dp);
            return;
        }

        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        if (imageScaleTypeEnum == null || ScaleTypeEnum.FIT_CENTER == imageScaleTypeEnum) {
            requestOptions = requestOptions.fitCenter();
        } else if (ScaleTypeEnum.CENTER_CROP == imageScaleTypeEnum) {
            requestOptions = requestOptions.centerCrop();
        }
        if (blur != null && blur) {
            requestOptions = requestOptions.transform(new BlurTransformation(25, 3));
        } else {
            requestOptions = requestOptions
                    .placeholder(R.drawable.ic_insert_photo_grey_400_24dp)
                    .error(R.drawable.ic_broken_image_grey_400_24dp);
        }

        Handler handler = new Handler();

        Glide.with(view.getContext())
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "Error loading image: " + url, e);
                        if (altUrl == null) {
                            return false;
                        }

                        handler.post(() -> {
                            // Try to load alternative image as primary one
                            setImageUrl(view, altUrl, null, null, blur);
                        });

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        Log.d(TAG, "Loaded image from URL: " + url);

                        if (ScaleTypeEnum.FIT_XY == imageScaleTypeEnum) {
                            ImageViewTarget viewTarget = (ImageViewTarget) target;
                            ImageView imageView = (ImageView) viewTarget.getView();
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        return false;
                    }
                }).apply(requestOptions)
                .into(view);
    }

    @BindingAdapter("textStyle")
    public static void setTextStyle(TextView textView, int typeFaceStyle) {
        textView.setTypeface(null, typeFaceStyle);
    }

    @BindingAdapter(value = {"bindEndingDate", "bindFormatString", "bindShowHours", "bindHoursFormatString"}, requireAll = false)
    public static void setFriendlyEndingDate(TextView view, DateTime endingDate, String stringFormat, Boolean showHours, String hoursFormatString) {
        view.setText(buildFriendlyEndingDate(view.getContext(), endingDate, stringFormat, showHours, hoursFormatString));
    }

    @BindingAdapter(value = {
            "bindEndingDateCD",
            "bindFormatStringCD",
            "bindShowHoursCD",
            "bindHoursFormatStringCD",
            "bindLimitedTime",
            "bindLimitedTimeCD",
            "bindRedeemedCD",
            "bindPointsCD",
            "bindRewardCD",
            "bindPrefixCD"}, requireAll = false)
    public static void setFriendlyEndingDateCD(View view,
                                               DateTime endingDate,
                                               String stringFormat,
                                               Boolean showHours,
                                               String hoursFormatString,
                                               boolean bindLimitedTime,
                                               String bindLimitedTimeCD,
                                               boolean redeemedCD,
                                               BigDecimal pointsCD,
                                               String rewardCD,
                                               String prefixCD) {
        String contentDescription = prefixCD != null ? prefixCD : " ";
        if (!redeemedCD) {
            contentDescription += DOT + (pointsCD == null ? "" : pointsCD.intValue() + view.getContext().getString(R.string.points));
        } else {
            contentDescription += DOT + buildFriendlyEndingDate(view.getContext(), endingDate, stringFormat, showHours, hoursFormatString);
        }
        contentDescription += DOT + rewardCD;
        if (bindLimitedTime && !TextUtils.isEmpty(bindLimitedTimeCD)) {
            contentDescription += DOT + bindLimitedTimeCD;
        }
        view.setContentDescription(contentDescription);
    }

    /**
     * @param context           an Android Context
     * @param endingDate        perk's ending date
     * @param stringFormat      date string format
     * @param showHours         T/F show hours
     * @param hoursFormatString hour string format.
     * @return a String according to the current date, and the {@param endingDate} of the perk
     * If the date is today, we return a constant TODAY
     * If the date is within the week but not today, we return the day name
     * Else we return Month, DayNumber (Shortened Month -> Jan, Feb, etc.) and we add the year if
     * there are more than 365 days between dates
     */
    private static String buildFriendlyEndingDate(Context context, DateTime endingDate,
                                                  String stringFormat, Boolean showHours, String hoursFormatString) {
        if (endingDate == null) {
            return null;
        }

        InjectionHelper injectionHelper = new InjectionHelper(context);

        if (showHours == null || hoursFormatString == null) {
            showHours = false;
        }

        EndingDateFormatter endingDateFormatter = new EndingDateFormatter(injectionHelper.getClock(), stringFormat, hoursFormatString, showHours);
        return endingDateFormatter.format(context, endingDate);
    }

    @BindingAdapter(value = {"bindDate", "bindDateFormat"}, requireAll = false)
    public static void setDate(EditText view, LocalDate date, String dateFormat) {
        if (date == null) {
            return;
        }
        view.setText((DateTimeFormat.forPattern(dateFormat == null ? "MM-dd-YYYY" : dateFormat)).withLocale(Locale.US).print(date));
    }

    @BindingAdapter(value = {"bindDate", "bindDateFormat"}, requireAll = false)
    public static void setDate(TextView view, LocalDate date, String dateFormat) {
        if (date == null) {
            return;
        }
        view.setText((DateTimeFormat.forPattern(dateFormat == null ? "MM-dd-YYYY" : dateFormat))
                .withLocale(Locale.US).print(date));

    }

    @BindingAdapter(value = {"bindDateTime", "bindDateFormat"}, requireAll = false)
    public static void setDate(TextView view, DateTime dateTime, String dateFormat) {
        if (dateTime == null) {
            return;
        }
        view.setText((DateTimeFormat.forPattern(dateFormat == null ? "MM-dd-YYYY" : dateFormat))
                .withLocale(Locale.US).print(dateTime));
    }

    @BindingAdapter("loadHtml")
    public static void setHtml(WebView webView, String html) {
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    /**
     * This is needed for check in, date is displayed "Last update MM/d at HH:mm"
     *
     * @param view
     * @param dateTime
     */
    @BindingAdapter("bindFormatLastUpdated")
    public static void setLastUpdated(TextView view, DateTime dateTime) {
        if (dateTime != null) {
            view.setText(StringUtils.format(view.getContext().getString(R.string.last_updated_check_in),
                    DateTimeFormat.forPattern("MM/d").print(dateTime),
                    DateTimeFormat.forPattern("hh:mm aa").print(dateTime)));
        } else {
            view.setText(R.string.out_of_date);
        }
    }

    @BindingAdapter(value = {"bindMoney", "bindMoneyDigits", "cdTemplate"}, requireAll = false)
    public static void setMoneyAmount(TextView view, BigDecimal balance, Integer digitsAfterComma, @StringRes int cdTemplateID) {
        String moneyAmount = StringUtils.formatMoneyAmount(view.getContext(), balance, digitsAfterComma);
        view.setText(moneyAmount);
        if (cdTemplateID != 0) {
            String moneyContentDescription = view.getContext().getString(cdTemplateID, moneyAmount);
            view.setContentDescription(moneyContentDescription);
        } else {
            view.setContentDescription(moneyAmount);
        }
    }

    @BindingAdapter(value = {"bindMoneyCD", "bindMoneyDigitsCD", "bindTemplateCD",
            "bindPrefixValueCD", "bindPrefixTemplateCD"}, requireAll = false)
    public static void setMoneyContentDescription(View view, BigDecimal moneyCD,
                                                  Integer moneyDigitsCD, String templateCD,
                                                  String prefixValueCD, String prefixTemplateCD) {
        String amountCD = StringUtils.formatMoneyAmount(view.getContext(), moneyCD, moneyDigitsCD);
        if (TextUtils.isEmpty(templateCD)) {
            view.setContentDescription(amountCD);
        } else {
            String moneyContentDescription = String.format(templateCD, amountCD);
            if (!TextUtils.isEmpty(prefixTemplateCD) && prefixValueCD != null) {
                String moneyCDPrefix = String.format(prefixTemplateCD, prefixValueCD);
                moneyContentDescription = moneyCDPrefix + moneyContentDescription;
            }
            view.setContentDescription(moneyContentDescription);
        }
    }

    @BindingAdapter(value = {"bindPointsCD", "bindPointsDigitsCD", "bindPointsTemplateCD"}, requireAll = false)
    public static void setPointsContentDescription(View view, BigDecimal pointsCD, Integer pointsDigitsCD, String pointsTemplateCD) {
        String amountCD = StringUtils.formatPointsAmount(view.getContext(), pointsCD, pointsDigitsCD);
        if (TextUtils.isEmpty(pointsTemplateCD)) {
            view.setContentDescription(amountCD);
        } else {
            String moneyContentDescription = String.format(pointsTemplateCD, amountCD);
            view.setContentDescription(moneyContentDescription);
        }
    }

    @BindingAdapter(
            value = {"bindSourceWebTitle", "bindSourceWebUrl", "bindSourceWebSendToken"},
            requireAll = false)
    public static void openSourceWebUrl(View view, String title, String webUrl, boolean sendToken) {
        if (TextUtils.isEmpty(webUrl)) {
            view.setOnClickListener(null);
            return;
        }
        view.setOnClickListener(v -> {
            Context context = v.getContext();
            Activity activity = UIUtil.getActivity(context);
            activity.startActivityForResult(
                    SourceWebActivity.createIntentFromUrl(
                            activity,
                            title,
                            webUrl,
                            DeeplinkUtil.buildSourceAppFinishActivity(context),
                            sendToken, false),
                    AppConstants.REQUEST_CODE_CARIBOU_WEBSITE);
        });
    }

    @BindingAdapter("bindVisible")
    public static void setVisibleOrGone(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("bindVisible")
    public static void setVisibleOrGone(ViewGroup view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("bindInvisible")
    public static void setInvisibleOrVisible(View view, boolean invisible) {
        view.setVisibility(invisible ? View.INVISIBLE : View.VISIBLE);
    }

    @BindingAdapter("bindInvisible")
    public static void setInvisibleOrVisible(ViewGroup view, boolean invisible) {
        view.setVisibility(invisible ? View.INVISIBLE : View.VISIBLE);
    }


    @BindingAdapter(value = {"bindPoints", "bindPointsAddText"}, requireAll = false)
    public static void setPoints(TextView textView, BigDecimal points, Boolean showPointsTextSuffix) {
        textView.setText(points == null ? "" : points.intValue()
                + (showPointsTextSuffix != null && showPointsTextSuffix ? " " + textView.getContext().getString(R.string.points) : ""));
    }

    @BindingAdapter(value = {"bindSmallFontSize", "bindLargeFontSize", "bindText"})
    public static void setSmallLargeFontSize(TextView textView, float smallFontSize, float largeFontSize, String text) {
        if (text.length() > 20) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallFontSize);
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, largeFontSize);
        }
        textView.setText(text);
    }

    /**
     * Expands long text when clicking on the textView.
     *
     * @param textView
     * @param text
     */
    @BindingAdapter("expandableText")
    public static void setText(TextView textView, String text) {
        String htmlText;
        Context context = textView.getContext();
        boolean needShortening = text.length() > EXPANDABLE_TEXT_SHORT_LENGTH;

        if (!needShortening) {
            textView.setText(text);
            return;
        }

        boolean expanded = textView.getTag(R.id.expanded) == null || ((Boolean) textView.getTag(R.id.expanded));

        if (expanded) {
            htmlText = text.substring(0, EXPANDABLE_TEXT_SHORT_LENGTH)
                    + StringUtils.format(context.getString(R.string.expand_short_text_suffix),
                    context.getResources().getColor(R.color.expand_collapse_text_button));
        } else {
            htmlText = text
                    + StringUtils.format(context.getString(R.string.collapse_short_text_suffix),
                    context.getResources().getColor(R.color.expand_collapse_text_button));
        }
        textView.setText(Html.fromHtml(htmlText), TextView.BufferType.SPANNABLE);
        textView.setContentDescription(text);
        textView.setOnClickListener(v -> {
            textView.setTag(R.id.expanded, !expanded);
            setText(textView, text);
        });
    }

    @BindingAdapter("bindItemSize")
    public static void bindItemSize(TextView textView, SizeEnum itemSize) {
        if (itemSize == null) {
            textView.setVisibility(View.GONE);
            return;
        }

        Context context = textView.getContext();

        switch (itemSize) {
            case SMALL:
                textView.setText(context.getString(R.string.size_small));
                break;
            case MEDIUM:
                textView.setText(context.getString(R.string.size_medium));
                break;
            case LARGE:
                textView.setText(context.getString(R.string.size_large));
                break;
            case EXTRA_LARGE:
                textView.setText(context.getString(R.string.size_extra_large));
                break;
            default:
                textView.setVisibility(View.GONE);
                return;
        }
        textView.setVisibility(View.VISIBLE);

    }

    @BindingAdapter("bindProductName")
    public static void bindProductName(TextView textView, RecentOrderItem orderItem) {
        textView.setText(orderItem.getProductName());
        if (orderItem.getQuantity().compareTo(BigDecimal.ONE) > 0) {
            textView.setText(textView.getContext().getString(R.string.items_quantity_with_name,
                    orderItem.getQuantity().intValue(), orderItem.getProductName()));
        }
    }

    @BindingAdapter("bindProductCustomizationStrings")
    public static void bindProductCustomization(TextView textView, List<String> itemCustomization) {
        textView.setVisibility(itemCustomization.isEmpty() ? View.GONE : View.VISIBLE);
        textView.setText(buildProductCustomization(textView.getContext(), itemCustomization));
    }

    public static String buildProductCustomization(Context context, List<String> itemCustomization) {
        return StringUtils.joinWith(StreamSupport.stream(itemCustomization).map(customization ->
                customization == null ? context.getString(R.string.customization_none) : customization
        ).collect(Collectors.toList()), ", ");
    }

    @BindingAdapter("bindItemCount")
    public static void bindItemCount(CartIconItemsView cartIconItemsView, Integer integer) {
        cartIconItemsView.setItemCount(integer);
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView imageView, int resourceId) {
        imageView.setImageResource(resourceId);
    }

    @BindingAdapter(value = {"filterColor", "mode"}, requireAll = false)
    public static void setColorFilter(ImageView imageView, Integer color, PorterDuff.Mode mode) {
        if (mode == null) {
            mode = PorterDuff.Mode.SRC_ATOP;
        }

        imageView.setColorFilter(color, mode);
    }

    @BindingAdapter(value = {"drawableTint", "drawableStart", "drawableTintMode", "drawablePadding"}, requireAll = false)
    public static void setDrawableStart(TextView textView, int tintColor, Drawable drawableStart, PorterDuff.Mode tintMode, float padding) {

        textView.setCompoundDrawablesWithIntrinsicBounds(
                tintDrawable(drawableStart, tintColor, tintMode),  // Left
                null, // Top
                null, // Right
                null); //Bottom

        textView.setCompoundDrawablePadding((int) padding);
    }

    @BindingAdapter("tintBackgroundDrawable")
    public static void setBackgroundDrawable(View view, int tintColor) {
        view.setBackground(tintDrawable(view.getBackground(), tintColor, null));
    }

    @BindingAdapter("tintButtonDrawable")
    public static void setDrawable(Button button, int tintColor) {
        setBackgroundDrawable(button, tintColor);
    }

    @BindingAdapter("tintColorDrawable")
    public static void setDrawable(RadioButton radioButton, int tintColor) {
        radioButton.setButtonDrawable(tintDrawable(radioButton.getBackground(), tintColor, null));
    }

    @BindingAdapter("tintStart")
    public static void setTintStart(TextView textView, int tintColor) {
        Drawable[] drawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(
                tintDrawable(drawables[0], tintColor, null),  // Left
                drawables[1], // Top
                drawables[2], // Right
                drawables[3]); //Bottom

    }

    @BindingAdapter("tintProgressbar")
    public static void setProgressbarTintColor(ProgressBar progressBar, int tintColor) {
        progressBar.getIndeterminateDrawable().setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
    }

    public static Drawable tintDrawable(Drawable drawable, int tint, PorterDuff.Mode tintMode) {
        if (drawable == null) {
            return null;
        }
        if (tintMode == null) {
            tintMode = PorterDuff.Mode.SRC_ATOP;
        }
        drawable = DrawableCompat.wrap(drawable);
        //https://medium.com/@hanru.yeh/tips-for-drawablecompat-settint-under-api-21-1e62a32fc033
        //Reference for mutable drawable
        Drawable mutableDrawable = drawable.mutate();
        DrawableCompat.setTint(mutableDrawable, tint);
        DrawableCompat.setTintMode(mutableDrawable, tintMode);

        return drawable;
    }

    @BindingAdapter("bindEditable")
    public static void bindEditable(EditText editText, boolean editable) {
        // Inspired in https://stackoverflow.com/questions/3928711/how-to-make-edittext-not-editable-through-xml-in-android
        editText.setClickable(editable);
        editText.setCursorVisible(editable);
        editText.setFocusable(editable);
        editText.setFocusableInTouchMode(editable);
    }

    @BindingAdapter("bindPhoneNumber")
    public static void bindPhoneNumberField(EditText editText, boolean phoneNumber) {
        // This library masks editTexts according to expression, in this case it will only accept US telephone numbers
        MaskedTextChangedListener.Companion
                .installOn(editText, "[000]-[000]-[0000]", null);
    }

    @BindingAdapter("bindPhoneNumber2")
    public static void bindPhoneNumberField2(EditText editText, boolean phoneNumber) {
        // This library masks editTexts according to expression, in this case it will only accept US telephone numbers
        MaskedTextChangedListener.Companion
                .installOn(editText, "([000]) [000]-[0000]", null);
    }

    @BindingAdapter("bindPreSelectedReward")
    public static void bindPreSelectedReward(RewardAddedBanner rewardAddedBanner, PreSelectedReward preSelectedReward) {
        rewardAddedBanner.setReward(preSelectedReward);
    }

    @BindingAdapter("bindTriviaAnswerList")
    public static void bindTriviaAnswerList(TriviaAnswersView triviaAnswersView, List<String> answers) {
        triviaAnswersView.clearAnswers();
        if (answers == null) {
            return;
        }
        for (String answer : answers) {
            triviaAnswersView.addAnswer(answer);
        }
    }

    @BindingAdapter("bindTriviaCorrectAnswer")
    public static void bindTriviaCorrectAnswer(TriviaAnswersView triviaAnswersView, String correctAnswer) {
        triviaAnswersView.setCorrectAnswer(correctAnswer);
    }

    @BindingAdapter("bindPickupType")
    public static void bindPickupType(TextView textView, YextPickupType pickupType) {
        Context context = textView.getContext();
        if (pickupType == YextPickupType.Delivery) {
            textView.setText(context.getString(pickupType.getDisplayNameStringId()));
            return;
        }
        textView.setText(context.getString(R.string.pick_up_type, pickupType == null ? ""
                : context.getString(pickupType.getDisplayNameStringId())));
    }

    @BindingAdapter("bindPickupTypeDescription")
    public static void bindPickupTypeDescription(View view, YextPickupType pickupType) {
        Context context = view.getContext();
        String content = context.getString(R.string.pick_up_type_cd, pickupType == null ? context.getString(R.string.none)
                : context.getString(pickupType.getDisplayNameStringId()));
        view.setContentDescription(content);
    }

    @BindingAdapter("bindStoreLocationCD")
    public static void bindStoreLocationContentDescription(ViewGroup viewGroup, StoreLocation storeLocation) {
        ClockWrapper clockWrapper = new ClockWrapper();
        SourceApplication.get(viewGroup.getContext()).getComponent().inject(clockWrapper);
        viewGroup.setContentDescription(getStoreContentDescription(storeLocation, viewGroup.getContext(), clockWrapper.getClock()));
    }

    public static String getStoreContentDescription(StoreLocation storeLocation, Context context, Clock clock) {
        StringBuilder storeDataAsString = new StringBuilder();
        storeDataAsString.append(storeLocation.getName()).append(", ");
        storeDataAsString.append(storeLocation.getAddress()).append(", ");
        Double distanceInMiles = storeLocation.getDistanceInMiles();
        if (distanceInMiles != null) {
            storeDataAsString.append(context.getString(R.string.distance_in_miles_away, storeLocation.getDistanceInMiles())).append(", ");
        }
        storeDataAsString.append(StoreHoursCheckUtil.getStoreOpenStatusString(context, storeLocation, clock)).append(", ");
        return storeDataAsString.toString();
    }

    public static class InjectionHelper {
        @Inject
        Clock mClock;

        public InjectionHelper(Context context) {
            SourceApplication.get(context).getComponent().inject(this);
        }

        public Clock getClock() {
            return mClock;
        }
    }

    public static class ClockWrapper {
        @Inject
        Clock mClock;

        public Clock getClock() {
            return mClock;
        }

        public void setClock(Clock clock) {
            mClock = clock;
        }
    }
}
