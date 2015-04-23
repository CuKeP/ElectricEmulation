package com.kovyazin.electric_emulator;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class Server {
    public static InputStream inputMass = null;
    public static OutputStream outputMass = null;
    public static Properties prop = new Properties();


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
        long date3 = date2-date1;
        return (int)(date3/1000/60/60);
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
                System.out.println(ip);
                int b = ip.get(0);

                if (ip.get(1) == 0) { //тест связи
                    byte[] fs = new byte[]{1, 0, -2};
                    fs[0] = (byte) b;
                    ArrayList<Integer> ip2 = new ArrayList<Integer>();
                    for (byte f : fs) {
                        ip2.add((int) f);
                    }
                    fs[2] = (byte) controlSum(ip2);
                    outputMass.write(fs);
                }

                if (ip.get(1) == 1) { //открыть канал связи
                    byte[] fs = new byte[]{1, 0, -2};
                    fs[0] = (byte) b;
                    ArrayList<Integer> ip2 = new ArrayList<Integer>();
                    for (byte f : fs) {
                        ip2.add((int) f);
                    }
                    fs[2] = (byte) controlSum(ip2);
                    outputMass.write(fs);
                }

                if (ip.get(1) == 2) { //закрыть канал связи
                    byte[] fs = new byte[]{1, 0, -2};
                    fs[0] = (byte) b;
                    ArrayList<Integer> ip2 = new ArrayList<Integer>();
                    for (byte f : fs) {
                        ip2.add((int) f);
                    }
                    fs[2] = (byte) controlSum(ip2);
                    outputMass.write(fs);
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

                if (ip.get(1) == 5){  //Энергия от сброса
                    byte [] fs = new byte[]{5, 0,0,0,0,  0,0,0,0,  0,0,0,0,  0,0,0,0,  127};
                    byte [] fs1 = new byte[16];
//                    fs[4] = (byte)(getDate() * Math.random() * Integer.parseInt(prop.getProperty("delta")));
//                    fs[8] = (byte)(getDate() * Math.random() * Integer.parseInt(prop.getProperty("delta")));
//                    fs[12] = (byte)(getDate() * Math.random() * Integer.parseInt(prop.getProperty("delta")));
//                    fs[16] = (byte)(getDate() * Math.random() * Integer.parseInt(prop.getProperty("delta")));

                    int Aplus = (int) (getDate() * (Math.random() * Integer.parseInt(prop.getProperty("delta"))));
                    int Aminus = (int) (getDate() * (Math.random() * Integer.parseInt(prop.getProperty("delta"))));
                    int Rplus = (int) (getDate() * (Math.random() * Integer.parseInt(prop.getProperty("delta"))));
                    int Rminus = (int) (getDate() * (Math.random() * Integer.parseInt(prop.getProperty("delta"))));

                    fs1[3] = (byte)(Aplus%256);
                    fs1[2] = (byte)(Aplus/256);
                    fs1[1] = (byte)(Aplus/256/256);
                    fs1[0] = (byte)(Aplus/256/256/256);

                    fs1[7] = (byte)(Aminus%256);
                    fs1[6] = (byte)(Aminus/256);
                    fs1[5] = (byte)(Aminus/256/256);
                    fs1[4] = (byte)(Aminus/256/256/256);

                    fs1[11] = (byte)(Rplus%256);
                    fs1[10] = (byte)(Rplus/256);
                    fs1[9] = (byte)(Rplus/256/256);
                    fs1[8] = (byte)(Rplus/256/256/256);

                    fs1[15] = (byte)(Rminus%256);
                    fs1[14] = (byte)(Rminus/256);
                    fs1[13] = (byte)(Rminus/256/256);
                    fs1[12] = (byte)(Rminus/256/256/256);
//                    fs1[3] = (byte)(1);
//                    fs1[2] = (byte)(3);
//                    fs1[1] = (byte)(4);
//                    fs1[0] = (byte)(5);

//                    for (int i=1; i<fs.length-2; i=i+4){
//                        for (int j=0; j<4; j++){
//                            fs[i+j] = fs1[j];
//                        }
//                    }
                    System.arraycopy( fs1, 0, fs, 1, fs.length-2 );

                    fs[0] = (byte) b;
                    ArrayList <Integer> ip2 = new ArrayList<Integer>();
                    for (byte f : fs) {
                        ip2.add((int) f);
                    }
                    fs[17] = (byte)controlSum(ip2);
                    outputMass.write(fs);
                }
//                5h 00h 00h 27h 11h 00h 00h 00h 00h  00h 00h 13h 89h 00h 00h 00h 65h КС

                if (ip.get(1) == 8) { //чтение параметров и данных
                    byte[] fs1;
                    switch (ip.get(2)) {
                        case 5: //сетевой адрес короткий 3й параметр
                            byte[] fs = new byte[]{5, 0, Byte.parseByte(prop.getProperty("setAdress")), 0};
                            fs[0] = (byte) b;
                            ArrayList<Integer> ip2 = new ArrayList<Integer>();
                            for (byte f : fs) {
                                ip2.add((int) f);
                            }
                            fs[3] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;

                        case 18: //постоянная счетчика
                            fs = new byte[]{5, 97, (byte) (64 + Byte.parseByte(prop.getProperty("post_schet"))), 16, 0};
//                            fs = new byte[]{5,7,4,6,0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList<Integer>();
                            for (byte f : fs) {
                                ip2.add((int) f);
                            }
                            fs[4] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;

                        case 11: //точка учета
                            fs = new byte[]{5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList<Integer>();
                            for (byte f1 : fs) {
                                ip2.add((int) f1);
                            }
                            fs[17] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;
//
                        case 0: //серийный номер, дата выпуска
                            fs = new byte[]{5, 0,0,0,0, 0,0,0, 0};
//
                            fs[1] = (byte) (Integer.parseInt(prop.getProperty("serNumber")) / 256 / 256 / 256);
                            fs[2] = (byte) (Integer.parseInt(prop.getProperty("serNumber")) / 256 / 256);
                            fs[3] = (byte) (Integer.parseInt(prop.getProperty("serNumber")) / 256);
                            fs[4] = (byte) (Integer.parseInt(prop.getProperty("serNumber")) % 256);
                            fs[5] = (byte) (Integer.parseInt(prop.getProperty("day"), 16));
                            fs[6] = (byte) (Integer.parseInt(prop.getProperty("month"), 16));
                            fs[7] = (byte) (Integer.parseInt(prop.getProperty("year"), 16));

                            fs[0] = (byte) b;
                            ip2 = new ArrayList<Integer>();
                            for (byte f1 : fs) {
                                ip2.add((int) f1);
                            }
                            fs[8] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;
//
                        case 2: //коэффиценты трансформации
                            fs = new byte[]{5, 0,0, 0,0, 0,0,0, 0,0,0, 0};

                            fs[1] = (byte) ((Integer.parseInt(prop.getProperty("KN"))) / 256);
                            fs[2] = (byte) ((Integer.parseInt(prop.getProperty("KN"))) % 256);
                            fs[3] = (byte) ((Integer.parseInt(prop.getProperty("KT"))) / 256);
                            fs[4] = (byte) ((Integer.parseInt(prop.getProperty("KT"))) % 256);

                            ip2 = new ArrayList<Integer>();
                            for (byte f1 : fs) {
                                ip2.add((int) f1);
                            }
                            fs[11] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;
//
                        case 6: //время интегрирования
                            if (ip.get(3) == 1) {
                                fs = new byte[]{5, 0, 0, 0};
                                fs[0] = (byte) b;
                                fs[2] = (byte) (Integer.parseInt(prop.getProperty("time_integr2")));

                                ip2 = new ArrayList<Integer>();
                                for (byte f : fs) {
                                    ip2.add((int) f);
                                }
                                fs[3] = (byte) controlSum(ip2);
                                outputMass.write(fs);
                                break;

                            } else {
                                fs = new byte[]{5, 0, 0, 0}; // 1E - 30min
                                fs[0] = (byte) b;
                                fs[2] = (byte) (Integer.parseInt(prop.getProperty("time_integr")));

                                ip2 = new ArrayList<Integer>();
                                for (byte f : fs) {
                                    ip2.add((int) f);
                                }
                                fs[3] = (byte) controlSum(ip2);
                                outputMass.write(fs);
                                break;
                            }
//
                        case 4: //Расширенное чтение текущего указателя массива профиля мощности (первого или второго)
                            if (ip.get(3) == 1) {
                                fs = new byte[]{5, (byte) 0x86, 0x19, 0x21, 0x11, 0x14, (byte) 0xB1, (byte) 0x48, 0};
                                fs[0] = (byte) b;
                                ip2 = new ArrayList<Integer>();
                                for (byte f : fs) {
                                    ip2.add((int) f);
                                }
                                fs[8] = (byte) controlSum(ip2);
                                outputMass.write(fs);
                                break;
                            } else {
                                fs = new byte[]{5, (byte) 0x80, 0x19, 0x21, 0x11, 0x14, (byte) 0xAB, (byte) 0x98};
                                fs[0] = (byte) b;
                                ip2 = new ArrayList<Integer>();
                                for (byte f : fs) {
                                    ip2.add((int) f);
                                }
                                fs[7] = (byte) controlSum(ip2);
                                outputMass.write(fs);
                                break;
                            }
//
                        case 1: //Чтение температуры
                            fs = new byte[]{5, (byte) 0x80, 0x19, 0x21, 0x11, 0x14, (byte) 0xAB, (byte) 0x98};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList<Integer>();
                            for (byte f : fs) {
                                ip2.add((int) f);
                            }
                            fs[7] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;
//
                        case 3: //версия ПО
                            fs = new byte[]{5, (byte) 0x2, 0x32, 0x30, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList<Integer>();
                            for (byte f : fs) {
                                ip2.add((int) f);
                            }
                            fs[4] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;
//
                        case 9: //Чтение программируемых флагов
                            fs = new byte[]{5, (byte) 0x0, 0x6, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList<Integer>();
                            for (byte f : fs) {
                                ip2.add((int) f);
                            }
                            fs[3] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;
//
                        case 34: //Чтение числа периодов усреднения для  измерения вспомогательных параметров
                            fs = new byte[]{5, (byte) 0x0, 0x32, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList<Integer>();
                            for (byte f : fs) {
                                ip2.add((int) f);
                            }
                            fs[3] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;
//
                        case 36: //Чтение идентификатора счетчика
                            fs = new byte[]{5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                            fs1 = getID(prop.getProperty("id"));
                            System.arraycopy(fs1, 0, fs, 1, fs1.length);

                            fs[0] = (byte) b;
                            ip2 = new ArrayList<Integer>();
                            for (byte f : fs) {
                                ip2.add((int) f);
                            }
                            fs[33] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;

                        case 14: //Чтение указателя текущего тарифа
                            fs = new byte[]{5, 17, 18, 4, 11, 12, 14, 0, 127};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList<Integer>();
                            for (byte f : fs) {
                                ip2.add((int) f);
                            }
                            fs[8] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;

                        case 13: //Чтение энергии текущего тарифа
                            fs = new byte[]{5, 0,0,12,2, 0,0,12,13, 0,4,0,0, 0,11,0,0, 127};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList<Integer>();
                            for (byte f : fs) {
                                ip2.add((int) f);
                            }
                            fs[17] = (byte) controlSum(ip2);
                            outputMass.write(fs);
                            break;
                    }

                }
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