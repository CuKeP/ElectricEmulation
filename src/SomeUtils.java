//package org.eva.papyrus.counter.driver.electricity;
//import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SomeUtils
{

    public static int UNLIMITED_RESPONCE_LENGTH = Integer.MAX_VALUE;

    static final int TIMEOUT = 6;
    static final int COUNTER_MAX_DELAY = 100;

//    private static Logger log = Logger.getLogger(SomeUtils.class);

    private static final int[] BIT_CONST = {Integer.parseInt("00000001", 2),
            Integer.parseInt("00000010", 2),
            Integer.parseInt("00000100", 2),
            Integer.parseInt("00001000", 2),
            Integer.parseInt("00010000", 2),
            Integer.parseInt("00100000", 2),
            Integer.parseInt("01000000", 2),
            Integer.parseInt("10000000", 2)};

    private static int bitStatus(int i, int ind)
    {
        return (i & BIT_CONST[ind]) == 0 ? 0 : 1;

    }

    public static int byte2int(byte b) {
        return b & 0xff;
    }

    public static int[] byteArray2intArray(byte[] b)
    {
        if(b == null)
        {
            return null;
        }
        int[] intArray = new int[b.length];
        for(int i = 0; i < b.length; i++)
        {
            intArray[i] = SomeUtils.byte2int(b[i]);
        }

        return intArray;
    }

    public static  byte[] nonBlockingRead(InputStream is, int timeoutMs, int iterationDelayMs) throws IOException, InterruptedException
    {
        return nonBlockingRead(is, timeoutMs, iterationDelayMs, -1);
    }

    public static byte[] nonBlockingRead(InputStream is, int timeoutMs, int iterationDelayMs, int silentTimeoutMs) throws IOException, InterruptedException
    {
        return nonBlockingRead(is, timeoutMs, iterationDelayMs, silentTimeoutMs, -1);
    }

    public static byte[] nonBlockingRead(InputStream is, int timeoutMs, int iterationDelayMs, int silentTimeoutMs, int responceLength) throws IOException, InterruptedException
    {
        //log.debug("timeoutMs=" + timeoutMs);
        //log.debug("iterationDelayMs=" + iterationDelayMs);
        //log.debug("silentTimeoutMs=" + silentTimeoutMs);

        if(silentTimeoutMs == -1)
        {
            silentTimeoutMs = timeoutMs;
        }

        List<byte[]> bufList = new ArrayList<byte[]>();
        int length = 0;

        long firstReadTime = System.currentTimeMillis();
        //log.debug("firstReadTime=" + firstReadTime);
        long lastReadDataTime = -1;
        boolean isFirstIteration = true;
        boolean sleepFlag = true;
        while(true)
        {
            if(Thread.interrupted())
            {
                throw new InterruptedException();
            }

            //log.debug("read cycle...");
            //log.debug("lastReadDataTime=" + lastReadDataTime);
            //log.debug("System.currentTimeMillis() - firstReadTime=" + (System.currentTimeMillis() - firstReadTime));
            if(lastReadDataTime == -1 && System.currentTimeMillis() - firstReadTime > timeoutMs)
            {
                break;
            }
            if(lastReadDataTime != -1 && System.currentTimeMillis() - lastReadDataTime > silentTimeoutMs)
            {
                break;
            }
            if(isFirstIteration)
            {
                isFirstIteration = false;
            } else
            {
                if(sleepFlag)
                {
                    Thread.sleep(iterationDelayMs);
                }
                sleepFlag = true;
            }

            int available = is.available();
            int bufSize = 1024;
            byte[] buf = new byte[bufSize];
            byte[] readedBytes = null;
            int readedLength = 0;
            if(available > 0)
            {
                //log.debug("available=" + available);
                readedLength = is.read(buf, 0, bufSize);
                //log.debug("readedLength=" + readedLength);
                if(readedLength < available) // значит прочитали не все из потока и на следующей итерации не следует засыпать перед чтением.
                {
                    sleepFlag = false;
                }
                readedBytes = new byte[readedLength];
                System.arraycopy(buf, 0, readedBytes, 0, readedLength);
                bufList.add(readedBytes);
                length = length + readedLength;
                //log.debug("length=" + length);
                lastReadDataTime = System.currentTimeMillis();

                if(responceLength != UNLIMITED_RESPONCE_LENGTH && length > 1024) // слишком длинный ответ, скорее всего это "мусор" из неправильно функционирующего порта.
                {
//                    log.debug("readed bytes amount=" + length);
                    byte[] result = new byte[length];
                    int destPos = 0;
                    for(byte[] b : bufList)
                    {
                        System.arraycopy(b, 0, result, destPos, b.length);
                        destPos = destPos + b.length;
                    }
//                    log.debug(SomeUtils.toHexString(result));
                    throw new IOException("Too long(more 1024) continious responce, may be port is broken.");
                }

                if(responceLength > 0 && responceLength <= length) // читаем только нужное число байт если указано(если responceLength > 0)
                {
                    break;
                }
            }
        }
        //log.debug("end cycle");

        if(length > 0)
        {
            byte[] result = new byte[length];
            int destPos = 0;
            for(byte[] buf : bufList)
            {
                System.arraycopy(buf, 0, result, destPos, buf.length);
                destPos = destPos + buf.length;
            }

            return result;
        }

        return null;
    }

    // то же самое что и byte[] nonBlockingRead(InputStream is, int timeoutMs, int iterationDelayMs, int silentTimeoutMs, int responceLength)
    // за исключением того что возвращает так же и время прихода первого байта ответа.
    public static Object[] nonBlockingReadWithRespStartTime(InputStream is, int timeoutMs, int iterationDelayMs, int silentTimeoutMs, int responceLength) throws IOException, InterruptedException
    {
        if(silentTimeoutMs == -1)
        {
            silentTimeoutMs = timeoutMs;
        }

        List<byte[]> bufList = new ArrayList<byte[]>();
        int length = 0;

        long firstReadTime = System.currentTimeMillis();
        long lastReadDataTime = -1;
        boolean isFirstIteration = true;
        long responceDataAvailableTimeMs = -1;
        while(true)
        {
            if(lastReadDataTime == -1 && System.currentTimeMillis() - firstReadTime > timeoutMs)
            {
                break;
            }
            if(lastReadDataTime != -1 && System.currentTimeMillis() - lastReadDataTime > silentTimeoutMs)
            {
                break;
            }
            if(isFirstIteration)
            {
                isFirstIteration = false;
            } else
            {
                Thread.sleep(iterationDelayMs);
            }

            int available = is.available();
            if(available > 0)
            {
                if(responceDataAvailableTimeMs == -1)
                {
                    responceDataAvailableTimeMs = System.currentTimeMillis();
                }
                byte[] buf = new byte[available];
                is.read(buf, 0, available);
                bufList.add(buf);
                length = length + available;
                lastReadDataTime = System.currentTimeMillis();
                if(responceLength > 0 && responceLength == length) // читаем только нужное число байт если указано(если responceLength > 0)
                {
                    break;
                }
            }
        }

        if(length > 0)
        {
            byte[] result = new byte[length];
            int destPos = 0;
            for(byte[] buf : bufList)
            {
                System.arraycopy(buf, 0, result, destPos, buf.length);
                destPos = destPos + buf.length;
            }

            return new Object[] {result, responceDataAvailableTimeMs};
        }

        return null;
    }

    public static String toHexString(byte[] arr, int startInd, int endInd)
    {
        StringBuffer buf = new StringBuffer();
        for(int i = startInd; i < endInd; i++)
        {
            buf.append(Integer.toHexString(SomeUtils.byte2int(arr[i])).toUpperCase());
            buf.append(" ");
        }

        return buf.toString();
    }

    public static String toHexString(byte[] arr)
    {
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < arr.length; i++)
        {
            buf.append(Integer.toHexString(SomeUtils.byte2int(arr[i])).toUpperCase());
            buf.append(" ");
        }

        return buf.toString();
    }

    public static String toHexString(int[] arr)
    {
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < arr.length; i++)
        {
            buf.append(Integer.toHexString(arr[i]).toUpperCase());
            buf.append(" ");
        }

        return buf.toString();
    }

    public static int to2_10code(int i)
    {
        return i < 10 ? i : Integer.parseInt("" + i, 16);
    }

    public static int from2_10code(int i)
    {
        return i < 10 ? i : Integer.parseInt(Integer.toHexString(i), 10);
    }

    public static long parse4Bytes(int i1, int i2, int i3, int i4)
    {
        return ((long)i1)*0x1000000 + i2*0x10000 + i3*0x100 + i4;
    }

    public static int parse3Bytes(int i1, int i2, int i3)
    {
        return i1*0x10000 + i2*0x100 + i3;
    }

    public static int parse2Bytes(int i1, int i2)
    {
        return i1*0x100 + i2;
    }

    public static double parseDouble(int m, int b1, int b2, int b3)
    {
        int a = b1;
        int b = b2;
        int c = b3;

        double order = Math.pow(2, m - 127);
        int sign = bitStatus(a, 7) == 0 ? 1 : -1;
        double sum = 1;
        if(bitStatus(a, 6) == 1)
        {
            sum = sum + Math.pow(2, -1);
        }
        if(bitStatus(a, 5) == 1)
        {
            sum = sum + Math.pow(2, -2);
        }
        if(bitStatus(a, 4) == 1)
        {
            sum = sum + Math.pow(2, -3);
        }
        if(bitStatus(a, 3) == 1)
        {
            sum = sum + Math.pow(2, -4);
        }
        if(bitStatus(a, 2) == 1)
        {
            sum = sum + Math.pow(2, -5);
        }
        if(bitStatus(a, 1) == 1)
        {
            sum = sum + Math.pow(2, -6);
        }
        if(bitStatus(a, 0) == 1)
        {
            sum = sum + Math.pow(2, -7);
        }

        if(bitStatus(b, 7) == 1)
        {
            sum = sum + Math.pow(2, -8);
        }
        if(bitStatus(b, 6) == 1)
        {
            sum = sum + Math.pow(2, -9);
        }
        if(bitStatus(b, 5) == 1)
        {
            sum = sum + Math.pow(2, -10);
        }
        if(bitStatus(b, 4) == 1)
        {
            sum = sum + Math.pow(2, -11);
        }
        if(bitStatus(b, 3) == 1)
        {
            sum = sum + Math.pow(2, -12);
        }
        if(bitStatus(b, 2) == 1)
        {
            sum = sum + Math.pow(2, -13);
        }
        if(bitStatus(b, 1) == 1)
        {
            sum = sum + Math.pow(2, -14);
        }
        if(bitStatus(b, 0) == 1)
        {
            sum = sum + Math.pow(2, -15);
        }

        if(bitStatus(c, 7) == 1)
        {
            sum = sum + Math.pow(2, -16);
        }
        if(bitStatus(c, 6) == 1)
        {
            sum = sum + Math.pow(2, -17);
        }
        if(bitStatus(c, 5) == 1)
        {
            sum = sum + Math.pow(2, -18);
        }
        if(bitStatus(c, 4) == 1)
        {
            sum = sum + Math.pow(2, -19);
        }
        if(bitStatus(c, 3) == 1)
        {
            sum = sum + Math.pow(2, -20);
        }
        if(bitStatus(c, 2) == 1)
        {
            sum = sum + Math.pow(2, -21);
        }
        if(bitStatus(c, 1) == 1)
        {
            sum = sum + Math.pow(2, -22);
        }
        if(bitStatus(c, 0) == 1)
        {
            sum = sum + Math.pow(2, -23);
        }

        return sign * sum * order;
    }

    public static int cutFirstBit(int i)
    {
        String s = Integer.toBinaryString(i);
        if(s.length() == 8)
        {
            s = s.substring(1);
            return Integer.parseInt(s, 2);
        }

        return i;
    }

    public static int getDayOfWeek(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek == Calendar.MONDAY)
        {
            return 1;
        }
        if(dayOfWeek == Calendar.TUESDAY)
        {
            return 2;
        }
        if(dayOfWeek == Calendar.WEDNESDAY)
        {
            return 3;
        }if(dayOfWeek == Calendar.THURSDAY)
    {
        return 4;
    }if(dayOfWeek == Calendar.FRIDAY)
    {
        return 5;
    }if(dayOfWeek == Calendar.SATURDAY)
    {
        return 6;
    }

        // if it sunday
        return 7;
    }

}