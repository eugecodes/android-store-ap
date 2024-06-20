package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import java.io.Serializable

class GuestInfoModel : BaseObservable(), Serializable {
    @get:Bindable
    public var guestFirstName: String? = null
        set(guestFirstName) {
            field = guestFirstName
            notifyPropertyChanged(BR.guestFirstName)
        }

    @get:Bindable
    public var guestLastName: String? = null
        set(guestLastName) {
            field = guestLastName
            notifyPropertyChanged(BR.guestLastName)
        }

    @get:Bindable
    public var guestEmailId: String? = null
        set(guestEmailId) {
            field = guestEmailId
            notifyPropertyChanged(BR.guestEmailId)
        }

    @get:Bindable
    public var guestPhoneNumber: String? = null
        set(guestPhoneNumber) {
            field = guestPhoneNumber
            notifyPropertyChanged(BR.guestPhoneNumber)
        }
}
