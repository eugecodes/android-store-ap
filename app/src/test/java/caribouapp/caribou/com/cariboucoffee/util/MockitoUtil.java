package caribouapp.caribou.com.cariboucoffee.util;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.mockito.stubbing.Stubber;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class MockitoUtil {

    private MockitoUtil() {
    }

    public static void mockEnqueueArray(Call mockCallback, String jsonResponseFileName, Type typeOfT) {
        doAnswer(invocation -> {
            Callback<List> callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockCallback, Response.success(GsonUtil.readArrayFromClasspath(jsonResponseFileName, typeOfT)));
            return null;
        }).when(mockCallback).enqueue(any(Callback.class));
    }

    public static <T> void mockEnqueueWithObject(Call mockCallback, T responseObject) {
        doAnswer(invocation -> {
            Callback<T> callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockCallback, Response.success(responseObject));
            return null;
        }).when(mockCallback).enqueue(any(Callback.class));
    }

    public static <T> void mockEnqueueWithSuccessEmptyResponse(Call mockCallback) {
        doAnswer(invocation -> {
            Callback<T> callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockCallback, Response.success(null));
            return null;
        }).when(mockCallback).enqueue(any(Callback.class));
    }

    public static <T> void mockEnqueueWithError(Call mockCallback, int responseCode) {
        doAnswer(invocation -> {
            Callback<T> callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockCallback, Response.error(responseCode, ResponseBody.create(MediaType.parse("plain/text"), "")));
            return null;
        }).when(mockCallback).enqueue(any(Callback.class));
    }

    public static <T> void mockEnqueueWithError(Call mockCallback, int responseCode, String responseMessage) {
        doAnswer(invocation -> {
            Callback<T> callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockCallback, Response.error(responseCode, ResponseBody.create(MediaType.parse("plain/text"), responseMessage)));
            return null;
        }).when(mockCallback).enqueue(any(Callback.class));
    }

    public static <T> void mockEnqueueToFail(Call mockCallback, Throwable throwable) {
        doAnswer(invocation -> {
            Callback<T> callback = invocation.getArgument(0, Callback.class);
            callback.onFailure(mockCallback, throwable);
            return null;
        }).when(mockCallback).enqueue(any(Callback.class));
    }

    public static <T> void mockEnqueue(Call mockCallback, String jsonResponseFileName, Type type) {
        doAnswer(invocation -> {
            Callback<T> callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockCallback, Response.success(GsonUtil.readObjectFromClasspath(jsonResponseFileName, type)));
            return null;
        }).when(mockCallback).enqueue(any(Callback.class));
    }

    public static <T> void mockAsynchronousRetrofitRequest(Call mockCallback, String jsonResponseFileName, Class<T> destinationClass) throws IOException {
        Response<T> response = Response.success(GsonUtil.readObjectFromClasspath(jsonResponseFileName, destinationClass));
        when(mockCallback.execute()).thenReturn(response);
    }

    public static <T> void mockAsynchronousRetrofitRequest(Call mockCallback, Response<T> response) throws IOException {
        when(mockCallback.execute()).thenReturn(response);
    }

    public static <T> void mockExecute(Call mockCallback, String jsonResponseFileName, Class<T> destinationClass) {
        try {
            doAnswer(invocation -> Response.success(GsonUtil.readObjectFromClasspath(jsonResponseFileName, destinationClass)))
                    .when(mockCallback).execute();
        } catch (IOException e) {
            Assert.fail();
        }
    }

    public static void mockCSV(Call mockCallback, String classpathResourcePath) {
        doAnswer(invocation -> {
            Callback<ResponseBody> callback = invocation.getArgument(0, Callback.class);
            callback.onResponse(mockCallback, Response.success(ResponseBody.create(MediaType.parse("text/plain"),
                    StringUtils.fromClasspathResource(classpathResourcePath))));
            return null;
        }).when(mockCallback).enqueue(any(Callback.class));
    }

    public static Stubber answerResultCallback(String jsonResponseFileName, Type arrayType) {
        return doAnswer(invocation -> {
            for (Object arg : invocation.getArguments()) {
                if (!(arg instanceof ResultCallback)) {
                    continue;
                }
                ResultCallback resultCallback = (ResultCallback) arg;
                resultCallback.onSuccess(GsonUtil.readArrayFromClasspath(jsonResponseFileName, arrayType));
            }
            return null;
        });
    }
}
