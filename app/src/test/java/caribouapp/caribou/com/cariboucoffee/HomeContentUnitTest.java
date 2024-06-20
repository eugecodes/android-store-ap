package caribouapp.caribou.com.cariboucoffee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.tz.UTCProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.DashboardContract;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.DashboardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.DashboardPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.TimeOfDay;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.TimeOfDayTimeRanges;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.push.PushManager;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class HomeContentUnitTest {

    private static final String USER_UID = "398472389473298";

    @Mock
    private DashboardContract.View mDashboardViewContract;
    @Mock
    private AppDataStorage mAppDataStorage;
    @Mock
    private EventLogger mEventLogger;
    @Mock
    private UserServices mUserServices;
    @Mock
    private OrderService mOrderService;
    @Mock
    private PushManager mPushManager;
    @Mock
    private Tagger mTagger;
    @Mock
    private SettingsServices mSettingsServices;

    private ArgumentCaptor<TimeOfDay> mTimeOfDayArgumentCaptor;

    @Before
    public void setup() {
        DateTimeZone.setProvider(new UTCProvider());
        mTimeOfDayArgumentCaptor = ArgumentCaptor.forClass(TimeOfDay.class);
    }


    @Test
    public void testHomeMessage1() {
        testHourAndMatchMessage(TimeOfDay.Morning, TimeOfDay.Morning, new String[]{"Get up and get crackin", "Good morning sunshine"}, 10, 20);
    }

    @Test
    public void testHomeMessage2() {
        testHourAndMatchMessage(TimeOfDay.LateNight, TimeOfDay.LateNight, new String[]{"Hey you! A bit late isn't it?"}, 0, 0);
    }

    @Test
    public void testHomeMessage3() {
        testHourAndMatchMessage(TimeOfDay.Night, TimeOfDay.Night, new String[]{"Hey you! Nighty night"}, 20, 0);
    }

    @Test
    public void testHomeMessage4() {
        testHourAndMatchMessage(TimeOfDay.Night, TimeOfDay.LoggedOut, new String[]{"Hey you! Log in please!"}, 20, 0);
    }

    @Test
    public void testNoWelcomeMessages() {
        when(mUserServices.isUserLoggedIn()).thenReturn(true);

        DashboardModel dashboardModel = new DashboardModel();
        dashboardModel.setDailyTriviaActive(false);
        dashboardModel.setRanges(GsonUtil.defaultGson().fromJson(
                new InputStreamReader(getClass()
                        .getResourceAsStream("/time_of_day_data_no_messages_test.json")),
                TimeOfDayTimeRanges.class));

        DashboardPresenter dashboardPresenter = buildDashboardPresenter(dashboardModel);

        String defaultWelcomeMessage = "HAPPY TO SEE YOU!";
        dashboardPresenter.setDefaultTitle(defaultWelcomeMessage);

        // Check null welcome message list
        dashboardPresenter.setClock(new CustomClock(new DateTime(2019, 04, 1, 2, 10)));
        dashboardPresenter.updateTimeOfDayData();
        assertEquals(defaultWelcomeMessage, dashboardModel.getTitle());
    }

    private DashboardPresenter buildDashboardPresenter(DashboardModel dashboardModel) {
        DashboardPresenter dashboardPresenter = new DashboardPresenter(mDashboardViewContract, dashboardModel);
        dashboardPresenter.setRandom(new Random());
        dashboardPresenter.setUserServices(mUserServices);
        dashboardPresenter.setAppDataStorage(mAppDataStorage);
        dashboardPresenter.setEventLogger(mEventLogger);
        dashboardPresenter.setOrderService(mOrderService);
        dashboardPresenter.setTagger(mTagger);
        dashboardPresenter.setPushManager(mPushManager);
        dashboardPresenter.setSettings(mSettingsServices);
        return dashboardPresenter;
    }

    @Test
    public void testTriviaHomeMessage() {
        DashboardModel dashboardModel = new DashboardModel();
        DashboardPresenter dashboardPresenter = buildDashboardPresenter(dashboardModel);

        SettingsServices settingsServices = mock(SettingsServices.class);
        when(settingsServices.isTrivia()).thenReturn(true);
        dashboardPresenter.setSettings(settingsServices);
        dashboardPresenter.setDefaultTitle("HAPPY TO SEE YOU!");

        UserServices userServices = mock(UserServices.class);
        when(userServices.isUserLoggedIn()).thenReturn(true);
        when(userServices.getLastPlayedTrivia()).thenReturn(new LocalDate(2019, 5, 21));
        dashboardPresenter.setUserServices(userServices);

        dashboardPresenter.setTimeOfDayFinishLoading(true);
        List<String> triviaMessages = new ArrayList();
        triviaMessages.add("CARPE THE DAY");
        triviaMessages.add("BEGIN WITH A WIN");
        triviaMessages.add("WAKE UP YOUR BRAIN");
        dashboardPresenter.setRandom(new Random());
        dashboardPresenter.getModel().setTriviaMessages(triviaMessages);

        dashboardModel.setRanges(GsonUtil.defaultGson().fromJson(
                new InputStreamReader(getClass()
                        .getResourceAsStream("/time_of_day_data_test.json")),
                TimeOfDayTimeRanges.class));

        dashboardPresenter.setClock(new CustomClock(new DateTime()));

        dashboardPresenter.checkTriviaAvailable();
        dashboardPresenter.updateData();
        dashboardPresenter.pushRegister();

        verify(settingsServices, times(1)).isTrivia();
        verify(mDashboardViewContract, times(2)).updateTriviaOnDashboard();

        assertTrue(dashboardModel.isDailyTriviaActive());
        assertNotEquals("Hey you! Log in please!", dashboardModel.getTitle());
        assertNotEquals("Hey you! Nighty night", dashboardModel.getTitle());
        assertNotEquals("Hey you! A bit late isn't it?", dashboardModel.getTitle());
        assertNotEquals("Get up and get crackin", dashboardModel.getTitle());
        assertNotEquals("Good morning sunshine", dashboardModel.getTitle());
        assertNotEquals("HAPPY TO SEE YOU!", dashboardModel.getTitle());
        assertThat(triviaMessages).contains(dashboardPresenter.getModel().getTitle());
    }

    @Test
    public void testTriviaAlreadyPlayedHomeMessage() {

        DashboardModel dashboardModel = new DashboardModel();
        DashboardPresenter dashboardPresenter = buildDashboardPresenter(dashboardModel);

        when(mSettingsServices.isTrivia()).thenReturn(true);
        dashboardPresenter.setDefaultTitle("HAPPY TO SEE YOU!");

        dashboardPresenter.setTimeOfDayFinishLoading(true);
        List<String> triviaMessages = new ArrayList();
        triviaMessages.add("CARPE THE DAY");
        triviaMessages.add("BEGIN WITH A WIN");
        triviaMessages.add("WAKE UP YOUR BRAIN");
        dashboardPresenter.getModel().setTriviaMessages(triviaMessages);

        dashboardModel.setRanges(GsonUtil.readObjectFromClasspath("/time_of_day_data_test.json", TimeOfDayTimeRanges.class));

        dashboardPresenter.setClock(new CustomClock(new DateTime(2019, 5, 21, 15, 0)));

        dashboardPresenter.checkTriviaAvailable();
        dashboardPresenter.updateData();
        dashboardPresenter.pushRegister();

        verify(mSettingsServices, times(1)).isTrivia();
        verify(mDashboardViewContract, times(2)).updateTriviaOnDashboard();

        assertFalse(dashboardModel.isDailyTriviaActive());
        assertThat(triviaMessages).doesNotContain(dashboardPresenter.getModel().getTitle());
    }


    private void testHourAndMatchMessage(TimeOfDay expectedTimeOfDayBackground, TimeOfDay expectedTimeOfDay, String[] possibleMessages, int hour, int minutes) {
        Set<String> expectedTitles = new HashSet<>();
        expectedTitles.addAll(Arrays.asList(possibleMessages));

        doNothing().when(mDashboardViewContract).setBackgroundStyle(mTimeOfDayArgumentCaptor.capture());

        when(mUserServices.isUserLoggedIn()).thenReturn(!expectedTimeOfDay.equals(TimeOfDay.LoggedOut));

        DashboardModel dashboardModel = new DashboardModel();
        dashboardModel.setDailyTriviaActive(false);
        dashboardModel.setRanges(GsonUtil.readObjectFromClasspath("/time_of_day_data_test.json", TimeOfDayTimeRanges.class));

        DashboardPresenter dashboardPresenter = buildDashboardPresenter(dashboardModel);

        dashboardPresenter.setDefaultTitle("HAPPY TO SEE YOU!");

        dashboardPresenter.setClock(new Clock() {
            @Override
            public LocalTime getCurrentTime() {
                return new LocalTime(hour, minutes);
            }

            @Override
            public DateTime getCurrentDateTime() {
                return new DateTime(2017, 11, 20, hour, minutes, 0, 0);
            }
        });

        dashboardPresenter.updateTimeOfDayData();

        assertThat(expectedTitles).contains(dashboardPresenter.getModel().getTitle());

        assertEquals(expectedTimeOfDayBackground, mTimeOfDayArgumentCaptor.getValue());
    }

}
