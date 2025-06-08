package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String format(Date date) {
        return date == null ? "" : DATE_FORMAT.format(date);
    }

    public static Date parse(String dateString) throws java.text.ParseException {
        return dateString == null || dateString.isEmpty() ? null : DATE_FORMAT.parse(dateString);
    }
}