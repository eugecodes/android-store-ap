package caribouapp.caribou.com.cariboucoffee.mvp.trivia.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class TriviaAnswerModel extends BaseObservable {

    private String mAnswer;

    @Bindable
    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String answer) {
        mAnswer = answer;
        notifyPropertyChanged(BR.answer);
    }
}
