package caribouapp.caribou.com.cariboucoffee.mvp.checkIn;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.common.BarcodeGenerator;
import caribouapp.caribou.com.cariboucoffee.util.Log;

/**
 * Created by asegurola on 2/19/18.
 */

public class BarcodeServiceImpl implements BarcodeService {

    private static final String TAG = BarcodeServiceImpl.class.getSimpleName();

    private Context mContext;

    public BarcodeServiceImpl(Application application) {
        mContext = application;
    }

    @Override
    public Bitmap generateBarcode(String barcodeData) {
        File barcodeFile = getCachedImageForBarcode(barcodeData);

        if (!barcodeFile.exists()) {
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int widthPixels = displayMetrics.widthPixels - mContext.getResources().getDimensionPixelSize(R.dimen.medium_spacing) * 2;
            Bitmap bitmap = BarcodeGenerator.generatePDF417Code(barcodeData, widthPixels);
            saveBitmapAsPng(bitmap, barcodeFile);
            return bitmap;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(barcodeFile.getPath(), options);
    }

    private File getCachedImageForBarcode(String barcodeData) {
        return new File(mContext.getCacheDir(), "barcode_v2_" + barcodeData + ".png");
    }

    private void saveBitmapAsPng(Bitmap bitmap, File outputFile) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            Log.e(TAG, e);
            outputFile.delete();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e);
                outputFile.delete();
            }
        }
    }
}
