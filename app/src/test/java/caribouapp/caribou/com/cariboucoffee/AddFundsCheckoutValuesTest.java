package caribouapp.caribou.com.cariboucoffee;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.AddFundsContract;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter.AddFundsPresenter;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddFundsCheckoutValuesTest {

    @Mock
    private SettingsServices mSettingsServicesMock;
    @Mock
    private AppDataStorage mAppDataStorageMock;

    AddFundsContract.Presenter mPresenter;

    @Before
    public void setup() {
        mPresenter = new AddFundsPresenter(null, true, null);
        ((AddFundsPresenter) mPresenter).setSettingsServices(mSettingsServicesMock);
        ((AddFundsPresenter) mPresenter).setAppDataStorage(mAppDataStorageMock);
    }

    @Test
    public void test1() {
        when(mSettingsServicesMock.getAddFundsCheckoutMinAmount()).thenReturn(5.00d);
        mPresenter.init();
        List<BigDecimal> testList = Arrays.asList(
                new BigDecimal("5.00"),
                new BigDecimal("10.00"),
                new BigDecimal("15.00"),
                new BigDecimal("25.00"),
                new BigDecimal("35.00"));
        Assert.assertEquals(testList, mPresenter.getCheckoutAddFundsValues(new BigDecimal("3.58")));
    }

    @Test
    public void test2() {
        when(mSettingsServicesMock.getAddFundsCheckoutMinAmount()).thenReturn(0.00d);
        mPresenter.init();
        List<BigDecimal> testList = Arrays.asList(
                new BigDecimal("3.58"),
                new BigDecimal("5.00"),
                new BigDecimal("10.00"),
                new BigDecimal("20.00"),
                new BigDecimal("30.00"));
        Assert.assertEquals(testList, mPresenter.getCheckoutAddFundsValues(new BigDecimal("3.58")));
    }

    @Test
    public void test3() {
        when(mSettingsServicesMock.getAddFundsCheckoutMinAmount()).thenReturn(0.00d);
        mPresenter.init();
        List<BigDecimal> testList = Arrays.asList(
                new BigDecimal("9.67"),
                new BigDecimal("10.00"),
                new BigDecimal("15.00"),
                new BigDecimal("25.00"),
                new BigDecimal("35.00"));
        Assert.assertEquals(testList, mPresenter.getCheckoutAddFundsValues(new BigDecimal("9.67")));
    }

    @Test
    public void test4() {
        when(mSettingsServicesMock.getAddFundsCheckoutMinAmount()).thenReturn(0.00d);
        mPresenter.init();
        List<BigDecimal> testList = Arrays.asList(
                new BigDecimal("13.45"),
                new BigDecimal("15.00"),
                new BigDecimal("20.00"),
                new BigDecimal("30.00"),
                new BigDecimal("40.00"));
        Assert.assertEquals(testList, mPresenter.getCheckoutAddFundsValues(new BigDecimal("13.45")));
    }

    @Test
    public void test5() {
        when(mSettingsServicesMock.getAddFundsCheckoutMinAmount()).thenReturn(0.00d);
        mPresenter.init();
        List<BigDecimal> testList = Arrays.asList(
                new BigDecimal("19.56"),
                new BigDecimal("20.00"),
                new BigDecimal("25.00"),
                new BigDecimal("35.00"),
                new BigDecimal("45.00"));
        Assert.assertEquals(testList, mPresenter.getCheckoutAddFundsValues(new BigDecimal("19.56")));
    }
}
