package caribouapp.caribou.com.cariboucoffee.mvp.push

import caribouapp.caribou.com.cariboucoffee.SourceApplication
import com.urbanairship.PrivacyManager
import com.urbanairship.UAirship

class UAirshipServicesImpl : UAirshipServices {
    private val notificationFeatures = intArrayOf(
        PrivacyManager.FEATURE_PUSH,
        PrivacyManager.FEATURE_MESSAGE_CENTER
    )

    private val analyticsFeatures = intArrayOf(
        PrivacyManager.FEATURE_CONTACTS,
        PrivacyManager.FEATURE_ANALYTICS,
        PrivacyManager.FEATURE_TAGS_AND_ATTRIBUTES,
    )

    override fun enableNotifications() {
        UAirship.shared().privacyManager.enable(*notificationFeatures)
    }

    override fun disableNotifications() {
        UAirship.shared().privacyManager.disable(*notificationFeatures)
    }

    override fun setupUrbanAirship(app: SourceApplication, callback: (UAirship) -> Unit) {
        UAirship.takeOff(app) { ua ->
            ua.privacyManager.enable(*analyticsFeatures)
            callback(ua)
        }
    }
}
