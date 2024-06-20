package caribouapp.caribou.com.cariboucoffee.mvp.trivia.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.util.List;

public class TriviaQuestionModel extends BaseObservable implements Serializable {

    private List<String> mAnswers;
    private String mQuestion;
    private String mCorrectAnswer;
    private String mUserAnswer;

    @Bindable
    public List<String> getAnswers() {
        return mAnswers;
    }

    public void setAnswers(List<String> answers) {
        mAnswers = answers;
        notifyPropertyChanged(BR.answers);
    }

    @Bindable
    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String question) {
        mQuestion = question;
        notifyPropertyChanged(BR.question);
    }

    @Bindable
    public String getCorrectAnswer() {
        return mCorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        mCorrectAnswer = correctAnswer;
        notifyPropertyChanged(BR.correctAnswer);
    }

    public String getUserAnswer() {
        return mUserAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        mUserAnswer = userAnswer;
    }
}
