package caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.view;

import caribouapp.caribou.com.cariboucoffee.common.EntityWithId;

public class SingleOption<T> extends EntityWithId<T> {

    private String mName;

    public SingleOption(T id) {
        setId(id);
        mName = (String) id;
    }

    public SingleOption(T id, String name) {
        setId(id);
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
