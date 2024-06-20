package caribouapp.caribou.com.cariboucoffee.mvp.dashboard

import android.text.TextUtils
import caribouapp.caribou.com.cariboucoffee.AppConstants
import caribouapp.caribou.com.cariboucoffee.BuildConfig.*
import caribouapp.caribou.com.cariboucoffee.R
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger
import caribouapp.caribou.com.cariboucoffee.common.Clock
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.FirebaseIdProvider
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices
import caribouapp.caribou.com.cariboucoffee.util.AppUtils
import caribouapp.caribou.com.cariboucoffee.util.Log
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException
import caribouapp.caribou.com.cariboucoffee.util.VersionUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.joda.time.Hours
import javax.inject.Inject

class BaseUpdatePresenter(view: UpdateContract.View) : BasePresenter<UpdateContract.View>(view),
    UpdateContract.Presenter {

    @Inject
    lateinit var mSettingsServices: SettingsServices

    @Inject
    lateinit var mFirebaseIdProvider: FirebaseIdProvider

    @Inject
    lateinit var mClock: Clock

    @Inject
    lateinit var mEventLogger: EventLogger

    override suspend fun runUpdates() {
        mFirebaseIdProvider.getFirebaseId()
        fetchRemoteConfigAndContinue()
        mEventLogger.logUserProperties(SOURCE_APP, AppUtils.getAppEnvironment())
    }

    private fun fetchRemoteConfigAndContinue() {
        val config = FirebaseRemoteConfig.getInstance()
        val cacheTimeToFetchData =
            if (DEBUG) FIREBASE_CACHE_TIME_DEBUG_MODE else AppConstants.FIREBASE_REMOTE_CONFIG_CACHE_TIME_IN_SECONDS
        val firstFetch =
            FirebaseRemoteConfig.LAST_FETCH_STATUS_NO_FETCH_YET == config.info.lastFetchStatus
        config.fetch(cacheTimeToFetchData.toLong())
            .addOnCompleteListener { fetchConfigTask: Task<Void?> ->
                if (fetchConfigTask.isSuccessful) {
                    config.activate().addOnCompleteListener { activateConfigTask: Task<Boolean?> ->
                        if (!activateConfigTask.isSuccessful) {
                            Log.e(TAG, LogErrorException("Error activating remote config"))
                        }
                        checkMinimumAndRecommendedVersion()
                    }
                } else {
                    Log.e(
                        TAG, LogErrorException(
                            "Failed fetching remote config" +
                                ", LastFetchStatus:" + parseLastFetchStatus(config.info.lastFetchStatus)
                        )
                    )
                    if (firstFetch) {
                        view.showWarning(R.string.unknown_error)
                    } else {
                        checkMinimumAndRecommendedVersion()
                    }
                }
            }
    }

    private fun parseLastFetchStatus(status: Int): String {
        return when (status) {
            -1 -> "LAST_FETCH_STATUS_SUCCESS"
            0 -> "LAST_FETCH_STATUS_NO_FETCH_YET"
            1 -> "LAST_FETCH_STATUS_FAILURE"
            2 -> "LAST_FETCH_STATUS_THROTTLED"
            else -> "UNKNOWN_STATUS"
        }
    }

    private fun checkMinimumAndRecommendedVersion() {
        val minimumVersion = mSettingsServices.minimumSupportedVersion
        val recommendedVersion = mSettingsServices.recommendedVersion
        if (view == null) {
            return
        }
        if (!TextUtils.isEmpty(minimumVersion) &&
            VersionUtil.compareVersions(VERSION_NAME, minimumVersion) < 0
        ) {
            view.showVersionIncompatible()
        } else if (!TextUtils.isEmpty(recommendedVersion) &&
            VersionUtil.compareVersions(
                VERSION_NAME,
                recommendedVersion
            ) < 0 && !isSnoozeValid
        ) {
            view.showRecommendUpdate()
        } else {
            view.updatesFinished()
        }
    }

    private val isSnoozeValid: Boolean
        get() {
            val snoozeTime = mSettingsServices.snoozeDateTime ?: return false
            val currentTime = mClock.currentDateTime
            val hoursBetween = Hours.hoursBetween(currentTime, snoozeTime)
            return Math.abs(hoursBetween.hours) <= AppConstants.SNOOZE_HOURS
        }

    override fun snoozeUpdate() {
        mSettingsServices.setSnoozeDatetime(mClock.currentDateTime)
        view.updatesFinished()
    }

    companion object {
        private val TAG = BaseUpdatePresenter::class.java.simpleName
        private const val FIREBASE_CACHE_TIME_DEBUG_MODE = 0
    }
}
