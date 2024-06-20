package caribouapp.caribou.com.cariboucoffee.mvp.termsandprivacy.presenter;

import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsWebContentSection;
import caribouapp.caribou.com.cariboucoffee.common.WebContentWithSectionsModel;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.termsandprivacy.TermsAndPrivacyContract;
import retrofit2.Response;

/**
 * Created by andressegurola on 11/14/17.
 */

public class TermsAndPrivacyPresenter extends BasePresenter<TermsAndPrivacyContract.View> implements TermsAndPrivacyContract.Presenter {

    @Inject
    CmsApi mCmsApi;
    private WebContentWithSectionsModel mModel;

    public TermsAndPrivacyPresenter(TermsAndPrivacyContract.View view, WebContentWithSectionsModel model) {
        super(view);
        mModel = model;
    }

    @Override
    public void loadContent() {
        mCmsApi.getTermsAndPrivacy().enqueue(new BaseViewRetrofitCallback<List<CmsWebContentSection>>(getView()) {

            @Override
            protected void onSuccessViewUpdates(Response<List<CmsWebContentSection>> response) {
                mModel = new WebContentWithSectionsModel(response.body());
                getView().updateContent(mModel);
            }

        });
    }

    @Override
    public WebContentWithSectionsModel getModel() {
        return mModel;
    }
}
