import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.sql.*;

public class Server {
    public static InputStream in = null;
    public static OutputStream out = null;

    public static int controlSum(ArrayList<Integer> a){
        int KC = 255;
        int get = 0;
        for (int i=0; i<a.size()-1; i++){
            get = a.get(i);
            KC =  KC ^ get;
        }
//        System.out.println("control sum: "+KC);
        return KC;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to Server side");

        ArrayList <Integer> ip = new ArrayList ();

        ServerSocket servers = null;
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

        in = fromclient.getInputStream();
        out = fromclient.getOutputStream();

        while (in != null) {
            int a = in.read();
            ip.add(a);
            if(a == controlSum(ip)){
                System.out.println(ip);
                int b = ip.get(0);

                if (ip.get(1) == 0){
                    byte [] fs = new byte[]{1, 0, -2};
                    fs[0] = (byte) b;
                    ArrayList <Integer> ip2 = new ArrayList ();
                    for (int i=0; i<fs.length; i++){
                        ip2.add((int)fs[i]);
                    }
                    fs[2] = (byte)controlSum(ip2);
                    out.write(fs);
                }

                if (ip.get(1) == 1){
                    byte [] fs = new byte[]{1, 0, -2};
                    fs[0] = (byte) b;
                    ArrayList <Integer> ip2 = new ArrayList ();
                    for (int i=0; i<fs.length; i++){
                        ip2.add((int)fs[i]);
                    }
                    fs[2] = (byte)controlSum(ip2);
                    out.write(fs);
                }

                if (ip.get(1) == 2){
                    byte [] fs = new byte[]{1, 0, -2};
                    fs[0] = (byte) b;
                    ArrayList <Integer> ip2 = new ArrayList ();
                    for (int i=0; i<fs.length; i++){
                        ip2.add((int)fs[i]);
                    }
                    fs[2] = (byte)controlSum(ip2);
                    out.write(fs);
                }

                if (ip.get(1) == 4){
                    byte [] fs = new byte[]{1, 49, 48, 21, 1, 2, 9, 2, 0, 0};
                    fs[0] = (byte) b;
                    ArrayList <Integer> ip2 = new ArrayList ();
                    for (int i=0; i<fs.length; i++){
                        ip2.add((int)fs[i]);
                    }
                    fs[9] = (byte)controlSum(ip2);
                    out.write(fs);
                }

                if (ip.get(1) == 8){
                    switch (ip.get(2)){
                        case 5:
                            byte [] fs = new byte[]{5,0,5,0};
                            fs[0] = (byte) b;
                            ArrayList <Integer> ip2 = new ArrayList ();
                            for (int i=0; i<fs.length; i++){
                                ip2.add((int)fs[i]);
                            }
                            fs[3] = (byte)controlSum(ip2);
                            out.write(fs);
                            break;

                        case 18:
                            fs = new byte[]{5,97,64,16,0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList ();
                            for (int i=0; i<fs.length; i++){
                                ip2.add((int)fs[i]);
                            }
                            fs[4] = (byte)controlSum(ip2);
                            out.write(fs);
                            break;

                        case 11:
                            fs = new byte[]{5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList ();
                            for (int i=0; i<fs.length; i++){
                                ip2.add((int)fs[i]);
                            }
                            fs[17] = (byte)controlSum(ip2);
                            out.write(fs);
                            break;

                        case 0:
                            fs = new byte[]{5,6,113,29,117,24,8,7,0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList ();
                            for (int i=0; i<fs.length; i++){
                                ip2.add((int)fs[i]);
                            }
                            fs[8] = (byte)controlSum(ip2);
                            out.write(fs);
                            break;

                        case 2:
                            fs = new byte[]{5,0,1,0,1,0,0,0,0,0,0,0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList ();
                            for (int i=0; i<fs.length; i++){
                                ip2.add((int)fs[i]);
                            }
                            fs[10] = (byte)controlSum(ip2);
                            out.write(fs);
                            break;

                        case 6:
                            if (ip.get(3)==1){
                                fs = new byte[]{5,0,3,0};
                                fs[0] = (byte) b;
                                ip2 = new ArrayList ();
                                for (int i=0; i<fs.length; i++){
                                    ip2.add((int)fs[i]);
                                }
                                fs[3] = (byte)controlSum(ip2);
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
                            if (ip.get(3)==1){
                                fs = new byte[]{5, (byte) 0x86,0x19,0x21,0x11,0x14, (byte) 0xB1, (byte) 0x48, 0};
                                fs[0] = (byte) b;
                                ip2 = new ArrayList ();
                                for (int i=0; i<fs.length; i++){
                                    ip2.add((int)fs[i]);
                                }
                                fs[8] = (byte)controlSum(ip2);
                                out.write(fs);
                                break;
                            }else {
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
                            fs = new byte[]{5, (byte) 0x80,0x19,0x21,0x11,0x14, (byte) 0xAB, (byte) 0x98};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList ();
                            for (int i=0; i<fs.length; i++){
                                ip2.add((int)fs[i]);
                            }
                            fs[7] = (byte)controlSum(ip2);
                            out.write(fs);
                            break;

                        case 3:
                            fs = new byte[]{5, (byte) 0x2,0x32,0x30,0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList ();
                            for (int i=0; i<fs.length; i++){
                                ip2.add((int)fs[i]);
                            }
                            fs[4] = (byte)controlSum(ip2);
                            out.write(fs);
                            break;

                        case 9:
                            fs = new byte[]{5, (byte) 0x0,0x6,0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList ();
                            for (int i=0; i<fs.length; i++){
                                ip2.add((int)fs[i]);
                            }
                            fs[3] = (byte)controlSum(ip2);
                            out.write(fs);
                            break;

                        case 34:
                            fs = new byte[]{5, (byte) 0x0,0x32,0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList ();
                            for (int i=0; i<fs.length; i++){
                                ip2.add((int)fs[i]);
                            }
                            fs[3] = (byte)controlSum(ip2);
                            out.write(fs);
                            break;

                        case 36:
                            fs = new byte[]{5, (byte) 0x31,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                            fs[0] = (byte) b;
                            ip2 = new ArrayList ();
                            for (int i=0; i<fs.length; i++){
                                ip2.add((int)fs[i]);
                            }
                            fs[33] = (byte)controlSum(ip2);
                            out.write(fs);
                            break;

                    }

                }
//                    byte [] fs = new byte[]{5, 0, 0, 39, 21, 0, 0, 21, (byte)174, 0, 0, 50, 100, 0, 0, 16, 10, 127};

//                05h 31h 30h 15h 01h 02h 09h 02h 00h

                ip.clear();

            }
        }

        out.close();
        in.close();
        fromclient.close();
        servers.close();
    }
}