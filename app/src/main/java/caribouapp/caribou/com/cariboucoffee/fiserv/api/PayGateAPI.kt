package caribouapp.caribou.com.cariboucoffee.fiserv.api

import caribouapp.caribou.com.cariboucoffee.fiserv.model.FiservAnonRequest
import caribouapp.caribou.com.cariboucoffee.fiserv.model.FiservAnonResponse
import caribouapp.caribou.com.cariboucoffee.fiserv.model.SaleRequest
import caribouapp.caribou.com.cariboucoffee.fiserv.model.SaleResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Swapnil on 03/10/22.
 */
interface PayGateAPI {
    @POST("token")
    fun getPayGateAnonymousToken(@Body request: FiservAnonRequest?): Call<FiservAnonResponse?>?

    @POST("sale")
    fun makeAuthorizeSale(@Body request: SaleRequest?): Call<SaleResponse>?
}
