package caribouapp.caribou.com.cariboucoffee.util;

/**
 * Created by asegurola on 5/3/18.
 */

public class LogErrorException extends Exception {
    public LogErrorException(String message) {
        super(message);
    }

    public LogErrorException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
