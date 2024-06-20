package caribouapp.caribou.com.cariboucoffee.api;

import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsErrorResponse;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import retrofit2.Response;

public final class OmsUtil {

    public static final String TAG = OmsUtil.class.getSimpleName();

    private OmsUtil() {

    }

    public static String parseErrorMessage(Response response) {
        try {
            OmsErrorResponse omsErrorResponse =
                    GsonUtil.defaultGson().fromJson(response.errorBody().string(),
                            OmsErrorResponse.class);
            return omsErrorResponse.getErrorMessage();
        } catch (Exception e) {
            return null;
        }
    }
}
