package caribouapp.caribou.com.cariboucoffee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuDetailsContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuDetailsPresenter;

public class MenuDetailsPresenterUnitTest {

    private MenuDetailsContract.View mView;
    private MenuCardItemModel mMenuCardItemModel;
    private AtomicInteger mCounter;

    @Before
    public void before() {
        mView = mock(MenuDetailsContract.View.class);
        mMenuCardItemModel = new MenuCardItemModel(mock(SettingsServices.class));
        mCounter = new AtomicInteger();
    }

    @Test
    public void testNutritionAllergensAvailabilityIsChecked() {
        MenuDetailsPresenter menuDetailsPresenter = new MenuDetailsPresenter(mView, mMenuCardItemModel);

        doAnswer(invocation -> mCounter.incrementAndGet()).when(mView).onNoAllergensAvailable();
        doAnswer(invocation -> mCounter.incrementAndGet()).when(mView).onNoNutritionAvailable();
        doAnswer(invocation -> mCounter.incrementAndGet()).when(mView).onNoNutritionAllergensAvailable();

        menuDetailsPresenter.loadData();

        assertThat(mCounter).hasValue(1);
    }
}
