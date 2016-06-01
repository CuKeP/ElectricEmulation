package com.kovyazin.electric_emulator;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * Created by Slavyan on 24.05.16.
 */
public class GetEnergyProfiles {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

    }
    static void getEnergy() throws ClassNotFoundException, SQLException {
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
        byte[] cs = new byte[6];
        Server.res = sta.executeQuery("SELECT * FROM `GenerationTimeCY` WHERE `id`<13900");
        while (Server.res.next()){
            Server.profilesMass[Server.globalI2] = ((byte) SomeUtils.to2_10code(Server.res.getInt(2)));
            Date d_point = Server.res.getDate(3);
            String day = new SimpleDateFormat("dd").format(d_point).toString();
            String month = new SimpleDateFormat("MM").format(d_point).toString();
            String year = new SimpleDateFormat("yy").format(d_point).toString();
            Server.profilesMass[Server.globalI2+1] =((byte) SomeUtils.to2_10code(Integer.parseInt(day)));
            Server.profilesMass[Server.globalI2+2] =((byte) SomeUtils.to2_10code(Integer.parseInt(month)));
            Server.profilesMass[Server.globalI2+3] =((byte) SomeUtils.to2_10code(Integer.parseInt(year)));
            Server.profilesMass[Server.globalI2+4] =((byte) 0x00);
            Server.profilesMass[Server.globalI2+5] =((byte) 0x3C);

            cs[0] = Server.profilesMass[Server.globalI2];
            cs[1] = Server.profilesMass[Server.globalI2+1];
            cs[2] = Server.profilesMass[Server.globalI2+2];
            cs[3] = Server.profilesMass[Server.globalI2+3];
            cs[4] = Server.profilesMass[Server.globalI2+4];
            cs[5] = Server.profilesMass[Server.globalI2+5];

            Server.profilesMass[Server.globalI2+6] = (byte) CommonUtils.controlSum(cs);
            Server.profilesMass[Server.globalI2+7] =((byte) 0x08);

//            System.out.println("A+= "+res.getInt(4));
//            System.out.println("HOUR= "+res.getInt(5));
//            System.out.println("HOUR= "+res.getInt(6));
//            System.out.println("HOUR= "+res.getInt(7));
//            Server.profilesMass[Server.globalI2+8] =((byte) Server.res.getInt(4));
//            Server.profilesMass[Server.globalI2+9] =((byte) 0);
//            Server.profilesMass[Server.globalI2+10] =((byte) Server.res.getInt(5));
//            Server.profilesMass[Server.globalI2+11] =((byte) 0);
//            Server.profilesMass[Server.globalI2+12] =((byte) Server.res.getInt(6));
//            Server.profilesMass[Server.globalI2+13] =((byte) 0);
//            Server.profilesMass[Server.globalI2+14] =((byte) Server.res.getInt(7));
//            Server.profilesMass[Server.globalI2+15] =((byte) 0);
            Server.profilesMass[Server.globalI2+8] =(5);
            Server.profilesMass[Server.globalI2+9] =((byte) 0);
            Server.profilesMass[Server.globalI2+10] =(5);
            Server.profilesMass[Server.globalI2+11] =((byte) 0);
            Server.profilesMass[Server.globalI2+12] =(5);
            Server.profilesMass[Server.globalI2+13] =((byte) 0);
            Server.profilesMass[Server.globalI2+14] =(5);
            Server.profilesMass[Server.globalI2+15] =((byte) 0);

            Server.globalI2=Server.globalI2+16;
        }
    }
}
