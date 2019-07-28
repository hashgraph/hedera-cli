package com.hedera.cli.hedera.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public long DateToMilliSeconds(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(date.toString());
        String dateInString = "22-06-2019 14:44:44";
        Date date1 = sdf.parse(dateInString);
        System.out.println(dateInString);
        System.out.println("Date - Time in milliseconds : " + date1.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        System.out.println("Calendar - Time in milliseconds : " + calendar.getTimeInMillis());
        return calendar.getTimeInMillis();
    }
}
