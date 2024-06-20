package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStreamReader;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

/**
 * Created by andressegurola on 10/18/17.
 */

public class DashboardDataStorageImpl implements DashboardDataStorage {

    private static final String TAG = DashboardDataStorageImpl.class.getSimpleName();


    private final Context mContext;

    public DashboardDataStorageImpl(Context context) {
        mContext = context;
    }


    @Override
    public void loadTimeOfDayRanges(ResultCallback<TimeOfDayTimeRanges> callback) {
        try {
            new LoadTimeOfDayDataAsyncTask(callback, new InputStreamReader(mContext.getAssets().open("time_of_day_data.json"))).execute();
        } catch (IOException e) {
            callback.onError(e);
        }
    }

    static class LoadTimeOfDayDataAsyncTask extends AsyncTask<Void, Void, TimeOfDayTimeRanges> {

        private final InputStreamReader mInputReader;
        private ResultCallback<TimeOfDayTimeRanges> mCallback;

        LoadTimeOfDayDataAsyncTask(ResultCallback<TimeOfDayTimeRanges> callback, InputStreamReader inputReader) {
            mCallback = callback;
            mInputReader = inputReader;
        }

        @Override
        protected TimeOfDayTimeRanges doInBackground(Void... voids) {
            return GsonUtil.defaultGson().fromJson(mInputReader, TimeOfDayTimeRanges.class);
        }

        @Override
        protected void onPostExecute(TimeOfDayTimeRanges data) {
            mCallback.onSuccess(data);
        }
    }
}
