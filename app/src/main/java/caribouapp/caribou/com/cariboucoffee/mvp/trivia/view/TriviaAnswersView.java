package caribouapp.caribou.com.cariboucoffee.mvp.trivia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutTriviaAnswersListBinding;

public class TriviaAnswersView extends LinearLayout implements TriviaAnswerView.TriviaAnswerListener {

    private List<TriviaAnswerView> mAnswerList = new ArrayList<>();
    private Integer mCorrectAnswer;
    private Integer mSelectedAnswer;
    private LayoutTriviaAnswersListBinding mBinding;
    private TriviaAnswerListener mListener;

    public TriviaAnswersView(Context context) {
        super(context);
        init();
    }

    public TriviaAnswersView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TriviaAnswersView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBinding = LayoutTriviaAnswersListBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    public void addAnswer(String answer) {
        TriviaAnswerView triviaAnswer = new TriviaAnswerView(mBinding.getRoot().getContext(), answer, this);
        mAnswerList.add(triviaAnswer);
        addToView(triviaAnswer);
    }

    private void addToView(TriviaAnswerView triviaAnswer) {
        mBinding.llTriviaAnswer.addView(triviaAnswer);
    }

    private void removeFromView(TriviaAnswerView view) {
        mBinding.llTriviaAnswer.removeView(view);
    }

    public void clearAnswers() {
        for (TriviaAnswerView view : mAnswerList) {
            removeFromView(view);
        }
        mAnswerList.clear();
    }

    private void updateView() {
        for (TriviaAnswerView answer : mAnswerList) {
            answer.clearSelection();
        }

        setButtonsEnabled(mCorrectAnswer == null && mSelectedAnswer == null);

        if (mCorrectAnswer == null && mSelectedAnswer != null) {
            // First case Answer has been selected
            mAnswerList.get(mSelectedAnswer).setAsSelected();
        } else if (mCorrectAnswer != null && mSelectedAnswer != null && mCorrectAnswer.equals(mSelectedAnswer)) {
            // Second case Correct answer has been selected
            mAnswerList.get(mSelectedAnswer).setAsCorrect();
        } else if (mCorrectAnswer != null && mSelectedAnswer != null && !mCorrectAnswer.equals(mSelectedAnswer)) {
            // Second case Incorrect answer has been selected
            mAnswerList.get(mSelectedAnswer).setAsIncorrect();
            mAnswerList.get(mCorrectAnswer).setAsCorrect();
        }

    }

    private void setButtonsEnabled(boolean buttonsEnabled) {
        for (TriviaAnswerView answer : mAnswerList) {
            answer.enableButton(buttonsEnabled);
        }
    }

    public void setCorrectAnswer(String correctAnswer) {
        int correctAnswerIndex = 0;
        for (TriviaAnswerView answer : mAnswerList) {
            if (answer.getAnswer().equals(correctAnswer)) {
                mCorrectAnswer = correctAnswerIndex;
                break;
            }
            correctAnswerIndex++;
        }
        updateView();
    }

    @Override
    public void answerSelected(TriviaAnswerView triviaAnswerView) {
        mSelectedAnswer = mAnswerList.indexOf(triviaAnswerView);
        mListener.answerSelected(triviaAnswerView.getAnswer());
        updateView();
    }

    public void setListener(TriviaAnswerListener listener) {
        mListener = listener;
    }

    public interface TriviaAnswerListener {
        void answerSelected(String answer);
    }
}
