package caribouapp.caribou.com.cariboucoffee

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import caribouapp.caribou.com.cariboucoffee.api.RestModule
import caribouapp.caribou.com.cariboucoffee.api.RestOAuthV2Module
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.FirebaseIdProvider
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.DashboardDataStorage
import caribouapp.caribou.com.cariboucoffee.mvp.push.UAirshipServices
import caribouapp.caribou.com.cariboucoffee.util.AppUtils
import caribouapp.caribou.com.cariboucoffee.util.Log
import com.newrelic.agent.android.NewRelic
import com.securepreferences.SecurePreferences
import com.urbanairship.UAirship
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by asegurola on 11/22/16.
 */
class SourceApplication : Application() {

    @Inject
    lateinit var mUAirshipServices: UAirshipServices

    @Inject
    lateinit var mUserServices: UserServices

    @Inject
    lateinit var mSettingsServices: SettingsServices

    @Inject
    lateinit var mDashboardDataStorage: DashboardDataStorage

    @Inject
    lateinit var mFirebaseIdProvider: FirebaseIdProvider

    lateinit var component: CaribouComponent
    private var mCurrentActivity: Activity? = null
    override fun onCreate() {
        super.onCreate()
        val debugBuild = AppUtils.isDebug()
        if (debugBuild) {
            SecurePreferences.setLoggingEnabled(true)
        }
        component = DaggerCaribouComponent.builder()
            .restModule(RestModule())
            .restOAuthV2Module(RestOAuthV2Module())
            .appModule(AppModule(this)).build()
        if (!debugBuild) {
            // Start new relic agent
            NewRelic.withApplicationToken(BuildConfig.NEW_RELIC_APP_ID).start(this)
        }
        component.inject(this)

        fetchFirebaseId()
        setupUrbanAirship()
        upgrades()
    }

    private fun fetchFirebaseId() {
        val semaphore = Semaphore(0)
        CoroutineScope(IO).launch {
            mFirebaseIdProvider.fetchFirebaseId()
            semaphore.release()
        }
        if (!semaphore.tryAcquire(20, TimeUnit.SECONDS)) {
            throw IllegalStateException("Error fetching Firebase installation Id")
        }
    }

    private fun setupUrbanAirship() {
        mUAirshipServices.setupUrbanAirship(this) { uAirship: UAirship ->

            if (mSettingsServices.isGuestNotificationEnabled) {
                mUAirshipServices.enableNotifications()
            } else {
                if (mUserServices.isUserLoggedIn) {
                    mUAirshipServices.enableNotifications()
                } else {
                    mUAirshipServices.disableNotifications()
                }

                mUserServices.addSignoutListener { mUAirshipServices.disableNotifications() }
            }

            setUpNotificationChannel()
            Log.d(TAG, "My Application Channel ID: " + uAirship.channel.id)
            Log.d(TAG, "Registration token: " + uAirship.pushManager.pushToken)
        }
    }

    private fun upgrades() {
        // Since we started obfuscating code we need to discard existing cache data
        AppUtils.runOnce(this, "cleanPrefsForObfuscationChanges") {
            mUserServices.signOut()
            mSettingsServices.clear()
        }
    }

    private fun setUpNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(
            NotificationChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }

    companion object {
        private val TAG = SourceApplication::class.java.simpleName

        @JvmStatic
        operator fun get(context: Context): SourceApplication {
            return context.applicationContext as SourceApplication
        }
    }

    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    fun setCurrentActivity(mCurrentActivity: Activity) {
        this.mCurrentActivity = mCurrentActivity
    }
}
