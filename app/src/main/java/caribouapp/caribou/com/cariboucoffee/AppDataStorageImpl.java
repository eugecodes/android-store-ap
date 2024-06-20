package caribouapp.caribou.com.cariboucoffee;

import android.content.Context;
import android.content.SharedPreferences;

import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.TriviaEventSource;

/**
 * Created by gonzalo.gelos on 1/16/18.
 */

public class AppDataStorageImpl implements AppDataStorage {

    private final Context mContext;

    private static final String ORDER_SCREEN_KEY = "orderScreen";

    private static final String TRIVIA_EVENT_SOURCE = "triviaEventSource";

    public AppDataStorageImpl(Context context) {
        mContext = context;
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(AppConstants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public void setOrderLastScreen(AppScreen screen) {
        getSharedPreferences().edit().putString(ORDER_SCREEN_KEY, screen.name()).apply();
    }

    @Override
    public AppScreen getOrderLastScreen() {
        String enumName = getSharedPreferences().getString(ORDER_SCREEN_KEY, null);
        if (enumName == null) {
            return null;
        }
        return AppScreen.valueOf(enumName);
    }

    @Override
    public void setTriviaEventSource(TriviaEventSource triviaEventSource) {
        getSharedPreferences().edit().putString(TRIVIA_EVENT_SOURCE, triviaEventSource.name()).apply();
    }

    @Override
    public TriviaEventSource getTriviaEventSource() {
        String triviaEventSourceName = getSharedPreferences().getString(TRIVIA_EVENT_SOURCE, null);
        return triviaEventSourceName == null ? null : TriviaEventSource.valueOf(triviaEventSourceName);
    }


}
