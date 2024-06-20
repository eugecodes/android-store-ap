package caribouapp.caribou.com.cariboucoffee;

import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.TriviaEventSource;

/**
 * Created by gonzalo.gelos on 1/16/18.
 */

public interface AppDataStorage {

    void setOrderLastScreen(AppScreen screen);

    AppScreen getOrderLastScreen();

    void setTriviaEventSource(TriviaEventSource triviaEventSource);

    TriviaEventSource getTriviaEventSource();
}

