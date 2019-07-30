package com.hedera.cli.hedera.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

public class Utils {

    public Instant dateToMilliseconds(String[] dateInString) throws ParseException {

        System.out.println(Arrays.asList(dateInString));

        StringBuilder appendedString = new StringBuilder();
        System.out.println("The date from cli is: ");
        for (String date : dateInString) {
            System.out.println(date);
            appendedString.append(date).append(" ");
        }

        System.out.println("Appended string here is....?");
        System.out.println(appendedString);
        SimpleDateFormat sdfWithTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        Date dateWithTime;
        dateWithTime = sdfWithTime.parse(appendedString.toString());
        System.out.println("Day Date Time : " + dateWithTime);

        Instant instant = dateWithTime.toInstant();
        System.out.println("Date of File Expiry is: " + instant);
        return instant;
    }
}
