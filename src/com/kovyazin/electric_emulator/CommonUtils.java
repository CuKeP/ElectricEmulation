package com.kovyazin.electric_emulator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vyacheslav on 09.10.2015.
 */
public class CommonUtils {

    public static int getDateDelta(String date) {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        long date1;
        try {
            date1 = df.parse(date).getTime();
        } catch (ParseException e) {
            return 0;
        }
        long date2 = new Date().getTime();
        long date3 = date2 - date1;
        return (int) (date3 / 1000 / 60 / 60);
    }
}
