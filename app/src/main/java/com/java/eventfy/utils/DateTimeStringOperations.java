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
import java.util.TimeZone;

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

        int hour = dateTime.getHourOfDay();

        String amOrPm = null;
        if(hour==12)
            amOrPm = "PM";
        else if(hour == 24){
            hour = 12;
            amOrPm = "AM";
        }
        else if(hour>13){
            hour = hour%12;
            amOrPm = "PM";
        }
        else
            amOrPm = "AM";

        String dateTimeStr = day.substring(0,3)+", "+month.substring(0,3)+" "+dateTime.getDayOfMonth()+
                " at "+dateTime.getHourOfDay()+":"+dateTime.getMinuteOfHour();

        return dateTimeStr;

    }

    public String getDateTimeStringForFb(String dateStr){
        DateTime dateTime = new DateTime(dateStr);

        DateTime.Property pDoW = dateTime.dayOfWeek();
        String day = pDoW.getAsText(Locale.ENGLISH);

        pDoW = dateTime.monthOfYear();
        String month = pDoW.getAsText(Locale.ENGLISH);

        int hour = dateTime.getHourOfDay();

        String amOrPm = null;
        if(hour==12)
            amOrPm = "PM";
        else if(hour == 24){
            hour = 12;
            amOrPm = "AM";
        }
        else if(hour>13){
            hour = hour%12;
            amOrPm = "PM";
        }
        else
            amOrPm = "AM";

        String dateTimeStr = day.substring(0,3)+", "+month.substring(0,3)+" "+dateTime.getDayOfMonth()+
                " at "+hour+":"+dateTime.getMinuteOfHour()+" "+amOrPm;

        return dateTimeStr;

    }


    public String getDateString(String dateStr){
        DateTime dateTime = convertStringToDateTimeObj(dateStr);

        DateTime.Property pDoW = dateTime.monthOfYear();
        String month = pDoW.getAsText(Locale.ENGLISH);

        String dateTimeStr = month.substring(0,3)+", "+dateTime.getDayOfMonth()+" "+dateTime.getYear();

        return dateTimeStr;

    }




    public DateTime convertStringToDateTime(String date, String timeZone){
        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("MM-dd-yyyy HH:mm").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").getParser(),
                DateTimeFormat.forPattern("MM/dd/yyyy HH:mm").getParser(),
                DateTimeFormat.forPattern("yyyy/MM/dd HH:mm").getParser(),
                DateTimeFormat.forPattern("MM-dd-yyyy").getParser(),
                DateTimeFormat.forPattern("yyyy-dd-MM").getParser(),
                DateTimeFormat.forPattern("MM-dd-yyyy").getParser(),
                DateTimeFormat.forPattern("MM/dd/yyyy").getParser()
        };

        DateTime out = null;
        //try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .append(null, parsers)
                    .toFormatter()
                    .withZone(DateTimeZone.forID(timeZone));
            out = formatter.parseDateTime(date);

//        }catch (Exception e){
//
//        }
        return out;

    }

    public boolean checkUSerIs18Plus(String userDob){


        org.joda.time.DateTime dateTimeNow = new org.joda.time.DateTime(DateTimeZone.forID(TimeZone.getDefault().getID()));
        dateTimeNow = dateTimeNow.plusYears(-13);
        org.joda.time.DateTime userDobDateTime = convertStringToDateTime(userDob, TimeZone.getDefault().getID());

        boolean b = true;
        if(dateTimeNow.isBefore(userDobDateTime)){
            b= false;
        }
        else
        b = true;

        Log.e("user is 13+ ", " ********* "+b);
        return b;
    }

    public String convertStringToDateTime(String dateTime){

        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd").getParser()
        };

        DateTime dTime = new DateTime();

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .append(null, parsers)
                .toFormatter()
                .withZone(dTime.getZone());

        dTime =  formatter.parseDateTime(dateTime);

        Date date =  dTime.toDate();

        Log.e("date obj = ", "  :   "+date);

        return date.toString();
    }


    public DateTime convertStringToDateTimeObj(String dateTime){

        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd").getParser()
        };

        DateTime dTime = new DateTime();

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .append(null, parsers)
                .toFormatter()
                .withZone(dTime.getZone());

        dTime =  formatter.parseDateTime(dateTime);

        Date date =  dTime.toDate();

        Log.e("date obj = ", "  :   "+date);

        return dTime;
    }


}
