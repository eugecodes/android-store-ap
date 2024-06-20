package caribouapp.caribou.com.cariboucoffee.di

import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger
import caribouapp.caribou.com.cariboucoffee.domain.AuthorizeSaleUseCase
import caribouapp.caribou.com.cariboucoffee.fiserv.api.PayGateService
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices
import dagger.Module
import dagger.Provides
import java.security.interfaces.RSAPublicKey
import javax.inject.Named

@Module
object DomainModule {

    @Provides
    fun provideAuthorizeUseCase(
        @Named("PayGatePublicKey")
        payGatePubKey: RSAPublicKey,
        payGateService: PayGateService,
        userServices: UserServices,
        settingsServices: SettingsServices,
        eventLogger: EventLogger,
        tagger: Tagger,
    ): AuthorizeSaleUseCase = AuthorizeSaleUseCase(
        payGatePubKey = payGatePubKey,
        payGateService = payGateService,
        userServices = userServices,
        settingsServices = settingsServices,
        eventLogger = eventLogger,
        tagger = tagger,
    )
}
