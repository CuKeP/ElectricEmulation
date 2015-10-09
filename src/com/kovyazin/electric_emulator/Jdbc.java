//package com.kovyazin.electric_emulator;
//
//import java.sql.Connection;
//import java.sql.Date;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class Jdbc {
//
//    public static final String URL = "jdbc:postgresql://localhost:5433/postgres";
//    public static final String USER = "postgres";
//    public static final String PASS = "261192";
//    public static String[] kursor = new String[10];
//    public static String Data;
//    public static String str;
//
//
//    public static int getEnergyReset(String SQL) throws SQLException {
//
//        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
//        Connection con = DriverManager.getConnection(URL, USER, PASS);
//        Statement statement = con.createStatement();
//
////        ResultSet rs = statement.executeQuery("SELECT \"to\" from ENERGY");
//        Data = "";
//        ResultSet rs = statement.executeQuery(SQL);
////        rs.next();
//        while (rs.next()) {
//            for (int i = 1; i <=1; i++) {
//                kursor[i-1] = rs.getString(i);
//            }
//            for (String s:kursor){
//                if (s!=null){
//                    System.out.print(" " + s);
//                    str=s;
//                }
//            }
//            System.out.println();
////            Date currentDate = rs.getDate("date");
////            System.out.println("Current Date from Oracle is : " + currentDate);
//        }
//
//
//        rs.close();
//        statement.close();
//        con.close();
//        return Integer.parseInt(str);
//    }
//}