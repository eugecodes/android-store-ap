package caribouapp.caribou.com.cariboucoffee.api;

import caribouapp.caribou.com.cariboucoffee.api.model.trivia.StartTriviaRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.StartTriviaResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.TriviaAnswerRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.TriviaAnswerResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.TriviaCheckResquest;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.TriviaEligibleResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TriviaApi {

    @POST("check-eligibility")
    Call<TriviaEligibleResponse> checkEligibility(@Body TriviaCheckResquest triviaCheckResquest);

    @POST("start-trivia")
    Call<StartTriviaResponse> startTrivia(@Body StartTriviaRequest startTriviaRequest);

    @POST("answer-trivia")
    Call<TriviaAnswerResponse> answerTrivia(@Body TriviaAnswerRequest triviaAnswerRequest);
}
