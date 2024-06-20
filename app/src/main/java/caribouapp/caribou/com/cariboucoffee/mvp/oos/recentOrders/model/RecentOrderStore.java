package caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model;

import java.io.Serializable;

public class RecentOrderStore implements Serializable {

    private String mName;
    private String mAddress;
    private String mId;

    public RecentOrderStore(String name, String id) {
        mName = name;
        mId = id;
    }

    public RecentOrderStore(String name, String address, String id) {
        mName = name;
        mAddress = address;
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
