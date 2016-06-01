package com.kovyazin.electric_emulator.request_handler;

import java.util.List;

/**
 * Created by Slavyan on 16.11.15.
 */
public class ReadPhysicalAddressShortRequestHandler implements RequestHandler {
    public byte[] handle(List<Integer> inputBytes) {
        byte[] outputData = new byte[inputBytes.get(5)];
        for (int i=0;i<outputData.length;i++){
        outputData[i] = (byte) (Math.random()*128);
    }


        return outputData;
    }
}
