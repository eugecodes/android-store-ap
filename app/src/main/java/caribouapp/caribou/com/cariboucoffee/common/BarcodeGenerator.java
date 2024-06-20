package caribouapp.caribou.com.cariboucoffee.common;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.PDF417Writer;

import java.util.HashMap;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

/**
 * Created by jmsmuy on 1/2/18.
 */

public final class BarcodeGenerator {

    private static final String TAG = BarcodeGenerator.class.getSimpleName();

    private BarcodeGenerator() {
    }

    public static Bitmap generatePDF417Code(String code, int width) {
        BitMatrix bitMatrix;
        Writer writer = new PDF417Writer();
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            bitMatrix = writer.encode(code, BarcodeFormat.PDF_417, 1, 1, hints);
            int scaleFactor = width / bitMatrix.getWidth();
            Bitmap bmp = Bitmap.createBitmap(bitMatrix.getWidth() * scaleFactor, bitMatrix.getHeight() * scaleFactor, Bitmap.Config.RGB_565);

            Log.d(TAG, "Bitmap size: " + bmp.getWidth() + "x" + bmp.getHeight());

            for (int x = 0; x < bmp.getWidth(); x++) {
                for (int y = 0; y < bmp.getHeight(); y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x / scaleFactor, y / scaleFactor) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (Exception e) {
            Log.e(TAG, new LogErrorException("Error creating barcode " + code));
        }
        return null;
    }
}
