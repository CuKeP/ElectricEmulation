package com.kovyazin.electric_emulator.request_handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Slavyan on 21.04.16.
 */
public class GenerationEnergy {

    public static void start() {
        Timer timer = new java.util.Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    Calendar calendar = Calendar.getInstance();

                    String dateStr = new String();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

                    Connection con = null;
                    Properties connInfo = new Properties();
                    connInfo.put("user", "");
                    connInfo.put("password", "");
                    //это обязательно, иначе русские символы не будут восприниматься, в базе кирилица...
                    connInfo.put("charSet", "Cp1251");
                    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                    String db = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=C:\\Program Files\\kovyazin\\ElectricEmulator\\db.mdb";
                    con = DriverManager.getConnection(db, connInfo);

                    Statement sta = con.createStatement();
                    dateStr = dateFormat.format(calendar.getTime());
                    int A1,A2,R1,R2;
                    A1 = (int) (Math.random() * 4);
                    A2 = (int) (Math.random() * 4);
                    R1 = (int) (Math.random() * 4);
                    R2 = (int) (Math.random() * 4);

                    sta.execute("INSERT INTO GenerationTimeCY ( `hour`, `date1`, `A+`, `A-`, `R+`, `R-` ) " +
                            "VALUES (" + calendar.get(Calendar.HOUR_OF_DAY) + ",'" + dateStr + "'," + A1 + "," + A2 + "," + R1 + "," + R2 + ")");
                    if (calendar.get(Calendar.HOUR_OF_DAY)<=12) {
                        sta.execute("UPDATE EnergyCurrentDay  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 1");
                        sta.execute("UPDATE EnergyCurrentYear  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 1");
                        sta.execute("UPDATE EnergyFromMonth  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 1");
                        sta.execute("UPDATE EnergyFromReset  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 1");
                    } else {
                        sta.execute("UPDATE EnergyCurrentDay  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 2");
                        sta.execute("UPDATE EnergyCurrentYear  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 2");
                        sta.execute("UPDATE EnergyFromMonth  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 2");
                        sta.execute("UPDATE EnergyFromReset  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 2");
                    }

                    System.out.println("ENERGY IS GENERATED [A+ = " + A1 + "; A- = " + A2 + "; R+ = " + R1 + "; R- = " + R2 + "]");

                    con.close();
                } catch (Exception e) {
                    System.err.println("Exception: " + e.getMessage());
                }

            }
        };
        timer.schedule(task,1000*3600,1000*3600);
    }
}