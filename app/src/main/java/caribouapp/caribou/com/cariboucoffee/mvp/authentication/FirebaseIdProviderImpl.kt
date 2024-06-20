package caribouapp.caribou.com.cariboucoffee.mvp.authentication

import caribouapp.caribou.com.cariboucoffee.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.installations.FirebaseInstallations

class FirebaseIdProviderImpl : FirebaseIdProvider {

    private var mFirebaseId: String? = null

    override fun getFirebaseId(): String? {
        if (mFirebaseId == null) {
            mFirebaseId = fetchFirebaseId()
        } else {
            Log.d(TAG, "Cache FirebaseId: $mFirebaseId")
        }
        return mFirebaseId
    }

    override fun fetchFirebaseId(): String? {
        val firebaseIDTask = FirebaseInstallations.getInstance().id
        Tasks.await(firebaseIDTask)
        mFirebaseId = firebaseIDTask.result
        Log.d(TAG, "fetched FirebaseId: $mFirebaseId")
        return firebaseIDTask.result
    }

    companion object {
        private val TAG = FirebaseIdProviderImpl::class.java.simpleName
    }
}
