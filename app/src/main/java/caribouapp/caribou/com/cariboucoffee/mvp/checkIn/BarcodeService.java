package caribouapp.caribou.com.cariboucoffee.mvp.checkIn;

import android.graphics.Bitmap;

/**
 * Created by asegurola on 2/19/18.
 */

public interface BarcodeService {
    Bitmap generateBarcode(String barcodeData);
}
