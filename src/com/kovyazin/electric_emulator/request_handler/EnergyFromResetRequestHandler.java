package com.kovyazin.electric_emulator.request_handler;

import com.kovyazin.electric_emulator.CommonUtils;

import java.util.List;

/**
 * Created by Vyacheslav on 09.10.2015.
 */
public class EnergyFromResetRequestHandler implements RequestHandler {
    private String date;
    private int delta;

    public EnergyFromResetRequestHandler(String date, int delta) {
        this.date = date;
        this.delta = delta;
    }

    @Override
    public byte[] handle(List<Integer> inputBytes) {

        byte[] outputData = new byte[16];

        int Aplus = (int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));
        int Aminus = (int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));
        int Rplus = (int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));
        int Rminus = (int) (CommonUtils.getDateDelta(date) * (Math.random() * delta));

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
    }
}
