package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;

/**
 * Created by gonzalogelos on 8/27/18.
 */

public interface UserAccountService {

    void getProfileData(ResultCallback<AmsResponse> callback);

    void getProfileDataWithCache(ResultCallback<Void> callback);

}
