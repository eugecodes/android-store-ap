package caribouapp.caribou.com.cariboucoffee.mvp.faq;

import caribouapp.caribou.com.cariboucoffee.common.WebContentWithSectionsModel;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

/**
 * Created by andressegurola on 11/13/17.
 */

public interface FaqContract {
    interface View extends MvpView {
        void updateContent(WebContentWithSectionsModel model);
    }

    interface Presenter extends MvpPresenter {
        void loadContent();

        WebContentWithSectionsModel getModel();
    }
}
