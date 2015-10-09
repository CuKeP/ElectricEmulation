package com.kovyazin.electric_emulator.request_handler;

import java.util.List;

/**
 * Created by Vyacheslav on 09.10.2015.
 */
public class ReadParamsRequstHandler implements RequestHandler {
    private byte networkAddress;
    private byte postSchet;
    private int serialNumber;
    private byte day;
    private byte month;
    private byte year;
    private byte kn;
    private byte kt;
    private byte integrationTime;
    private byte integrationTime2;
    private String id;

    public ReadParamsRequstHandler(String networkAddress, String postSchet, String serialNumber, String day, String month,
                                   String year, String kn, String kt, String integrationTime, String integrationTime2, String id) {
        this.networkAddress = Byte.parseByte(networkAddress);
        this.postSchet = Byte.parseByte(postSchet);
        this.serialNumber = Integer.parseInt(serialNumber);
        this.day = Byte.parseByte(day);
        this.month = Byte.parseByte(month);
        this.year = Byte.parseByte(year);
        this.kn = Byte.parseByte(kn);
        this.kt = Byte.parseByte(kt);
        this.integrationTime = Byte.parseByte(integrationTime);
        this.integrationTime2 = Byte.parseByte(integrationTime2);
        this.id = id;
    }

    @Override
    public byte[] handle(List<Integer> inputBytes) {
        byte[] outputData;
        switch (inputBytes.get(2)) {
            case 5: //������� ����� �������� 3� ��������
                return new byte[]{0, networkAddress};
            case 18: //���������� ��������
                return new byte[]{97, (byte) (64 + postSchet), 16};
            case 11: //����� �����
                return new byte[16];
            case 0: //�������� �����, ���� �������
                outputData = new byte[7];
                outputData[0] = (byte) (networkAddress / 256 / 256 / 256);
                outputData[1] = (byte) (networkAddress / 256 / 256);
                outputData[2] = (byte) (serialNumber / 256);
                outputData[3] = (byte) (serialNumber % 256);
                outputData[4] = day;
                outputData[5] = month;
                outputData[6] = year;
                return outputData;
            case 2: //����������� �������������
                outputData = new byte[10];
                outputData[0] = (byte) (kn / 256);
                outputData[1] = (byte) (kn % 256);
                outputData[2] = (byte) (kt / 256);
                outputData[3] = (byte) (kt % 256);
                return outputData;
            case 6: //����� ��������������
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
            case 4: //����������� ������ �������� ��������� ������� ������� �������� (������� ��� �������)
                if (inputBytes.get(3) == 1) {
                    return new byte[]{(byte) 0x86, 0x19, 0x21, 0x11, 0x14, (byte) 0xB1, (byte) 0x48};
                } else {
                    return new byte[]{(byte) 0x80, 0x19, 0x21, 0x11, 0x14, (byte) 0xAB, (byte) 0x98};
                }
            case 1: //������ �����������
                return new byte[]{0, 29};
            case 3: //������ ��
                return new byte[]{2, 50, 48};
            case 9: //������ ��������������� ������
                return new byte[]{0, 6};
            case 34: //������ ����� �������� ���������� ���  ��������� ��������������� ����������
                return new byte[]{0, 0x32};
            case 36: //������ �������������� ��������
                return getID(id);
            case 14: //������ ��������� �������� ������
                return new byte[]{17, 18, 4, 11, 12, 14, 0};
            case 13: //������ ������� �������� ������
                return new byte[]{0, 0, 12, 2, 0, 0, 12, 13, 0, 4, 0, 0, 0, 11, 0, 0};
            default:
                throw new IllegalArgumentException("no code param");
        }
    }

    private byte[] getID(String s) {
        byte out[] = new byte[s.length()];
        for (int j = 0; j < s.length(); j++) {
            out[j] = (byte) s.charAt(j);
        }
        System.out.println("ret=" + out[0]);
        return out;
    }
}
