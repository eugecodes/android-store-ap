package caribouapp.caribou.com.cariboucoffee.mvp.faq.presenter;

import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsWebContentSection;
import caribouapp.caribou.com.cariboucoffee.common.WebContentWithSectionsModel;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.faq.FaqContract;
import retrofit2.Response;

/**
 * Created by andressegurola on 11/14/17.
 */

public class FaqPresenter extends BasePresenter<FaqContract.View> implements FaqContract.Presenter {

    @Inject
    CmsApi mCmsApi;
    private WebContentWithSectionsModel mModel;

    public FaqPresenter(FaqContract.View view, WebContentWithSectionsModel model) {
        super(view);
        mModel = model;
    }


    @Override
    public void loadContent() {
        mCmsApi.getFaqs().enqueue(new BaseViewRetrofitCallback<List<CmsWebContentSection>>(getView()) {
            @Override
            protected void onSuccessBeforeViewUpdates(Response<List<CmsWebContentSection>> response) {
                mModel = new WebContentWithSectionsModel(response.body());
            }

            @Override
            protected void onSuccessViewUpdates(Response<List<CmsWebContentSection>> response) {
                getView().updateContent(mModel);
            }

        });
    }

    @Override
    public WebContentWithSectionsModel getModel() {
        return mModel;
    }
}
