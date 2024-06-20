package caribouapp.caribou.com.cariboucoffee.mvp.trivia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutTriviaAnswerBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.model.TriviaAnswerModel;

public class TriviaAnswerView extends LinearLayout {

    private LayoutTriviaAnswerBinding mBinding;
    private TriviaAnswerListener mListener;
    private TriviaAnswerModel mModel;

    public TriviaAnswerView(Context context) {
        super(context);
    }

    public TriviaAnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TriviaAnswerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TriviaAnswerView(Context context, String answer, TriviaAnswersView triviaAnswersView) {
        super(context);
        mBinding = LayoutTriviaAnswerBinding.inflate(LayoutInflater.from(getContext()), this, true);

        mModel = new TriviaAnswerModel();
        mModel.setAnswer(answer);

        mBinding.btnTriviaAnswer.setOnClickListener(v -> mListener.answerSelected(this));
        mBinding.setModel(mModel);

        mListener = triviaAnswersView;
    }

    public void clearSelection() {
        mBinding.ivCorrect.setVisibility(View.INVISIBLE);
        mBinding.ivIncorrect.setVisibility(View.INVISIBLE);
    }

    public void setAsSelected() {
        mBinding.btnTriviaAnswer.setSelected(true);
    }

    public void setAsCorrect() {
        setAsSelected();
        mBinding.ivCorrect.setVisibility(View.VISIBLE);
    }

    public void setAsIncorrect() {
        setAsSelected();
        mBinding.ivIncorrect.setVisibility(View.VISIBLE);
    }

    public String getAnswer() {
        return mModel.getAnswer();
    }

    public void setAnswer(String answer) {
        mModel.setAnswer(answer);
    }

    public void setListener(TriviaAnswerListener listener) {
        mListener = listener;
    }

    public void enableButton(boolean buttonsEnabled) {
        mBinding.btnTriviaAnswer.setClickable(buttonsEnabled);
    }

    public interface TriviaAnswerListener {
        void answerSelected(TriviaAnswerView triviaAnswerView);
    }
}
