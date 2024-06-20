package caribouapp.caribou.com.cariboucoffee.api.model.trivia;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TriviaAnswerRequest extends TriviaBaseRequest implements Serializable {

    @SerializedName("answer")
    private String mSelectedAnswer;

    public TriviaAnswerRequest(String uid, String answer) {
        super(uid);
        mSelectedAnswer = answer;
    }

    public String getSelectedAnswer() {
        return mSelectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        mSelectedAnswer = selectedAnswer;
    }
}
