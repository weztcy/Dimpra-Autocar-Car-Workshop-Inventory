package com.workshop.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("id", "ID"));
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDate(Date date) {
        if (date == null) return "-";
        return DATE_FORMAT.format(date);
    }

    public static String formatDate(java.util.Date date) {
        if (date == null) return "-";
        return DATE_FORMAT.format(date);
    }

    public static String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) return "-";
        return DATETIME_FORMAT.format(timestamp);
    }

    public static String toIsoDate(java.util.Date date) {
        if (date == null) return "";
        return ISO_DATE_FORMAT.format(date);
    }
}
