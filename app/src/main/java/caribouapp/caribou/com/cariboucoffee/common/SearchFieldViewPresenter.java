package caribouapp.caribou.com.cariboucoffee.common;

/**
 * Created by jmsmuy on 3/15/18.
 */

public interface SearchFieldViewPresenter {

    void search(String text);

    void showFilterDialog();

    void searchCleared();

    void searchTextEmpty(boolean isEmpty);
}
