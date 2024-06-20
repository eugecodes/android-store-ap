package caribouapp.caribou.com.cariboucoffee.fiserv.api;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.FiservAnonResponse;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.SaleRequest;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.SaleResponse;

/**
 * Created by Swapnil 0117/10/22
 */

public interface PayGateService {
    void acquireToken(ResultCallback<FiservAnonResponse> callback, String uniqueId);

    void startAuthorizeSale(ResultCallback<SaleResponse> callback,
                            SaleRequest saleRequest);
}
