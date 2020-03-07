package com.cradle.neptune.utilitiles;

import android.util.Log;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getDateString(ZonedDateTime date) {
        if (date == null) {
            return "";
        }
        ZonedDateTime now = ZonedDateTime.now();

        DateTimeFormatter formatter;
        if (now.toLocalDate().equals(date.toLocalDate())) {
            // Today: omit the date
            formatter = DateTimeFormatter.ofPattern("h:mm a");
        } else if (now.getYear() == date.getYear()) {
            // This year: omit the year
            formatter = DateTimeFormatter.ofPattern("MMM d '@' h:mm a");
        } else {
            // Full date
            formatter = DateTimeFormatter.ofPattern("MMM d, yyyy '@' h:mm a");
        }

        return date.format(formatter);
    }

    public static String getConciseDateString(ZonedDateTime date) {
        ZonedDateTime now = ZonedDateTime.now();
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatter;
        if (now.toLocalDate().equals(date.toLocalDate())) {
            // Today: omit the date
            formatter = DateTimeFormatter.ofPattern("h:mm a");
        } else if (now.getYear() == date.getYear()) {
            // This year: omit the year
            formatter = DateTimeFormatter.ofPattern("MMM d '@' h a");
        } else {
            // Full date
            formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        }

        return date.format(formatter);
    }

    public static String getFullDateString(ZonedDateTime date) {
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatter;
        formatter = DateTimeFormatter.ofPattern("MMM d, yyyy '@' h:mm a");
        return date.format(formatter);
    }

    public static String getISODate(ZonedDateTime date) {
        // example:  '2011-12-03T10:15:30'.

        if (date == null) {
            return "";
        }

        return date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static String getISODateForFilename(ZonedDateTime date) {
        return getISODate(date).replace(":", ".");
    }

    public static ZonedDateTime getZoneTimeFromString(String date){
        if (date==null || date.equals("")|| date.toLowerCase().equals("null")){
            return null;
        }

        ZonedDateTime zonedDateTime = null;
        try {
            zonedDateTime = ZonedDateTime.parse(date);
        }catch (Exception e){
            Log.d("bugg", "date: "+ date);
            try {
                String date1=date.replace("T"," ");
                Log.d("bugg","new string: "+date1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date2 = simpleDateFormat.parse(date1);
                zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(date2.getTime()), ZoneId.systemDefault());
                Log.d("bugg","time:zone: "+zonedDateTime.toString());
            }catch (Exception e1){
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return zonedDateTime;
    }
}
