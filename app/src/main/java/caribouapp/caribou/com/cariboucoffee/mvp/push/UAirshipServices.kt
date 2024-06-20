package caribouapp.caribou.com.cariboucoffee.mvp.push

import caribouapp.caribou.com.cariboucoffee.SourceApplication
import com.urbanairship.UAirship

interface UAirshipServices {
    fun enableNotifications()
    fun disableNotifications()
    fun setupUrbanAirship(app: SourceApplication, callback: (UAirship) -> Unit)
}
