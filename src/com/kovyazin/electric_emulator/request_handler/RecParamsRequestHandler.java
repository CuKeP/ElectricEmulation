package com.kovyazin.electric_emulator.request_handler;

import com.kovyazin.electric_emulator.CommonUtils;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * Created by Slavyan on 19.10.15.
 */
public class RecParamsRequestHandler implements RequestHandler {

    @Override
    public byte[] handle(List<Integer> inputBytes) throws IOException {

        byte[] outputData;

        switch (inputBytes.get(2)) {
            case 0: //	Запись времени интегрирования мощности для первого (или един-ственного) массива профиля мощности
                outputData = new byte[]{0};
                CommonUtils.saveProperties("time_integr", String.valueOf(inputBytes.get(3)));
                return outputData;

            case 5: //Запись сетевого адреса
                CommonUtils.saveProperties("NetworkAddress", String.valueOf(inputBytes.get(3)));
                return new byte[]{0};

            case 34: //запись наименовании точки учета
                String tochkaYcheta="";
                for (int i=4;i<inputBytes.size()-2;i++){
                    int z = inputBytes.get(i);
                    tochkaYcheta+=(char)z;
                }
                CommonUtils.saveProperties("tochkaYcheta", tochkaYcheta);
                return new byte[]{0};

            case 24://флаги
                return new byte[]{0};

            case 27: //коэффиценты трансформации по напряжению
                CommonUtils.saveProperties("KN", String.valueOf((inputBytes.get(3)*256)+inputBytes.get(4)));
                return new byte[]{0};

            case 28: //коэффиценты трансформации по току
                CommonUtils.saveProperties("KT", String.valueOf((inputBytes.get(3)*256)+inputBytes.get(4)));
                return new byte[]{0};

            case 40: //2.3.1.21	Поиск адреса заголовка массива профиля мощности 28h
                //CommonUtils.saveProperties("KT", String.valueOf((inputBytes.get(3)*256)+inputBytes.get(4)));
                return new byte[]{0};

            case 49://2.3.1.28 Запись числа периодов усреднения для измерения вспомогательных парамет-
                CommonUtils.saveProperties("periodAvgParam", String.valueOf(inputBytes.get(4)));
                return new byte[]{0};

            case 50: //идентификатор счетчика
                String idSchetchika="";
                for (int i=4;i<inputBytes.size()-2;i++){
                    int z = inputBytes.get(i);
                    idSchetchika+=(char)z;
                }
                CommonUtils.saveProperties("id", idSchetchika);
                return new byte[]{0};


            default:
                throw new IllegalArgumentException("no code param");
        }
    }

}
