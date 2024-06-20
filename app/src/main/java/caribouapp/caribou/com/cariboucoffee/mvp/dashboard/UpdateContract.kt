package caribouapp.caribou.com.cariboucoffee.mvp.dashboard

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView

class UpdateContract {
    interface View : MvpView {
        fun showRecommendUpdate()
        fun showVersionIncompatible()
        fun updatesFinished()
    }

    interface Presenter : MvpPresenter {
        suspend fun runUpdates()
        fun snoozeUpdate()
    }
}
