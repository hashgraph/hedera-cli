package com.hedera.cli.hedera.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Utils {

    public Instant dateToMilliseconds(String[] dateInString) throws ParseException {
        StringBuilder appendedString = new StringBuilder();
        System.out.println("The date from cli is: ");
        for (String date : dateInString) {
            appendedString.append(date).append(" ");
        }
        SimpleDateFormat sdfWithTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date dateWithTime;
        dateWithTime = sdfWithTime.parse(appendedString.toString());
        Instant instant = dateWithTime.toInstant();
        System.out.println("Date of File Expiry is: " + instant);
        return instant;
    }
}
