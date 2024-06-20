package caribouapp.caribou.com.cariboucoffee.messages;

import com.google.gson.annotations.SerializedName;

public class ErrorMessage {

    @SerializedName("Source")
    private ErrorSource mSource;

    @SerializedName("UserMessage")
    private String mUserMessage;

    @SerializedName("SystemMessage")
    private String mSystemMessage;

    @SerializedName("ID")
    private String mId;


    public ErrorSource getSource() {
        return mSource;
    }

    public void setSource(ErrorSource source) {
        mSource = source;
    }

    public String getUserMessage() {
        return mUserMessage;
    }

    public void setUserMessage(String userMessage) {
        mUserMessage = userMessage;
    }

    public String getSystemMessage() {
        return mSystemMessage;
    }

    public void setSystemMessage(String systemMessage) {
        mSystemMessage = systemMessage;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
