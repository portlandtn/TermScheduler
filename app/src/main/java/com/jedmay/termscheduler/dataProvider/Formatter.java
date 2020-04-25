package com.jedmay.termscheduler.dataProvider;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Formatter {

    public static String formatDate(Date date) {
        return date != null ? DateFormat.getDateInstance(DateFormat.LONG).format(date) : "";
    }

    public static Date convertIntegersToDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);

        return cal.getTime();
    }

}
