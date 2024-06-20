package caribouapp.caribou.com.cariboucoffee.mvp;

/**
 * Created by jmsmuy on 10/26/17.
 */

public abstract class BasePresenter<T extends MvpView> implements MvpPresenter {

    private T mView;

    public BasePresenter(T view) {
        mView = view;
    }

    public T getView() {
        return mView;
    }

    @Override
    public void detachView() {
        mView = null;
    }

}
