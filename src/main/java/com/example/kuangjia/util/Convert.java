package com.example.kuangjia.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Convert {
    public Convert() {
    }

    public static int strToInt(String str, int defaultValue) {
        int Result = defaultValue;

        try {
            Result = Integer.parseInt(str);
        } catch (Exception var4) {
            ;
        }

        return Result;
    }

    public static long strToLong(String str, long defaultValue) {
        long Result = defaultValue;

        try {
            Result = Long.parseLong(str);
        } catch (Exception var6) {
            ;
        }

        return Result;
    }

    public static float strToFloat(String str, float defaultValue) {
        float Result = defaultValue;

        try {
            Result = Float.parseFloat(str);
        } catch (Exception var4) {
            ;
        }

        return Result;
    }

    public static double strToDouble(String str, double defaultValue) {
        double Result = defaultValue;

        try {
            Result = Double.parseDouble(str);
        } catch (Exception var6) {
            ;
        }

        return Result;
    }

    public static boolean strToBoolean(String str, boolean defaultValue) {
        boolean Result = defaultValue;

        try {
            Result = Boolean.parseBoolean(str);
        } catch (Exception var4) {
            ;
        }

        return Result;
    }

    public static Date strToDate(String str, Date defaultValue) {
        return strToDate(str, "yyyy-MM-dd HH:mm:ss", defaultValue);
    }

    public static Date strToDate(String str, String format, Date defaultValue) {
        Date Result = defaultValue;
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);

        try {
            Result = formatter.parse(str);
        } catch (Exception var6) {
            ;
        }

        return Result;
    }

    public static String dateToStr(Date date, String defaultValue) {
        return dateToStr(date, "yyyy-MM-dd HH:mm:ss", defaultValue);
    }

    public static String dateToStr(Date date, String format, String defaultValue) {
        String Result = defaultValue;
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);

        try {
            Result = formatter.format(date);
        } catch (Exception var6) {
            ;
        }

        return Result;
    }

    public static String strToStr(String str, String defaultValue) {
        String Result = defaultValue;
        if (str != null && !str.isEmpty()) {
            Result = str;
        }

        return Result;
    }

    public static java.sql.Date dateToSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static Date sqlDateToDate(java.sql.Date date) {
        return new Date(date.getTime());
    }

    public static Timestamp dateToSqlTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    public static Date qlTimestampToDate(Timestamp date) {
        return new Date(date.getTime());
    }

    public static int strtoAsc(String st) {
        byte[] gc = st.getBytes();
        int asnum = gc[0];
        return asnum;
    }

    public static char intToChar(int backnum) {
        char stchar = (char)backnum;
        return stchar;
    }
}

