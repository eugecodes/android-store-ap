package caribouapp.caribou.com.cariboucoffee.common.callbacks;

import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapperBuilder;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;

/**
 * Created by gonzalogelos on 9/3/18.
 */

public abstract class ResultCallbackErrorMapper<T extends ResponseWithHeader> extends BaseViewResultCallback<T> {

    private static final String TAG = BaseViewResultCallback.class.getSimpleName();

    private ErrorMessageMapper mErrorMessageMapper;

    public ResultCallbackErrorMapper(MvpView baseView, boolean showLoadingIndicator) {
        super(baseView, showLoadingIndicator);
        mErrorMessageMapper = buildErrorMessageMapper();
    }

    protected ErrorMessageMapper buildErrorMessageMapper() {
        return ErrorMessageMapperBuilder.buildErrorMessageMapper(getBaseView().get());
    }

    public ResultCallbackErrorMapper(MvpView baseView) {
        this(baseView, true);
    }

    @Override
    public void onSuccess(T data) {
        super.onPostResponse();
        if (mErrorMessageMapper.isSuccessful(data)) {
            UIUtil.runWithBaseView(getBaseView(), baseView -> onSuccessViewUpdates(data));
        } else {
            UIUtil.runWithBaseView(getBaseView(), baseView -> mErrorMessageMapper.displayError(data, baseView));
        }
    }

    protected abstract void onSuccessViewUpdates(T data);
}
