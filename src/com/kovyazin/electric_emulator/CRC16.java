package com.kovyazin.electric_emulator;

public class CRC16
{
    public static int crc16(int[] dataBuffer) {
        return crc16(dataBuffer, 0, dataBuffer.length);
    }

    public static int crc16(int[] dataBuffer, int pos, int length) {
        int sum = (int) 0xffff;
        for (int i = pos; i < length; i++) {
            sum = (sum ^ dataBuffer[i]);
            for (int j = 0; j < 8; j++) {
                if ((sum & 0x1) == 1) {
                    sum >>>= 1;
                    sum = (sum ^ 0xA001);
                } else {
                    sum >>>= 1;
                }
            }
        }

        return sum;
    }

    public static int crc16(byte[] dataBuffer) {
        return crc16(dataBuffer, 0, dataBuffer.length);
    }

    public static int crc16(byte[] dataBuffer, int pos, int length) {
        int sum = (int) 0xffff;
        for (int i = pos; i < length; i++) {
            sum = (sum ^ SomeUtils.byte2int(dataBuffer[i]));
            for (int j = 0; j < 8; j++) {
                if ((sum & 0x1) == 1) {
                    sum >>>= 1;
                    sum = (sum ^ 0xA001);
                } else {
                    sum >>>= 1;
                }
            }
        }

        return sum;
    }

    public static String[] getLoHiStr(int crc16) {
        String s = Integer.toHexString(crc16);
        int length = s.length();
        for(int i = 0; i < 4 - length; i++)
        {
            s = "0" + s;
        }

        String stEnd = s.substring(s.length() - 2, s.length());
        String stBeg = s.substring(s.length() - 4, s.length() - 2);

        return new String[] { stEnd, stBeg };
    }

    public static int[] getLoHiInt(int crc16) {
        String[] loHi = getLoHiStr(crc16);

        return new int[] { Long.decode("0x" + loHi[0]).intValue(),
                Long.decode("0x" + loHi[1]).intValue() };
    }

    public static byte[] getLoHiByte(int crc16) {
        int[] loHi = getLoHiInt(crc16);

        return new byte[] { (byte) loHi[0], (byte) loHi[1] };
    }

    public static boolean crc16Equation(int crc16, byte crc16HiByte, byte crc16LoByte)
    {
        byte[] loHi = getLoHiByte(crc16);
        return loHi[0] == crc16LoByte && loHi[1] == crc16HiByte;
    }

    public static byte[] getByteArray(String s, String delim) {
        String[] array = s.split(delim);
        int arrayLength = array.length;
        byte[] result = new byte[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            result[i] = Long.decode("0x" + array[i]).byteValue();
        }

        return result;
    }

    public static byte[] getByteArray(String s) {
        return getByteArray(s, " ");
    }

    public static byte[] getByteArrayWithCrc16(byte[] b) {
        int crc16 = crc16(b);
        byte[] loHi = getLoHiByte(crc16);
        int bLength = b.length;
        byte[] result = new byte[bLength + 2];
        System.arraycopy(b, 0, result, 0, bLength);
        result[bLength] = loHi[0];
        result[bLength + 1] = loHi[1];

        return result;
    }

    public static byte[] getByteArrayWithCrc16(String s) {
        return getByteArrayWithCrc16(getByteArray(s));
    }

    public static byte[] getByteArrayWithCrc16(String s, String delim) {
        return getByteArrayWithCrc16(getByteArray(s, delim));
    }

    public static int getHiByte(int crc16)
    {
        return crc16 / 256;
    }

    public static int getLoByte(int crc16)
    {
        return crc16 % 256;
    }


    public static void fillCrc16(byte[] b)
    {
        int crc = crc16(b, 0, b.length - 2);
        int hi = getHiByte(crc);
        int lo = getLoByte(crc);
        b[b.length - 1] = (byte) hi;
        b[b.length - 2] = (byte) lo;
    }

    public static boolean validateCRC16(int[] b, int pos, int length)
    {
        int crc16 = CRC16.crc16(b, pos, length);
        return CRC16.crc16Equation(crc16, (byte) b[pos + length + 1], (byte) b[pos + length]);
    }

    public static boolean validateCRC16(byte[] b, int pos, int length)
    {
        int crc16 = CRC16.crc16(b, pos, length);
        return CRC16.crc16Equation(crc16, (byte) b[pos + length + 1], (byte) b[pos + length]);
    }

    public static void main(String[] args) {
        byte[] b = new byte[4];
        b[0] = (byte) 0xA; //Byte.parseByte("9", 16);
        b[1] = (byte) 0x80; //Byte.parseByte("80", 16);
        fillCrc16(b);
        System.out.println(SomeUtils.toHexString(b));
    }
}