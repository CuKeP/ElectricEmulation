package com.kovyazin.electric_emulator;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class Server {
    public static InputStream inputMass = null;
    public static OutputStream outputMass = null;
    public static Properties prop = new Properties();

    public static final int TESTS_CONNECTION_INDEX = 0;
    public static final int OPEN_CHANEL = 1;
    public static final int CLOSE_CHANEL = 2;
    public static final int ENERGY_FROM_RESET = 5;
    public static final int READING_PARAMETERS = 8;


    public static byte[] getID(String s) {
        byte out[] = new byte[s.length()];
        for (int j = 0; j < s.length(); j++) {
            out[j] = (byte) s.charAt(j);
        }
        System.out.println("ret=" + out[0]);
        return out;
    }

    public static int getDate() throws ParseException {

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        long date1 = df.parse(prop.getProperty("data_otscheta")).getTime();
        long date2 = new Date().getTime();
        long date3 = date2 - date1;
        return (int) (date3 / 1000 / 60 / 60);
    }

    public static void TestCloseOpenRead(int b, byte[] poz) throws IOException {
        byte[] fs = new byte[poz.length + 2];
        System.arraycopy(poz, 0, fs, 1, fs.length - 2);
        fs[0] = (byte) b;
        ArrayList<Integer> ip2 = new ArrayList<Integer>();
        for (byte f : fs) {
            ip2.add((int) f);
        }
        fs[poz.length + 1] = (byte) controlSum(ip2);
        System.out.println("output mass: " + Arrays.toString(fs) + "\n");

        outputMass.write(fs);

    }

    public static int controlSum(ArrayList<Integer> outMass) {
        int KC = 255;
        int getInt;
        for (int i = 0; i < outMass.size() - 1; i++) {
            getInt = outMass.get(i);
            KC = KC ^ getInt;
        }
        return KC;
    }

    public static void main(String[] args) throws IOException, ParseException {


        prop.load(new InputStreamReader(new FileInputStream("D:\\progr\\JAVA_PROJECTS\\ElectricEmulator\\src\\com\\kovyazin\\electric_emulator\\example.properties"), "UTF-8"));
        System.out.println("data=" + getDate());

        System.out.println("Welcome to Server side");

        ArrayList<Integer> ip = new ArrayList<Integer>();

        ServerSocket servers;
        Socket fromclient = null;

        servers = new ServerSocket(4444, 0, InetAddress.getByName("localhost"));


        try {
            System.out.print("Waiting for a client...");
            fromclient = servers.accept();
            System.out.println("Client connected");


        } catch (IOException e) {
            System.out.println("Can't accept");
            System.exit(-1);
        }

        inputMass = fromclient.getInputStream();
        outputMass = fromclient.getOutputStream();

        while (inputMass != null) {
//            System.out.println(inputMass.read());
            int a = inputMass.read();
            ip.add(a);
            if (a == controlSum(ip)) {
                System.out.println("input mass: " + ip);
                final int b = ip.get(0);

                switch (ip.get(1)) {
                    case TESTS_CONNECTION_INDEX: {//тест связи
                        byte[] fs = new byte[]{0};

                        TestCloseOpenRead(b, fs);
                        break;
                    }
                    case OPEN_CHANEL: {//открыть канал связи
                        byte[] fs = new byte[]{0};

                        TestCloseOpenRead(b, fs);
                        break;
                    }
                    case CLOSE_CHANEL: {//закрыть канал связи
                        byte[] fs = new byte[]{0};

                        TestCloseOpenRead(b, fs);
                        break;
                    }
                    case ENERGY_FROM_RESET: {//Энергия от сброса
                        byte[] fs = new byte[16];

                        int Aplus = (int) (getDate() * (Math.random() * Integer.parseInt(prop.getProperty("delta"))));
                        int Aminus = (int) (getDate() * (Math.random() * Integer.parseInt(prop.getProperty("delta"))));
                        int Rplus = (int) (getDate() * (Math.random() * Integer.parseInt(prop.getProperty("delta"))));
                        int Rminus = (int) (getDate() * (Math.random() * Integer.parseInt(prop.getProperty("delta"))));

                        fs[3] = (byte) (Aplus % 256);
                        fs[2] = (byte) (Aplus / 256);
                        fs[1] = (byte) (Aplus / 256 / 256);
                        fs[0] = (byte) (Aplus / 256 / 256 / 256);

                        fs[7] = (byte) (Aminus % 256);
                        fs[6] = (byte) (Aminus / 256);
                        fs[5] = (byte) (Aminus / 256 / 256);
                        fs[4] = (byte) (Aminus / 256 / 256 / 256);

                        fs[11] = (byte) (Rplus % 256);
                        fs[10] = (byte) (Rplus / 256);
                        fs[9] = (byte) (Rplus / 256 / 256);
                        fs[8] = (byte) (Rplus / 256 / 256 / 256);

                        fs[15] = (byte) (Rminus % 256);
                        fs[14] = (byte) (Rminus / 256);
                        fs[13] = (byte) (Rminus / 256 / 256);
                        fs[12] = (byte) (Rminus / 256 / 256 / 256);
//
                        TestCloseOpenRead(b, fs);
                        break;
                    }
                    case READING_PARAMETERS: {//чтение параметров и данных
                        switch (ip.get(2)) {
                            case 5: //сетевой адрес короткий 3й параметр
                                byte[] fs = new byte[]{0, Byte.parseByte(prop.getProperty("setAdress"))};

                                TestCloseOpenRead(b, fs);
                                break;

                            case 18: //постоянная счетчика
                                fs = new byte[]{97, (byte) (64 + Byte.parseByte(prop.getProperty("post_schet"))), 16};

                                TestCloseOpenRead(b, fs);
                                break;

                            case 11: //точка учета
                                fs = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

                                TestCloseOpenRead(b, fs);
                                break;
//
                            case 0: //серийный номер, дата выпуска
                                fs = new byte[]{0, 0, 0, 0, 0, 0, 0};
//
                                fs[0] = (byte) (Integer.parseInt(prop.getProperty("serNumber")) / 256 / 256 / 256);
                                fs[1] = (byte) (Integer.parseInt(prop.getProperty("serNumber")) / 256 / 256);
                                fs[2] = (byte) (Integer.parseInt(prop.getProperty("serNumber")) / 256);
                                fs[3] = (byte) (Integer.parseInt(prop.getProperty("serNumber")) % 256);
                                fs[4] = (byte) (Integer.parseInt(prop.getProperty("day"), 16));
                                fs[5] = (byte) (Integer.parseInt(prop.getProperty("month"), 16));
                                fs[6] = (byte) (Integer.parseInt(prop.getProperty("year"), 16));

                                TestCloseOpenRead(b, fs);
                                break;
//
                            case 2: //коэффиценты трансформации
                                fs = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

                                fs[0] = (byte) ((Integer.parseInt(prop.getProperty("KN"))) / 256);
                                fs[1] = (byte) ((Integer.parseInt(prop.getProperty("KN"))) % 256);
                                fs[2] = (byte) ((Integer.parseInt(prop.getProperty("KT"))) / 256);
                                fs[3] = (byte) ((Integer.parseInt(prop.getProperty("KT"))) % 256);

                                TestCloseOpenRead(b, fs);
                                break;
//
                            case 6: //время интегрирования
                                if (ip.get(3) == 1) {
                                    fs = new byte[]{0, 0};
                                    fs[1] = (byte) (Integer.parseInt(prop.getProperty("time_integr2")));

                                    TestCloseOpenRead(b, fs);
                                    break;

                                } else {
                                    fs = new byte[]{0, 0}; // 1E - 30min
                                    fs[1] = (byte) (Integer.parseInt(prop.getProperty("time_integr")));

                                    TestCloseOpenRead(b, fs);
                                    break;
                                }
//
                            case 4: //Расширенное чтение текущего указателя массива профиля мощности (первого или второго)
                                if (ip.get(3) == 1) {
                                    fs = new byte[]{(byte) 0x86, 0x19, 0x21, 0x11, 0x14, (byte) 0xB1, (byte) 0x48};

                                    TestCloseOpenRead(b, fs);
                                    break;
                                } else {
                                    fs = new byte[]{(byte) 0x80, 0x19, 0x21, 0x11, 0x14, (byte) 0xAB, (byte) 0x98};

                                    TestCloseOpenRead(b, fs);
                                    break;
                                }
//
                            case 1: //Чтение температуры
                                fs = new byte[]{0, 29};
                                TestCloseOpenRead(b, fs);
                                break;
//
                            case 3: //версия ПО
                                fs = new byte[]{2, 50, 48};
                                TestCloseOpenRead(b, fs);
                                break;
//
                            case 9: //Чтение программируемых флагов
                                fs = new byte[]{0, 6};
                                TestCloseOpenRead(b, fs);
                                break;
//
                            case 34: //Чтение числа периодов усреднения для  измерения вспомогательных параметров
                                fs = new byte[]{0, 0x32};
                                TestCloseOpenRead(b, fs);
                                break;
//
                            case 36: //Чтение идентификатора счетчика
                                fs = getID(prop.getProperty("id"));

                                TestCloseOpenRead(b, fs);
                                break;

                            case 14: //Чтение указателя текущего тарифа
                                fs = new byte[]{17, 18, 4, 11, 12, 14, 0};
                                TestCloseOpenRead(b, fs);
                                break;

                            case 13: //Чтение энергии текущего тарифа
                                fs = new byte[]{0, 0, 12, 2, 0, 0, 12, 13, 0, 4, 0, 0, 0, 11, 0, 0};
                                TestCloseOpenRead(b, fs);
                                break;
                        }


                    }

                }


//                if (ip.get(1) == 4) { чтение журналов
//                    byte[] fs = new byte[]{1, 49, 48, 21, 1, 2, 9, 2, 0, 0};
//                    fs[0] = (byte) b;
//                    ArrayList<Integer> ip2 = new ArrayList<Integer>();
//                    for (byte f : fs) {
//                        ip2.add((int) f);
//                    }
//                    fs[9] = (byte) controlSum(ip2);
//                    outputMass.write(fs);
//                }

//                    byte [] fs = new byte[]{5, 0, 0, 39, 21, 0, 0, 21, (byte)174, 0, 0, 50, 100, 0, 0, 16, 10, 127};

//                05h 31h 30h 15h 01h 02h 09h 02h 00h

                ip.clear();

            }
        }

        outputMass.close();
        inputMass.close();
        fromclient.close();
        servers.close();
    }
}