package com.kovyazin.electric_emulator.request_handler;

import com.kovyazin.electric_emulator.CommonUtils;
import com.kovyazin.electric_emulator.Server;

import java.io.*;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * Created by Vyacheslav on 09.10.2015.
 */
public class ReadParamsRequestHandler implements RequestHandler {
    private byte networkAddress;
    private byte postSchet;
    private int serialNumber;
    private byte day;
    private byte month;
    private byte year;
    private int kn;
    private int kt;
    private byte integrationTime;
    private byte integrationTime2;
    private String id;
    private String tochkaYcheta;
    private int periodAvgParam;
    Properties prop = new Properties();


    public ReadParamsRequestHandler() throws IOException {
        prop.load(new InputStreamReader(new FileInputStream("example.properties"), "UTF-8"));
        this.networkAddress = Byte.parseByte(prop.getProperty("NetworkAddress"));
        this.postSchet = Byte.parseByte(prop.getProperty("post_schet"));
        this.serialNumber = Integer.parseInt(prop.getProperty("serNumber"));
        this.day = (byte) Integer.parseInt(prop.getProperty("day"),16);
        this.month = (byte) Integer.parseInt(prop.getProperty("month"),16);
        this.year = (byte) Integer.parseInt(prop.getProperty("year"),16);
        this.kn = Integer.parseInt(prop.getProperty("KN"));
        this.kt = Integer.parseInt(prop.getProperty("KT"));
        this.integrationTime = Byte.parseByte(prop.getProperty("time_integr"));
        this.integrationTime2 = Byte.parseByte(prop.getProperty("time_integr2"));
        this.id = prop.getProperty("id");
        this.tochkaYcheta = prop.getProperty("tochkaYcheta");
        this.periodAvgParam = Integer.parseInt(prop.getProperty("periodAvgParam"));
    }

    @Override
    public byte[] handle(List<Integer> inputBytes) throws IOException {
        byte[] outputData;
        Calendar calendar = Calendar.getInstance();
        switch (inputBytes.get(2)) {
            case 5: //сетевой адрес короткий 3й параметр
                return new byte[]{0, networkAddress};
            case 18: //постоянная счетчика
                return new byte[]{97, (byte) (64 + postSchet), 16};
            case 11: //точка учета
                return CommonUtils.getID(tochkaYcheta);
            case 0: //серийный номер, дата выпуска
                outputData = new byte[7];
                outputData[0] = (byte) (networkAddress / 256 / 256 / 256);
                outputData[1] = (byte) (networkAddress / 256 / 256);
                outputData[2] = (byte) (serialNumber / 256);
                outputData[3] = (byte) (serialNumber % 256);
                outputData[4] = day;
                outputData[5] = month;
                outputData[6] = year;
                return outputData;
            case 2: //коэффиценты трансформации
                outputData = new byte[10];
                outputData[0] = (byte) (kn / 256);
                outputData[1] = (byte) (kn % 256);
                outputData[2] = (byte) (kt / 256);
                outputData[3] = (byte) (kt % 256);
                return outputData;
            case 6: //время интегрирования
                if (inputBytes.get(3) == 1) {
                    outputData = new byte[]{0, 0};
                    outputData[1] = integrationTime2;
                    return outputData;
                } else {
                    outputData = new byte[]{0, 0}; // 1E - 30min
                    outputData[1] = integrationTime;
                    return outputData;
                }
//
            case 4: //Расширенное чтение текущего указателя массива профиля мощности (первого или второго)
                if (inputBytes.get(3) == 1) {
                    return new byte[]{(byte) 0xC2, 0x17, 0x07, 0x12, 0x15, (byte) 0xCD, (byte) 0x38};
                } else {
                    return new byte[]{(byte) 0x80, 0x19, 0x21, 0x11, 0x14, (byte) 0xAB, (byte) 0x98};
                }
            case 1: //Чтение температуры 
                return new byte[]{0, 29};
            case 3: //версия ПО
                return new byte[]{2, 50, 48};
            case 9: //Чтение программируемых флагов
                return new byte[]{0, 6};
            case 34: //Чтение числа периодов усреднения для  измерения вспомогательных параметров
                return new byte[]{0, (byte)periodAvgParam};
            case 36: //Чтение идентификатора счетчика
                return CommonUtils.getID(id);
            case 14: //Чтение указателя текущего тарифа
                if (calendar.get(Calendar.HOUR_OF_DAY)<=12){
                    return new byte[]{0, 0, 0, 0, 0, 0, 0};
                } else {
                    return new byte[]{0, 0, 0, 0, 0, 0, 1};
                }

            case 13: //Чтение энергии текущего тарифа
//                return new byte[]{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
                int[] energyFromDB;
                outputData = new byte[16];

                if (calendar.get(Calendar.HOUR_OF_DAY)<=12){
                    energyFromDB = CommonUtils.selectEnergy(4,0,1);
                } else {
                    energyFromDB = CommonUtils.selectEnergy(4,0,2);
                }


                int Aplus = energyFromDB[0];//(int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));
                int Aminus = energyFromDB[1];//(int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));
                int Rplus = energyFromDB[2];//(int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));
                int Rminus = energyFromDB[3];//(int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));

                outputData[3] = (byte) (Aplus % 256);
                outputData[2] = (byte) (Aplus / 256);
                outputData[1] = (byte) (Aplus / 256 / 256);
                outputData[0] = (byte) (Aplus / 256 / 256 / 256);

                outputData[7] = (byte) (Aminus % 256);
                outputData[6] = (byte) (Aminus / 256);
                outputData[5] = (byte) (Aminus / 256 / 256);
                outputData[4] = (byte) (Aminus / 256 / 256 / 256);

                outputData[11] = (byte) (Rplus % 256);
                outputData[10] = (byte) (Rplus / 256);
                outputData[9] = (byte) (Rplus / 256 / 256);
                outputData[8] = (byte) (Rplus / 256 / 256 / 256);

                outputData[15] = (byte) (Rminus % 256);
                outputData[14] = (byte) (Rminus / 256);
                outputData[13] = (byte) (Rminus / 256 / 256);
                outputData[12] = (byte) (Rminus / 256 / 256 / 256);
                return outputData;
            case 24: //2.4.3.29.1	Чтение слова состояния задачи поиска адреса заголовка массива профиля
                return new byte[]{0};
            default:
                throw new IllegalArgumentException("no code param");
        }
    }
}