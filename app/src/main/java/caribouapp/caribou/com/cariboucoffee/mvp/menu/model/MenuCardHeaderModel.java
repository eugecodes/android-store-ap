package caribouapp.caribou.com.cariboucoffee.mvp.menu.model;

/**
 * Created by jmsmuy on 10/12/17.
 */

public class MenuCardHeaderModel extends MenuCardModel {

    private String mHeader;

    public MenuCardHeaderModel(String header) {
        mHeader = header;
    }

    public String getHeader() {
        return mHeader;
    }

    public void setHeader(String header) {
        mHeader = header;
    }

}
