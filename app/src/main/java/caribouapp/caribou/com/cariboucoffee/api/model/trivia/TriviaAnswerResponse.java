package caribouapp.caribou.com.cariboucoffee.api.model.trivia;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TriviaAnswerResponse implements Serializable {

    @SerializedName("inTime")
    private boolean mAnsweredInTime;

    @SerializedName("userCorrect")
    private boolean mCorrectlyAnswered;

    @SerializedName("userAnswer")
    private String mUserAnswer;

    @SerializedName("correctAnswer")
    private String mCorrectAnswer;

    public boolean isAnsweredInTime() {
        return mAnsweredInTime;
    }

    public void setAnsweredInTime(boolean answeredInTime) {
        mAnsweredInTime = answeredInTime;
    }

    public boolean isCorrectlyAnswered() {
        return mCorrectlyAnswered;
    }

    public void setCorrectlyAnswered(boolean correctlyAnswered) {
        mCorrectlyAnswered = correctlyAnswered;
    }

    public String getUserAnswer() {
        return mUserAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        mUserAnswer = userAnswer;
    }

    public String getCorrectAnswer() {
        return mCorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        mCorrectAnswer = correctAnswer;
    }
}
