package caribouapp.caribou.com.cariboucoffee.mvp.termsandprivacy;

import caribouapp.caribou.com.cariboucoffee.common.WebContentWithSectionsModel;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

/**
 * Created by andressegurola on 11/13/17.
 */

public interface TermsAndPrivacyContract {
    interface View extends MvpView {
        void updateContent(WebContentWithSectionsModel webContentWithSectionsModel);
    }

    interface Presenter extends MvpPresenter {
        void loadContent();

        WebContentWithSectionsModel getModel();
    }
}
