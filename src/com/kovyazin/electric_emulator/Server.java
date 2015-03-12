package com.kovyazin.electric_emulator;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Server {
    public static InputStream in = null;
    public static OutputStream out = null;

    public static int controlSum(ArrayList<Integer> a) {
        int KC = 255;
        int get;
        for (int i = 0; i < a.size() - 1; i++) {
            get = a.get(i);
            KC = KC ^ get;
        }
        return KC;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to com.kovyazin.electric_emulator.Server side");

        ArrayList<Integer> ip = new ArrayList<Integer>();

        ServerSocket servers;
        Socket clientSocket = null;

        servers = new ServerSocket(4444, 0, InetAddress.getByName("localhost"));


        try {
            System.out.print("Waiting for a client...");
            clientSocket = servers.accept();
            System.out.println("com.kovyazin.electric_emulator.Client connected");


        } catch (IOException e) {
            System.out.println("Can't accept");
            System.exit(-1);
        }

        in = clientSocket.getInputStream();
        out = clientSocket.getOutputStream();

        while (in != null) {
            int a = in.read();
            ip.add(a);
            if (a == controlSum(ip)) {
                System.out.println(ip);
                int b = ip.get(0);

                if (ip.get(1) == 0) {
                    byte[] fs = new byte[]{1, 0, -2};
                    fs[0] = (byte) b;
                    ArrayList<Integer> ip2 = new ArrayList();
                    for (int i = 0; i < fs.length; i++) {
                        ip2.add((int) fs[i]);
                    }
                    fs[2] = (byte) controlSum(ip2);
                    out.write(fs);
                }

                if (ip.get(1) == 1) {
                    byte[] fs = new byte[]{1, 0, -2};
                    fs[0] = (byte) b;
                    ArrayList<Integer> ip2 = new ArrayList();
                    for (int i = 0; i < fs.length; i++) {
                        ip2.add((int) fs[i]);
                    }
                    fs[2] = (byte) controlSum(ip2);
                    out.write(fs);
                }

                if (ip.get(1) == 2) {
                    byte[] fs = new byte[]{1, 0, -2};
                    fs[0] = (byte) b;
                    ArrayList<Integer> ip2 = new ArrayList();
                    for (int i = 0; i < fs.length; i++) {
                        ip2.add((int) fs[i]);
                    }
                    fs[2] = (byte) controlSum(ip2);
                    out.write(fs);
                }

                if (ip.get(1) == 4) {
                    byte[] fs = new byte[]{1, 49, 48, 21, 1, 2, 9, 2, 0, 0};
                    fs[0] = (byte) b;
                    ArrayList<Integer> ip2 = new ArrayList();
                    for (int i = 0; i < fs.length; i++) {
                        ip2.add((int) fs[i]);
                    }
                    fs[9] = (byte) controlSum(ip2);
                    out.write(fs);
                }

                if (ip.get(1) == 8) {
                    switch (ip.get(2)) {
                        case 5:
                            byte[] fs = new byte[]{5, 0, 5, 0};
                            fs[0] = (byte) b;
                            ArrayList<Integer> ip2 = new ArrayList();
                            for (int i = 0; i < fs.length; i++) {
                                ip2.add((int) fs[i]);
                            }
                            fs[3] = (byte) controlSum(ip2);
                            out.write(fs);
                            break;

                        case 18:
                            fs = new byte[]{5, 97, 64, 16, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList();
                            for (int i = 0; i < fs.length; i++) {
                                ip2.add((int) fs[i]);
                            }
                            fs[4] = (byte) controlSum(ip2);
                            out.write(fs);
                            break;

                        case 11:
                            fs = new byte[]{5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList();
                            for (int i = 0; i < fs.length; i++) {
                                ip2.add((int) fs[i]);
                            }
                            fs[17] = (byte) controlSum(ip2);
                            out.write(fs);
                            break;

                        case 0:
                            fs = new byte[]{5, 6, 113, 29, 117, 24, 8, 7, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList();
                            for (int i = 0; i < fs.length; i++) {
                                ip2.add((int) fs[i]);
                            }
                            fs[8] = (byte) controlSum(ip2);
                            out.write(fs);
                            break;

                        case 2:
                            fs = new byte[]{5, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList();
                            for (int i = 0; i < fs.length; i++) {
                                ip2.add((int) fs[i]);
                            }
                            fs[10] = (byte) controlSum(ip2);
                            out.write(fs);
                            break;

                        case 6:
                            if (ip.get(3) == 1) {
                                fs = new byte[]{5, 0, 3, 0};
                                fs[0] = (byte) b;
                                ip2 = new ArrayList();
                                for (int i = 0; i < fs.length; i++) {
                                    ip2.add((int) fs[i]);
                                }
                                fs[3] = (byte) controlSum(ip2);
                                out.write(fs);
                                break;

                            } else {
                                fs = new byte[]{5, 0, 0x1E, 0};
                                fs[0] = (byte) b;
                                ip2 = new ArrayList();
                                for (int i = 0; i < fs.length; i++) {
                                    ip2.add((int) fs[i]);
                                }
                                fs[3] = (byte) controlSum(ip2);
                                out.write(fs);
                                break;
                            }

                        case 4:
                            if (ip.get(3) == 1) {
                                fs = new byte[]{5, (byte) 0x86, 0x19, 0x21, 0x11, 0x14, (byte) 0xB1, (byte) 0x48, 0};
                                fs[0] = (byte) b;
                                ip2 = new ArrayList();
                                for (int i = 0; i < fs.length; i++) {
                                    ip2.add((int) fs[i]);
                                }
                                fs[8] = (byte) controlSum(ip2);
                                out.write(fs);
                                break;
                            } else {
                                fs = new byte[]{5, (byte) 0x80, 0x19, 0x21, 0x11, 0x14, (byte) 0xAB, (byte) 0x98};
                                fs[0] = (byte) b;
                                ip2 = new ArrayList();
                                for (int i = 0; i < fs.length; i++) {
                                    ip2.add((int) fs[i]);
                                }
                                fs[7] = (byte) controlSum(ip2);
                                out.write(fs);
                                break;
                            }

                        case 1:
                            fs = new byte[]{5, (byte) 0x80, 0x19, 0x21, 0x11, 0x14, (byte) 0xAB, (byte) 0x98};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList();
                            for (int i = 0; i < fs.length; i++) {
                                ip2.add((int) fs[i]);
                            }
                            fs[7] = (byte) controlSum(ip2);
                            out.write(fs);
                            break;

                        case 3:
                            fs = new byte[]{5, (byte) 0x2, 0x32, 0x30, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList();
                            for (int i = 0; i < fs.length; i++) {
                                ip2.add((int) fs[i]);
                            }
                            fs[4] = (byte) controlSum(ip2);
                            out.write(fs);
                            break;

                        case 9:
                            fs = new byte[]{5, (byte) 0x0, 0x6, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList();
                            for (int i = 0; i < fs.length; i++) {
                                ip2.add((int) fs[i]);
                            }
                            fs[3] = (byte) controlSum(ip2);
                            out.write(fs);
                            break;

                        case 34:
                            fs = new byte[]{5, (byte) 0x0, 0x32, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList();
                            for (int i = 0; i < fs.length; i++) {
                                ip2.add((int) fs[i]);
                            }
                            fs[3] = (byte) controlSum(ip2);
                            out.write(fs);
                            break;

                        case 36:
                            fs = new byte[]{5, (byte) 0x31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList();
                            for (int i = 0; i < fs.length; i++) {
                                ip2.add((int) fs[i]);
                            }
                            fs[33] = (byte) controlSum(ip2);
                            out.write(fs);
                            break;

                    }

                }

                ip.clear();

            }
        }

        out.close();
        in.close();
        clientSocket.close();
        servers.close();
    }
}