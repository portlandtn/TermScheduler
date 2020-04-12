package DataProvider;

import java.text.DateFormat;
import java.util.Date;

public class Formatter {

    public static String formatDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.LONG).format(date);
    }

}
