package caribouapp.caribou.com.cariboucoffee.api.model.trivia;

import java.io.Serializable;

public class TriviaCheckResquest extends TriviaBaseRequest implements Serializable {

    public TriviaCheckResquest(String uid) {
        super(uid);
    }
}
