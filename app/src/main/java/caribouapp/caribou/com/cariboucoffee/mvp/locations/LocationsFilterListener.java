package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import java.util.Set;

public interface LocationsFilterListener {

    void applyFilters(Set<StoreFeature> storeFeatures);
}
