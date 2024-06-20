package caribouapp.caribou.com.cariboucoffee;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import caribouapp.caribou.com.cariboucoffee.api.model.ResponseHeader;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by andressegurola on 11/8/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ErrorMappingTest {

    private ErrorMessageMapper mErrorMessageMapper;


    @Before
    public void setup() {
        mErrorMessageMapper = new ErrorMessageMapper(new InputStreamReader(getClass().getResourceAsStream("/error_message_mappings.json")));
    }

    @Test
    public void testErrorMappingsFailure1031() {
        AmsResponse amsResponse = new AmsResponse();
        ResponseHeader amsHeader = new ResponseHeader();
        amsHeader.setStatus(ResponseHeader.FAILURE_STATUS);
        amsHeader.setErrorCode("1031");
        amsHeader.setMsg("UID not found in CRM\\r\\n");
        amsResponse.setHeader(amsHeader);

        Response<AmsResponse> response = Response.success(amsResponse);

        Assert.assertFalse(mErrorMessageMapper.isSuccessful(response));

        MvpView mvpView = mock(MvpView.class);
        mErrorMessageMapper.displayError(response, mvpView);
        verify(mvpView).showMessage("Sorry, but we had a problem retrieving your account information. Please try logging in with your username and password.");
    }

    @Test
    public void testErrorMappingsFailureDefaultMessage() {
        AmsResponse amsResponse = new AmsResponse();
        ResponseHeader amsHeader = new ResponseHeader();
        amsHeader.setStatus(ResponseHeader.FAILURE_STATUS);
        amsHeader.setErrorCode("22282");
        amsResponse.setHeader(amsHeader);

        Response<AmsResponse> response = Response.success(amsResponse);

        Assert.assertFalse(mErrorMessageMapper.isSuccessful(response));

        MvpView mvpView = mock(MvpView.class);
        mErrorMessageMapper.displayError(response, mvpView);
        verify(mvpView).showMessage("Sorry, but we are not able to process your request with the information you provided. Please check your information and try again. ");
    }

    @Test
    public void testErrorMappingsFailureNoCodeForError() {
        AmsResponse amsResponse = new AmsResponse();
        ResponseHeader amsHeader = new ResponseHeader();
        amsHeader.setStatus(ResponseHeader.FAILURE_STATUS);
        amsHeader.setErrorCode("222312");
        amsHeader.setMsg("Credit Card processor could not approve transaction with information provided.");
        amsResponse.setHeader(amsHeader);

        Response<AmsResponse> response = Response.success(amsResponse);

        Assert.assertFalse(mErrorMessageMapper.isSuccessful(response));

        MvpView mvpView = mock(MvpView.class);
        mErrorMessageMapper.displayError(response, mvpView);
        verify(mvpView).showMessage("Sorry, but we are not able to process your request with the information you provided. Please check your information and try again. ");
    }

    @Test
    public void testErrorMappingsSuccess() {
        AmsResponse amsResponse = new AmsResponse();
        ResponseHeader amsHeader = new ResponseHeader();
        amsHeader.setStatus(ResponseHeader.SUCCESS_STATUS);
        amsResponse.setHeader(amsHeader);

        Response<AmsResponse> response = Response.success(amsResponse);

        Assert.assertTrue(mErrorMessageMapper.isSuccessful(response));
    }

    @Test
    public void testErrorMappingsMapErrorWithNonSuccessStatusCode() {
        AmsResponse amsResponse = new AmsResponse();
        ResponseHeader amsHeader = new ResponseHeader();
        amsHeader.setStatus(ResponseHeader.FAILURE_STATUS);
        amsHeader.setErrorCode("1056");
        amsResponse.setHeader(amsHeader);

        Response<AmsResponse> response = Response.error(HttpURLConnection.HTTP_BAD_REQUEST,
                ResponseBody.create(MediaType.parse("application/json"),
                        GsonUtil.defaultGson().toJson(amsResponse)));

        Assert.assertFalse(mErrorMessageMapper.isSuccessful(response));

        MvpView mvpView = mock(MvpView.class);
        mErrorMessageMapper.displayError(response, mvpView);
        verify(mvpView).showMessage("The state and zip code you submitted do not match. (1056)");
    }

    @Test
    public void testErrorMappingsNoMappingForNonSuccessStatusCode() {
        AmsResponse amsResponse = new AmsResponse();
        ResponseHeader amsHeader = new ResponseHeader();
        amsHeader.setStatus(ResponseHeader.FAILURE_STATUS);
        amsHeader.setErrorCode("42432324");
        amsResponse.setHeader(amsHeader);

        Response<AmsResponse> response = Response.error(HttpURLConnection.HTTP_BAD_REQUEST,
                ResponseBody.create(MediaType.parse("application/json"),
                        GsonUtil.defaultGson().toJson(amsHeader)));

        Assert.assertFalse(mErrorMessageMapper.isSuccessful(response));

        MvpView mvpView = mock(MvpView.class);
        mErrorMessageMapper.displayError(response, mvpView);
        verify(mvpView).showWarning(R.string.unknown_server_error);
    }

    @Test
    public void testErrorMappingsNonJsonResponse() {
        Response response = Response.error(HttpURLConnection.HTTP_BAD_REQUEST,
                ResponseBody.create(MediaType.parse("application/json"), "This is an error message text that is not in JSON format."));

        Assert.assertFalse(mErrorMessageMapper.isSuccessful(response));

        MvpView mvpView = mock(MvpView.class);
        mErrorMessageMapper.displayError(response, mvpView);
        verify(mvpView).showWarning(R.string.unknown_server_error);
    }

    @Test
    public void testErrorMappingsJsonResponseWithUnexpectedFieldDataTypes() {
        Response<AmsResponse> response = Response.error(HttpURLConnection.HTTP_BAD_REQUEST,
                ResponseBody.create(MediaType.parse("application/json"), "{\"header\":{\"errorCode\": false,\"status\": 1}}"));
        Assert.assertFalse(mErrorMessageMapper.isSuccessful(response));

        MvpView mvpView = mock(MvpView.class);
        mErrorMessageMapper.displayError(response, mvpView);
        verify(mvpView).showWarning(R.string.unknown_server_error);
    }

    @Test
    public void testErrorMappingsJsonResponseWithUnexpectedJsonStructure() {
        Response<AmsResponse> response = Response.error(HttpURLConnection.HTTP_BAD_REQUEST,
                ResponseBody.create(MediaType.parse("application/json"), "{\"header\":10}"));
        Assert.assertFalse(mErrorMessageMapper.isSuccessful(response));

        MvpView mvpView = mock(MvpView.class);
        mErrorMessageMapper.displayError(response, mvpView);
        verify(mvpView).showWarning(R.string.unknown_server_error);
    }

    @Test
    public void testFailureWithNoErrorCode() {
        AmsResponse amsResponse = new AmsResponse();
        ResponseHeader amsHeader = new ResponseHeader();
        amsHeader.setStatus(ResponseHeader.FAILURE_STATUS);
        amsResponse.setHeader(amsHeader);

        Response<AmsResponse> response = Response.error(HttpURLConnection.HTTP_BAD_REQUEST,
                ResponseBody.create(MediaType.parse("application/json"), GsonUtil.defaultGson().toJson(amsResponse)));
        Assert.assertFalse(mErrorMessageMapper.isSuccessful(response));

        MvpView mvpView = mock(MvpView.class);
        mErrorMessageMapper.displayError(response, mvpView);
        verify(mvpView).showWarning(R.string.unknown_server_error);
    }
}
