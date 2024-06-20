package caribouapp.caribou.com.cariboucoffee.common;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import caribouapp.caribou.com.cariboucoffee.util.Log;
import okhttp3.ResponseBody;

/**
 * Based on https://futurestud.io/tutorials/retrofit-2-how-to-download-files-from-server
 */
public class DownloadFileAsyncTask extends AsyncTask<ResponseBody, Void, Boolean> {

    private static final String TAG = DownloadFileAsyncTask.class.getSimpleName();

    private static final int BUFFER_SIZE = 4096;

    private File mDestinationFile;

    private ResultCallback<File> mCallback;

    public DownloadFileAsyncTask(File destinationFile, ResultCallback<File> resultCallback) {
        mDestinationFile = destinationFile;
        mCallback = resultCallback;
    }

    @Override
    protected Boolean doInBackground(ResponseBody... responseBodies) {
        mDestinationFile.getParentFile().mkdirs();
        return writeResponseBodyToDisk(responseBodies[0]);
    }

    @Override
    protected void onPostExecute(Boolean fileWrittenToDisk) {
        if (fileWrittenToDisk) {
            mCallback.onSuccess(mDestinationFile);
        } else {
            mCallback.onError(new IOException("Error downloading file " + mDestinationFile));
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] buffer = new byte[BUFFER_SIZE];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(mDestinationFile);

                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                Log.e("Error writing file to disk", e);
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.e("Error closing streams", e);
            return false;
        }
    }
}
