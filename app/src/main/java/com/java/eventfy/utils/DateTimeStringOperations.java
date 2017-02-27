package com.java.eventfy.utils;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.util.Date;
import java.util.Locale;

/**
 * Created by swapnil on 2/25/17.
 */
public class DateTimeStringOperations {
    private static DateTimeStringOperations ourInstance = new DateTimeStringOperations();

    public static DateTimeStringOperations getInstance() {
        return ourInstance;
    }

    private DateTimeStringOperations() {
    }


    public String getDateTimeString(String dateStr, String timeZone){
        DateTime dateTime = convertStringToDateTime(dateStr, timeZone);

        DateTime.Property pDoW = dateTime.dayOfWeek();
        String day = pDoW.getAsText(Locale.ENGLISH);

        pDoW = dateTime.monthOfYear();
        String month = pDoW.getAsText(Locale.ENGLISH);

        String dateTimeStr = day+", "+month+" - "+dateTime.getDayOfMonth()+" - "+dateTime.getYear();

        return dateTimeStr;

    }


    public DateTime convertStringToDateTime(String date, String timeZone){
        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("MM-dd-yyyy HH:mm").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").getParser(),
                DateTimeFormat.forPattern("MM/dd/yyyy HH:mm").getParser(),
                DateTimeFormat.forPattern("yyyy/MM/dd HH:mm").getParser()
        };



        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .append(null, parsers)
                .toFormatter()
                .withZone(DateTimeZone.forID(timeZone));

        return formatter.parseDateTime(date);

    }


    public String convertStringToDateTime(String dateTime){

        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("yyyy-MM-dd- HH:mm").getParser()
        };

        DateTime dTime = new DateTime();

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm")
                .toFormatter()
                .withZone(dTime.getZone());

        dTime =  formatter.parseDateTime(dateTime);

        Date date =  dTime.toDate();

        Log.e("date obj = ", "  :   "+date);

        return date.toString();
    }


}
