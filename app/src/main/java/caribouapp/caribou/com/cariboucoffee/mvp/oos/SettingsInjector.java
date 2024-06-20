package caribouapp.caribou.com.cariboucoffee.mvp.oos;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;

public class SettingsInjector {

    @Inject
    SettingsServices mSettingsServices;

    public SettingsServices getSettingsServices() {
        return mSettingsServices;
    }
}
