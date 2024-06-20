package caribouapp.caribou.com.cariboucoffee.api

import caribouapp.caribou.com.cariboucoffee.BuildConfig
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices
import caribouapp.caribou.com.cariboucoffee.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class TimeoutInterceptor(val settingsServices: SettingsServices) : Interceptor {

    val TAG = TimeoutInterceptor::class.java.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        val customTimeoutId: TimeoutGroup = TimeoutGroup.from(chain.request().header(TIMEOUT_GROUP_HEADER))

        val timeout = when (customTimeoutId) {
            TimeoutGroup.AddFunds -> settingsServices.timeoutAddFunds
            TimeoutGroup.Default -> settingsServices.timeoutDefault
        }

        val request = chain.request().newBuilder()
            .removeHeader(TIMEOUT_GROUP_HEADER)
            .build()

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Target: ${chain.request().url} Request timeout: $timeout seconds")
        }

        return chain.withConnectTimeout(timeout, TimeUnit.SECONDS)
            .withReadTimeout(timeout, TimeUnit.SECONDS)
            .withWriteTimeout(timeout, TimeUnit.SECONDS)
            .proceed(request)
    }
}
