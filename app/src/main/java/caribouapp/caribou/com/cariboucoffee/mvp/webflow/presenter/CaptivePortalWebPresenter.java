package caribouapp.caribou.com.cariboucoffee.mvp.webflow.presenter;

import java.net.HttpURLConnection;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.api.CheckCaptivePortalApi;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.SourceWebContract;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gonzalogelos on 3/6/18.
 */

public class CaptivePortalWebPresenter extends BasePresenter<SourceWebContract.CaptivePortalWeb.View>
        implements SourceWebContract.CaptivePortalWeb.Presenter {

    @Inject
    CheckCaptivePortalApi mCheckCaptivePortalApi;


    public CaptivePortalWebPresenter(SourceWebContract.CaptivePortalWeb.View view) {
        super(view);
    }

    @Override
    public void checkCaptivePortal() {
        mCheckCaptivePortalApi.checkInternetConnectivity().enqueue(
                new Callback<ResponseBody>() {
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if ((response.code() == HttpURLConnection.HTTP_NO_CONTENT || hasEmptyBody(response.body())) && getView() != null) {
                            // Already accept terms and condition and we have internet
                            // We close the webView and the refresh the screen
                            getView().finishCaptivePortalWeb();
                        }
                    }

                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("Captive portal Error", new LogErrorException("OnFailure", t));
                    }
                }
        );
    }

    private boolean hasEmptyBody(ResponseBody responseBody) {
        return responseBody.contentLength() == 0;
    }
}
