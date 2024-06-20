package caribouapp.caribou.com.cariboucoffee.api.model.trivia;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StartTriviaResponse implements Serializable {

    @SerializedName("question")
    private String mQuestion;

    @SerializedName("answers")
    private List<String> mAnswers;

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String question) {
        mQuestion = question;
    }

    public List<String> getAnswers() {
        return mAnswers;
    }

    public void setAnswers(List<String> answers) {
        mAnswers = answers;
    }
}
