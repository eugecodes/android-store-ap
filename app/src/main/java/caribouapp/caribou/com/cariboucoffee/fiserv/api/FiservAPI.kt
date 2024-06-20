package caribouapp.caribou.com.cariboucoffee.fiserv.api

import caribouapp.caribou.com.cariboucoffee.fiserv.model.AccountTokenRequest
import caribouapp.caribou.com.cariboucoffee.fiserv.model.AccountTokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Swapnil on 03/10/22.
 */
interface FiservAPI {
    @POST("account-tokens")
    fun getFiservAccountToken(@Body request: AccountTokenRequest?): Call<AccountTokenResponse?>?
}
