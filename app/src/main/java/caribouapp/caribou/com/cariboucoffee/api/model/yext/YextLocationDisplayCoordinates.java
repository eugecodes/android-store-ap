package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

public class YextLocationDisplayCoordinates {


    @SerializedName("latitude")
    private double mYextDisplayLat;

    @SerializedName("longitude")
    private double mYextDisplayLng;

    public double getYextDisplayLat() {
        return mYextDisplayLat;
    }

    public void setYextDisplayLat(double yextDisplayLat) {
        mYextDisplayLat = yextDisplayLat;
    }

    public double getYextDisplayLng() {
        return mYextDisplayLng;
    }

    public void setYextDisplayLng(double yextDisplayLng) {
        mYextDisplayLng = yextDisplayLng;
    }
}
