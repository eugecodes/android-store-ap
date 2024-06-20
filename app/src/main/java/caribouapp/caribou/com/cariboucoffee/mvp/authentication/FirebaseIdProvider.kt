package caribouapp.caribou.com.cariboucoffee.mvp.authentication

interface FirebaseIdProvider {

    fun getFirebaseId(): String?
    fun fetchFirebaseId(): String?
}
