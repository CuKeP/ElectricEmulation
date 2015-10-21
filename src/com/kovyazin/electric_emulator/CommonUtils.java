package com.kovyazin.electric_emulator;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Vyacheslav on 09.10.2015.
 */
public class CommonUtils {

    public static void saveProperties(String keyName, String keyValue) throws  IOException {
        Properties prop = new Properties();
        prop.load(new InputStreamReader(new FileInputStream("example.properties"), "UTF-8"));
        File cfg = new File("example.properties");
        prop.put(keyName, keyValue);
        savePropertiesAll(cfg, prop);
    }

    public static void savePropertiesAll(File f, Properties p) throws IOException {
        OutputStream os = new FileOutputStream(f);
        p.store(os, null);
    }

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

    public static byte[] getID(String s) {
        byte out[] = new byte[s.length()];
        for (int j = 0; j < s.length(); j++) {
            out[j] = (byte) s.charAt(j);
        }
        System.out.println("ret=" + out[0]);
        return out;
    }
}