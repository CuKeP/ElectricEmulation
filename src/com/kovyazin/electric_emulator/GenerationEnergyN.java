package com.kovyazin.electric_emulator;

import com.kovyazin.electric_emulator.request_handler.GenerationEnergy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class GenerationEnergyN {
    public static void main(String[] args) {
        dbselect();
    }


    public static void dbselect(){

        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
//        calendar.set(2015,0,0, 0,0,0);
        calendar.set(Calendar.DATE,calendar.get(Calendar.DATE) - Server.n);

        String dateStr = new String();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

//        System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
//        System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
//        System.out.println("DATE: " + calendar.get(Calendar.DATE));
//        System.out.println("HOUR: " + calendar.get(Calendar.HOUR));


        try {
            Connection con = null;

            Properties connInfo = new Properties();
            connInfo.put("user", "");
            connInfo.put("password","");
            //это обязательно, иначе русские символы не будут восприниматься, в базе кирилица...
            connInfo.put("charSet", "Cp1251");
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String db="jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=C:\\Program Files\\kovyazin\\ElectricEmulator\\db.mdb";
            con = DriverManager.getConnection(db, connInfo);
            System.out.println("Connection ok.");

            Statement sta = con.createStatement();

            //ResultSet res =
            int A1,A2,R1,R2;
            for (int i=0; i<Server.n; i++){
                calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+1);
                for(int j=0; j<24; j++){
                    A1 = (int) (Math.random() * 4);
                    A2 = (int) (Math.random() * 4);
                    R1 = (int) (Math.random() * 4);
                    R2 = (int) (Math.random() * 4);
                    dateStr = dateFormat.format(calendar.getTime());

                    if (calendar.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)){
                        sta.execute("INSERT INTO GenerationTimeCY ( `hour`, `date1`, `A+`, `A-`, `R+`, `R-` ) " + "VALUES ("+j+",'"+dateStr+"',"+A1+","+A2+","+R1+","+R2+")");

                        if (j<12) {
                            sta.execute("UPDATE EnergyCurrentYear  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 1");
                            sta.execute("UPDATE EnergyFromReset  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 1");
                        } else {
                            sta.execute("UPDATE EnergyCurrentYear  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 2");
                            sta.execute("UPDATE EnergyFromReset  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 2");
                        }

                        if (i == Server.n-1){
                            if (j<12) {
                                sta.execute("UPDATE EnergyCurrentDay  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 1");
                            } else {
                                sta.execute("UPDATE EnergyCurrentDay  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 2");
                            }
                        }

                        if (i == Server.n-2){
                            if (j<12) {
                                sta.execute("UPDATE EnergyPreviousDay  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 1");
                            } else {
                                sta.execute("UPDATE EnergyPreviousDay  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 2");
                            }
                        }

                        if (calendar.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)){
                            if (j<12) {
                                sta.execute("UPDATE EnergyFromMonth  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 1");
                            } else {
                                sta.execute("UPDATE EnergyFromMonth  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 2");
                            }
                        }
                    } else {

                        sta.execute("INSERT INTO GenerationTimePY ( `hour`, `date1`, `A+`, `A-`, `R+`, `R-` ) " + "VALUES ("+j+",'"+dateStr+"',"+A1+","+A2+","+R1+","+R2+")");

                        if (j<12) {
                            sta.execute("UPDATE EnergyPreviousYear  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 1");
                            sta.execute("UPDATE EnergyFromReset  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 1");
                        } else {
                            sta.execute("UPDATE EnergyPreviousYear  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 2");
                            sta.execute("UPDATE EnergyFromReset  SET `A+`=`A+`+" + A1 + ",`A-`=`A-`+" + A2 + ",`R+`=`R+`+" + R1 + ",`R-`=`R-`+" + R2 + " WHERE `N_mass` = 2");
                        }

                    }


                }
            }




//            System.out.println("YEAR: " + dateFormat.format(calendar.getTime()));
//            System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
//            System.out.println("HOUR: " + calendar.get(Calendar.DATE));
//            System.out.println("HOUR: " + calendar.get(Calendar.HOUR));


//            sta.execute("INSERT INTO GenerationTimeOther ( hour, date1, `A+`, `A-`, `R+`, `R-` ) VALUES (1,Date(),1,1,1,1)");

//            System.out.println("List: ");
////            while (res.next()) {
////                System.out.println("  " +res.getString(1) + " | "+res.getString(2) + " | "+res.getString(3) + " | "+res.getString(4)); //+ " | "+res.getString(5)+ " | "+res.getString(6));
//////                System.out.println(res.getString(1));
////            }
//            int[] result = {0,0,0,0};
//            while (res.next()) {
//                //System.out.println("  " +res.getString(1) + " | "+res.getString(2) + " | "+res.getString(3) + " | "+res.getString(4));// + " | "+res.getString(5));
////                System.out.println(res.getString(1));
//                result[0] = (int) Float.parseFloat(res.getString(1));
//                result[1] = (int) Float.parseFloat(res.getString(2));
//                result[2] = (int) Float.parseFloat(res.getString(3));
//                result[3] = (int) Float.parseFloat(res.getString(4));
//            }
//            System.out.println("REQUEST MASS = "+result[0]+" "+result[1]+" "+result[2]+" "+result[3]+" ");

//            res.close();
            con.close();
        } catch (Exception e) {
            System.err.println("Exception: "+e.getMessage());
        }
    }
}