package caribouapp.caribou.com.cariboucoffee.messages;

import com.google.gson.annotations.SerializedName;

public class ErrorSource {
    @SerializedName("Title")
    private String mTitle;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
