package caribouapp.caribou.com.cariboucoffee.fiserv.api;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.AccountTokenRequest;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.AccountTokenResponse;

/**
 * Created by Swapnil 0117/10/22
 */

public interface FiservService {
    void acquireAccountToken(ResultCallback<AccountTokenResponse> callback,
                             AccountTokenRequest accountTokenRequest);
}
