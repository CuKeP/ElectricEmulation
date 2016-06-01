package com.kovyazin.electric_emulator;

import com.kovyazin.electric_emulator.request_handler.*;

import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class Server {

    public static int globalNmass = 0 ;
    public static int n = 0 ;
    public static ResultSet res = null;
    public static byte[] profilesMass = new byte[1000000];
    public static int globalI = 0;
    public static int globalI2 = 0;

    public static final int RH_TESTS_CONNECTION_INDEX = 0;
    public static final int RH_OPEN_CHANEL = 1;
    public static final int RH_CLOSE_CHANEL = 2;
    public static final int RH_ENERGY_FROM_RESET = 5;
    public static final int RH_READING_PARAMETERS = 8;
    public static final int RH_RECORDING_PARAMETERS = 3;
    public static final int RH_READ_PHYSICAL_ADDRESS_EXTENDED = 12; //2.4.4.2	Расширенный запрос на чтение информации по физическим адресам физиче-ской памяти
    public static final int RH_READ_PHYSICAL_ADDRESS_SHORT = 6; //2.4.4.1	Короткий запрос на чтение информации по физическим адресам физической памяти

    private static final int INDEX_NETWORK_ADDRESS = 0;
    private static final int INDEX_REQUEST_CODE = 1;

    public static InputStream clientInputStream = null;
    public static OutputStream clientOutputStream = null;
    public static Properties prop = new Properties();
    public static byte[] longByte = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  //128 length
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    public static ArrayList<Integer> inputBytes = new ArrayList<Integer>();

    public static void main(String[] args) throws IOException, ParseException, SQLException, ClassNotFoundException {
        //FIXME: Close the stream

        Scanner sc = new Scanner(System.in);
        System.out.println("Сгенерировать новые значения энергий? y/n?");
        sc.hasNext();
        if (sc.nextLine().toString().toLowerCase().equals("y")){
            System.out.println("На сколько дней назад сгенерировать энергии?");
            sc.hasNextInt();
            n = sc.nextInt(); // количество дней сгенерированных ранее
            GenerationEnergyN.dbselect();
        } else {
            System.out.println("yy");
        }

        GenerationEnergy.start();

        prop.load(new InputStreamReader(new FileInputStream("example.properties"), "UTF-8"));
//        System.out.println("data=" + CommonUtils.getDateDelta());

        System.out.println("Welcome to Server side");

        ServerSocket serverSocket = new ServerSocket(4444, 0, InetAddress.getByName("localhost"));

        System.out.println("Waiting for a client...");
        Socket clientSocket = waitClient(serverSocket);

        if (null == clientSocket) {
            //FIXME: close serverSocket
            System.out.println("Can't accept");
            return;
        }
        System.out.println("A client is connected");
        GetEnergyProfiles.getEnergy();

        clientInputStream = clientSocket.getInputStream();
        clientOutputStream = clientSocket.getOutputStream();

        while (clientInputStream != null) {

            clientInputStream.read(longByte);
            CommonUtils.convertStreamToByte(longByte); //читает из стрима массив байтов запроса и конвертирует в Input Bytes
            System.out.println("input bytes: " + inputBytes);

            RequestHandler requestHandler = getRequestHandler(inputBytes.get(INDEX_REQUEST_CODE));
            byte[] outputData = requestHandler.handle(inputBytes);
            sendDataToClient(inputBytes.get(INDEX_NETWORK_ADDRESS), outputData);

            inputBytes.clear();
        }

        System.out.println("close SERVER!");
        clientOutputStream.close();
        clientInputStream.close();
        clientSocket.close();
        serverSocket.close();
    }

    private static RequestHandler getRequestHandler(int requestCode) throws IOException {
        switch (requestCode) {
            case RH_OPEN_CHANEL:
                return new OpenChannelRequestHandler();
            case RH_CLOSE_CHANEL:
                return new CloseChannelRequestHandler();
            case RH_TESTS_CONNECTION_INDEX:
                return new TestChannelRequestHandler();
            case RH_ENERGY_FROM_RESET:
                return new EnergyFromResetRequestHandler(prop.getProperty("data_otscheta"),
                        Integer.parseInt(prop.getProperty("delta")), inputBytes.get(3), inputBytes.get(2));
            case RH_READING_PARAMETERS:
                return new ReadParamsRequestHandler();
            case RH_RECORDING_PARAMETERS:
                try {
                    prop.load(new InputStreamReader(new FileInputStream("example.properties"), "UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new RecParamsRequestHandler();
            case RH_READ_PHYSICAL_ADDRESS_EXTENDED:
                return new ReadPhysicalAddressRequestHandler(res);
            case RH_READ_PHYSICAL_ADDRESS_SHORT:
                return new ReadPhysicalAddressShortRequestHandler();
            default:
                throw new IllegalArgumentException("Unknown request code");
        }
    }

    private static Socket waitClient(ServerSocket serverSocket) {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            return null;
        }
    }

    public static void sendDataToClient(int counterNetworkAddress, byte[] outputData) throws IOException {
        // Add two elements for network address and control sum
        byte[] counterAnswerArray = new byte[outputData.length + 2];

        counterAnswerArray[INDEX_NETWORK_ADDRESS] = (byte) counterNetworkAddress;
        System.arraycopy(outputData, 0, counterAnswerArray, 1, outputData.length);

        // Calculate control sum value
        ArrayList<Integer> controlSumElements = new ArrayList<Integer>();
        for (int k : counterAnswerArray) {
            controlSumElements.add(k);
        }
//        byte controlSum = (byte) controlSum(controlSumElements);

        // Set control sum value
//        counterAnswerArray[outputData.length + 1] = controlSum;
//        System.arraycopy(outputData,0,controlSum(controlSumElements),0,controlSum(controlSumElements).length-1);

        System.out.println("output mass: " + Arrays.toString(controlSum(controlSumElements)) + "\n");
        clientOutputStream.write(controlSum(controlSumElements));
    }

    public static byte[] controlSum(ArrayList<Integer> list) {
        byte[] array = new byte[list.size() + 1];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).byteValue();
        }
        CRC16.fillCrc16(array);
        return array;
    }
}