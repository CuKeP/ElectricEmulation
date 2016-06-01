package com.kovyazin.electric_emulator.request_handler;

import com.kovyazin.electric_emulator.CommonUtils;
import com.kovyazin.electric_emulator.Server;
import com.kovyazin.electric_emulator.SomeUtils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * Created by Slavyan on 16.11.15.
 */
public class ReadPhysicalAddressRequestHandler implements RequestHandler {
    private ResultSet res;
    public ReadPhysicalAddressRequestHandler(ResultSet res) {
        this.res = res;
    }

    public byte[] handle(List<Integer> inputBytes) throws ClassNotFoundException, SQLException {

        //     ЗАГОЛОВОК                               |     первые 30 мин            |       вторые 30 мин
      // часы   Дата       зима\лето t интегр   КС       А+       А-     R+      R-   |   A+      A-      R+      R-
        //17  |09 11 15|    |01|     |1E|      |65| 08 |80 00| |80 00| |80 00| |80 00| |80 00| |80 00| |80 00| |80 00|


        byte[] outputData = new byte[inputBytes.get(6)+1];
        outputData[0] = inputBytes.get(2).byteValue();
//        System.out.println("  " +res.getString(1) + " | "+res.getString(2) + " | "+res.getString(3) + " | "+res.getString(4));

        for (int i=1; i<outputData.length; i++) {
            outputData[i] = Server.profilesMass[Server.globalI];
            Server.globalI=Server.globalI+1;
        }
//            outputData[2] = Server.profilesMass.get(i+1);
//            outputData[3] = Server.profilesMass.get(i+2);
//            outputData[4] = Server.profilesMass.get(i+3);
//            outputData[5] = Server.profilesMass.get(i+4);
//            outputData[6] = Server.profilesMass.get(i+5);
//            outputData[7] = (byte) CommonUtils.controlSum(outputData);
//            outputData[8] = Server.profilesMass.get(i+7);
//
////            System.out.println("A+= "+res.getInt(4));
////            System.out.println("HOUR= "+res.getInt(5));
////            System.out.println("HOUR= "+res.getInt(6));
////            System.out.println("HOUR= "+res.getInt(7));
//            outputData[9] = Server.profilesMass.get(i+8);
//            outputData[10] = Server.profilesMass.get(i+9);
//            outputData[11] = Server.profilesMass.get(i+10);
//            outputData[12] = Server.profilesMass.get(i+11);
//            outputData[13] = Server.profilesMass.get(i+12);
//            outputData[14] = Server.profilesMass.get(i+13);
//            outputData[15] = Server.profilesMass.get(i+14);
//            outputData[16] = Server.profilesMass.get(i+15);
//            System.out.println("ZAGOLOBOK GOTOV");
//        }


//        for (int i=1; i<outputData.length; i=+16){
//            res.next();
////            System.out.println("HOUR= "+res.getInt(2));
//            outputData[1] = (byte) SomeUtils.to2_10code(res.getInt(2));
//            Date d_point = res.getDate(3);
//        // форматируем дату и выводим
//            String dateFormatted = new SimpleDateFormat("dd").format(d_point).toString();
////            System.out.println("Date: ".concat(dateFormatted));
//            outputData[2] = (byte) SomeUtils.to2_10code(Integer.parseInt(dateFormatted));
//            outputData[3] = (byte) SomeUtils.to2_10code(01);
//            outputData[4] = (byte) SomeUtils.to2_10code(01);
//            outputData[5] = 0x00;
//            outputData[6] = 0x3C;
//            outputData[7] = (byte) CommonUtils.controlSum(outputData);
//            outputData[8] = 0x08;
//
////            System.out.println("A+= "+res.getInt(4));
////            System.out.println("HOUR= "+res.getInt(5));
////            System.out.println("HOUR= "+res.getInt(6));
////            System.out.println("HOUR= "+res.getInt(7));
//            outputData[9] = (byte) res.getInt(4);
//            outputData[10] = 0;
//            outputData[11] = (byte) res.getInt(5);
//            outputData[12] = 0;
//            outputData[13] = (byte) res.getInt(6);
//            outputData[14] = 0;
//            outputData[15] = (byte) res.getInt(7);
//            outputData[16] = 0;
//        }



//        outputData[17] = (byte) SomeUtils.to2_10code(16);
//        outputData[18] = (byte) SomeUtils.to2_10code(15);
//        outputData[19] = (byte) SomeUtils.to2_10code(4);
//        outputData[20] = (byte) SomeUtils.to2_10code(16);
//        outputData[21] = 0x01;
//        outputData[22] = 0x3C;
//        outputData[23] = (byte) (outputData[7]+1);
//        outputData[24] = 0x08;
//        outputData[25] = 0xA;
//        outputData[26] = 0xB;

//        for (int i=1;i<outputData.length;i++){
//            outputData[i] = (byte)(Math.random()*9+1);
//        }
        return outputData;
    }
}
