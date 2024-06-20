package caribouapp.caribou.com.cariboucoffee.common.callbacks;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapperBuilder;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jmsmuy on 12/26/17.
 */

public abstract class BaseViewRetrofitErrorMapperCallback<T extends ResponseWithHeader> extends BaseViewRetrofitCallback<T> {

    private ErrorMessageMapper mErrorMessageMapper;

    public BaseViewRetrofitErrorMapperCallback(MvpView baseView) {
        this(baseView, true);
    }

    public BaseViewRetrofitErrorMapperCallback(MvpView baseView, boolean showLoadingIndicator) {
        this(baseView, showLoadingIndicator, false);
    }

    public BaseViewRetrofitErrorMapperCallback(MvpView baseView, boolean showLoadingIndicator,
                                               boolean showProcessingText) {
        super(baseView, showLoadingIndicator, showProcessingText);
        mErrorMessageMapper = buildErrorMessageMapper();
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        onPostResponse();
        if (mErrorMessageMapper.isSuccessful(response)) {
            onSuccess(response);
        } else {
            onFail(response);
        }
    }

    protected ErrorMessageMapper buildErrorMessageMapper() {
        return ErrorMessageMapperBuilder.buildErrorMessageMapper(getMvpView());
    }

    @Override
    protected void onFail(Response<T> response) {
        onErrorMapperFail(response);
    }

    private void onErrorMapperFail(Response<T> response) {
        UIUtil.runWithBaseView(getBaseView(), baseView -> mErrorMessageMapper
                .displayError(response, baseView));
    }

    public static class InjectionHelper {
        @Inject
        ErrorMessageMapper mErrorMessageMapper;

        public ErrorMessageMapper getErrorMessageMapper() {
            return mErrorMessageMapper;
        }

    }

}
