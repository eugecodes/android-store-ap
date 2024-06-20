package caribouapp.caribou.com.cariboucoffee.api.model.trivia;

import java.io.Serializable;

public class StartTriviaRequest extends TriviaBaseRequest implements Serializable {

    public StartTriviaRequest(String uid) {
        super(uid);
    }
}
