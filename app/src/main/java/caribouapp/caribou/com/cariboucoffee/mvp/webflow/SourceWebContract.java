package caribouapp.caribou.com.cariboucoffee.mvp.webflow;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

/**
 * Created by andressegurola on 11/28/17.
 */

public interface SourceWebContract {
    interface View extends MvpView {
        /**
         * Display the given Caribou Perks URL.
         * This is useful to display existing caribou mobile webapp features embedded in the app.
         *
         * @param authToken   If not null this is added as a cookie to do automatic login of the current native
         *                    app user to the mobile webapp
         * @param url         path to the mobile webapp page that we want to display inside the native app
         * @param redirectUrl url that the mobile webapp should navigate to after finishing with the web use case.
         */
        void updateUrl(String authToken, String url, String redirectUrl);
    }

    interface Presenter extends MvpPresenter {
        /**
         * Load necessary data to load a caribou mobile webapp page.
         *
         * @param sendAuthToken if true the native app will request a short lived auth token to be used as a method to
         *                      automatically log in the user in the mobile webapp.
         * @param webUrl      the url domain we want the user to redirect, it should always be a brand related link.
         * @param redirectUrl   url that the mobile webapp should navigate to after finishing with the web use case.
         */
        void loadUrl(boolean sendAuthToken, String webUrl, String redirectUrl);
    }

    interface CaptivePortalWeb extends MvpPresenter {
        interface View extends MvpView {

            void finishCaptivePortalWeb();

        }

        interface Presenter extends MvpPresenter {
            void checkCaptivePortal();

        }
    }
}
