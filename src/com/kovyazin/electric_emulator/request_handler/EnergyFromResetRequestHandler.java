package com.kovyazin.electric_emulator.request_handler;

import com.kovyazin.electric_emulator.CommonUtils;
import com.kovyazin.electric_emulator.Server;

import java.util.List;

/**
 * Created by Vyacheslav on 09.10.2015.
 */
public class EnergyFromResetRequestHandler implements RequestHandler {
    private String date;
    private int delta;
    private int nTarif;
    private int nMass;
    private  int nMonth;

    public EnergyFromResetRequestHandler(String date, int delta, int nTarif, int nMassNmonth) {
        this.date = date;
        this.delta = delta;
        this.nTarif = nTarif;
        this.nMass = nMassNmonth / 16;//Integer.parseInt(String.valueOf(Integer.toHexString(nMassNmonth).charAt(0)));
        this.nMonth = nMassNmonth % 16;//Integer.parseInt(String.valueOf(Integer.toHexString(nMassNmonth).charAt(1)));
    }

    @Override
    public byte[] handle(List<Integer> inputBytes) {
        byte[] outputData = new byte[16];
        int[] energyFromDB = {0,0,0,0};

        Server.globalNmass = nMass;

        if (nTarif > 0) {
            energyFromDB = CommonUtils.selectEnergy(nMass,nMonth,nTarif);
        }
        if (nTarif == 0) {
            energyFromDB = CommonUtils.selectEnergy(nMass,nMonth,0);
        }

        int Aplus = energyFromDB[0];//(int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));
        int Aminus = energyFromDB[1];//(int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));
        int Rplus = energyFromDB[2];//(int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));
        int Rminus = energyFromDB[3];//(int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));

        outputData[3] = (byte) (Aplus % 256);
        outputData[2] = (byte) (Aplus / 256);
        outputData[1] = (byte) (Aplus / 256 / 256);
        outputData[0] = (byte) (Aplus / 256 / 256 / 256);
//        outputData[3] = (byte)0x0;
//        outputData[2] = (byte)0x1;
//        outputData[1] = (byte)0x0;
//        outputData[0] = 0;

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
    }
}