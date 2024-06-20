package caribouapp.caribou.com.cariboucoffee.messages;

import android.content.Context;

import androidx.fragment.app.Fragment;

import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

public final class ErrorMessageMapperBuilder {

    private ErrorMessageMapperBuilder() { }

    public static ErrorMessageMapper buildErrorMessageMapper(MvpView mvpView) {
        BaseViewRetrofitErrorMapperCallback.InjectionHelper injectionHelper = new BaseViewRetrofitErrorMapperCallback.InjectionHelper();
        Context context = mvpView instanceof Fragment ? ((Fragment) mvpView).getContext() : (Context) mvpView;
        SourceApplication.get(context).getComponent().inject(injectionHelper);
        return injectionHelper.getErrorMessageMapper();
    }

}
