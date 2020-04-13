package DataProvider;

import java.text.DateFormat;
import java.util.Date;

public class Formatter {

    public static String formatDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.LONG).format(date);
    }

    public static String formatDate(int year, int month, int day) {
        return new StringBuilder().append(year).append("-").append(month+1).append("-").append(day).toString();
    }

}
