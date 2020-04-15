package DataProvider;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

public class Formatter {

    public static String formatDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.LONG).format(date);
    }

    public static String formatDate(int year, int month, int day) {
        return year + "-" + (month + 1) + "-" + day;
    }

    public static Date convertDateToJavaSQL(int year, int month, int day) {
        return java.sql.Date.valueOf(formatDate(year, month, day));
    }

}
