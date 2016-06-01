package com.kovyazin.electric_emulator;

//import com.sun.java.util.jar.pack.Instruction;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Vyacheslav on 09.10.2015.
 */
public class CommonUtils {

    public static void saveProperties(String keyName, String keyValue) throws IOException {
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

    public static void convertStreamToByte(byte[] inputStream) {
        for (int i = 0; i < inputStream.length - 4; i++) {
            if ((inputStream[i] == -1) & (inputStream[i + 1] == -1) & (inputStream[i + 2] == -1) & (inputStream[i + 3] == -1)
                    & (inputStream[i + 4] == -1)) {
                break;
            } else {
                Server.inputBytes.add((int) inputStream[i]);
            }

        }
//        while ((inputStream[i] != -1)){
//            Server.inputBytes.add((int) inputStream[i]);
//            i++;
//        }

        for (int j = 0; j < 128; j++) {
            Server.longByte[j] = -1;
        }
    }

    public static int controlSum(byte b[]) {
        int KC = 255;
        int k;
        for (int i = 0; i < b.length; i++) {
            k = b[i];
            KC = KC ^ k;
        }
        return KC;
    }

    public static int controlSumForProfiles(byte b[]) {
        int KC = 0;
        int k;
        for (int i = 0; i < b.length; i++) {
//            k = b[i];
            KC =  KC + b[i];
        }
        return KC;
    }

    public static byte[] getID(String s) {
        byte out[] = new byte[s.length()];
        for (int j = 0; j < s.length(); j++) {
            out[j] = (byte) s.charAt(j);
        }
        System.out.println("ret=" + out[0]);
        return out;
    }

    public static int[] selectEnergy(int nMass, int nMonth, int nTarif) {
        int[] result = {0, 0, 0, 0};

        try {
            Connection con = null;
            Properties connInfo = new Properties();
            connInfo.put("user", "");
            connInfo.put("password", "");
            //это обязательно, иначе русские символы не будут восприниматься, в базе кирилица...
            connInfo.put("charSet", "Cp1251");
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String db = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=C:\\Program Files\\kovyazin\\ElectricEmulator\\db.mdb";
            con = DriverManager.getConnection(db, connInfo);

            System.out.println("Connection ok.");

            Statement sta = con.createStatement();
            ResultSet res = null;

            switch (nMass) {
                case 0:
                    if (nTarif == 0) {
                        res = sta.executeQuery("SELECT Sum(`A+`),Sum(`A-`),Sum(`R+`),Sum(`R-`) FROM EnergyFromReset");
                    } else {
                        res = sta.executeQuery("SELECT `A+`,`A-`,`R+`,`R-` FROM EnergyFromReset where `N_tarif` = " + nTarif);
                    }
                    break;
                case 1:
                    if (nTarif == 0) {
                        res = sta.executeQuery("SELECT Sum(`A+`),Sum(`A-`),Sum(`R+`),Sum(`R-`) FROM EnergyCurrentYear");
                    } else {
                        res = sta.executeQuery("SELECT `A+`,`A-`,`R+`,`R-` FROM EnergyCurrentYear where `N_tarif` = " + nTarif);
                    }
                    break;
                case 2:
                    if (nTarif == 0) {
                        res = sta.executeQuery("SELECT Sum(`A+`),Sum(`A-`),Sum(`R+`),Sum(`R-`) FROM EnergyPreviousYear");
                    } else {
                        res = sta.executeQuery("SELECT `A+`,`A-`,`R+`,`R-` FROM EnergyPreviousYear where `N_tarif` = " + nTarif);
                    }
                    break;
                case 3:
                    if (nTarif == 0) {
                        res = sta.executeQuery("SELECT Sum(`A+`),Sum(`A-`),Sum(`R+`),Sum(`R-`) FROM EnergyFromMonth");
                    } else {
                        res = sta.executeQuery("SELECT `A+`,`A-`,`R+`,`R-` FROM EnergyFromMonth where `N_tarif` = " + nTarif);
                    }
                    break;
                case 4:
                    if (nTarif == 0) {
                        res = sta.executeQuery("SELECT Sum(`A+`),Sum(`A-`),Sum(`R+`),Sum(`R-`) FROM EnergyCurrentDay");
                    } else {
                        res = sta.executeQuery("SELECT `A+`,`A-`,`R+`,`R-` FROM EnergyCurrentDay where `N_tarif` = " + nTarif);
                    }
                    break;
                case 5:
                    if (nTarif == 0) {
                        res = sta.executeQuery("SELECT Sum(`A+`),Sum(`A-`),Sum(`R+`),Sum(`R-`) FROM EnergyPreviousDay");
                    } else {
                        res = sta.executeQuery("SELECT `A+`,`A-`,`R+`,`R-` FROM EnergyPreviousDay where `N_tarif` = " + nTarif);
                    }
                    break;
            }

            System.out.println("List: ");
            while (res.next()) {
                //System.out.println("  " +res.getString(1) + " | "+res.getString(2) + " | "+res.getString(3) + " | "+res.getString(4));// + " | "+res.getString(5));
//                System.out.println(res.getString(1));
                result[0] = (int) Float.parseFloat(res.getString(1));
                result[1] = (int) Float.parseFloat(res.getString(2));
                result[2] = (int) Float.parseFloat(res.getString(3));
                result[3] = (int) Float.parseFloat(res.getString(4));
            }
            res.close();
            con.close();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
        return result;
    }
}