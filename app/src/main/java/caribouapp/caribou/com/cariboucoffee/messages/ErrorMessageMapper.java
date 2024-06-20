package caribouapp.caribou.com.cariboucoffee.messages;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.api.model.ResponseHeader;
import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import retrofit2.Response;

/**
 * Created by andressegurola on 11/3/17.
 */
@Singleton
public class ErrorMessageMapper {

    private static final String TAG = ErrorMessageMapper.class.getSimpleName();

    private static final String DEFAULT_ERROR_CODE = "9999";

    private Map<String, ErrorMessage> mIdToErrorMessage;

    public ErrorMessageMapper(Reader mappingsJsonData) {
        mIdToErrorMessage = new HashMap<>();

        try {
            List<ErrorMessage> errorMessageList = GsonUtil.readArrayFromClasspath(mappingsJsonData,
                    new TypeToken<ArrayList<ErrorMessage>>() {
                    }.getType());
            for (ErrorMessage errorMessage : errorMessageList) {
                mIdToErrorMessage.put(errorMessage.getId(), errorMessage);
            }
        } catch (RuntimeException e) {
            Log.e(TAG, new LogErrorException("Error parsing mappings", e));
        }
    }


    private String mapErrorByCode(String errorCode) {
        ErrorMessage errorMessage = mIdToErrorMessage.get(errorCode);

        if (errorMessage == null) {
            return null;
        }

        Log.d(TAG, "ErrorMessage mapping found for code: " + errorCode + " -> " + errorMessage.getUserMessage());
        return errorMessage.getUserMessage();
    }

    public boolean isSuccessful(Response<? extends ResponseWithHeader> response) {
        return response.isSuccessful() // Std. Success case (Header with "success" as state
                && isSuccessful(response.body());
    }

    public boolean isSuccessful(ResponseWithHeader response) {
        return (response.getHeader() == null // Note: this is because some AMS services are not working correctly
                || response.getHeader().getStatus() != null
                && response.getHeader().getStatus().equals(ResponseHeader.SUCCESS_STATUS)); // Std. Success case (Header with "success" as state
    }

    public void displayError(Response<? extends ResponseWithHeader> response, MvpView view) {
        if (response.isSuccessful()) {
            ResponseWithHeader responseWithHeader = response.body();
            displayError(responseWithHeader, view);
        } else {
            try {
                String errorBodyContent = response.errorBody().string();
                ResponseWithHeader responseWithHeader = GsonUtil.defaultGson().fromJson(errorBodyContent, ResponseWithHeader.class);
                displayError(responseWithHeader, view);
            } catch (JsonParseException | IOException e) {
                Log.e(TAG, new LogErrorException("Problems parsing errorBody.", e));
                view.showWarning(R.string.unknown_server_error);
            }
        }
    }

    public void displayError(ResponseWithHeader responseWithHeader, MvpView mvpView) {
        try {
            String mappedErrorMessage = mapErrorMessage(responseWithHeader);
            if (mappedErrorMessage == null) {
                mvpView.showWarning(R.string.unknown_server_error);
                return;
            }
            mvpView.showMessage(mappedErrorMessage);
        } catch (RuntimeException e) {
            mvpView.showWarning(R.string.unknown_server_error);
        }
    }

    public String mapErrorMessage(ResponseWithHeader responseWithHeader) {
        ResponseHeader responseHeader = responseWithHeader.getHeader();
        if (responseHeader == null || !ResponseHeader.FAILURE_STATUS.equalsIgnoreCase(responseHeader.getStatus())) {
            throw new IllegalStateException("Map error called with status != Failure");
        }

        String errorCode = responseHeader.getErrorCode();
        if (errorCode == null) {
            throw new IllegalStateException("Failure with no error code.");
        }

        Log.d(TAG, "Header Failure errorCode: " + errorCode);
        String mappedErrorMessage = mapErrorByCode(errorCode);

        if (mappedErrorMessage == null) {
            Log.e(TAG, new LogErrorException("Missing error mapping for error code: " + errorCode));
            // Try to find a default error message for the source system. Those are coded with the 9999 code.
            mappedErrorMessage = mapErrorByCode(DEFAULT_ERROR_CODE);
        }

        if (mappedErrorMessage == null) {
            throw new IllegalStateException("No error mapping found.");
        }

        return mappedErrorMessage;
    }
}
