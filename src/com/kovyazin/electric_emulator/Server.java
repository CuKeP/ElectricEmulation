package com.kovyazin.electric_emulator;

import com.kovyazin.electric_emulator.request_handler.*;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class Server {

    public static final int RH_TESTS_CONNECTION_INDEX = 0x0;
    public static final int RH_OPEN_CHANEL = 1;
    public static final int RH_CLOSE_CHANEL = 2;
    public static final int RH_ENERGY_FROM_RESET = 5;
    public static final int RH_READING_PARAMETERS = 8;
    public static final int RH_RECORDING_PARAMETERS = 3;

    private static final int INDEX_NETWORK_ADDRESS = 0;
    private static final int INDEX_REQUEST_CODE = 1;

    public static InputStream clientInputStream = null;
    public static OutputStream clientOutputStream = null;
    public static Properties prop = new Properties();

    public static void main(String[] args) throws IOException, ParseException, SQLException {

        //FIXME: Close the stream
        prop.load(new InputStreamReader(new FileInputStream("example.properties"), "UTF-8"));

        System.out.println("Welcome to Server side");

        ArrayList<Integer> inputBytes = new ArrayList<Integer>();

        ServerSocket serverSocket = new ServerSocket(4444, 0, InetAddress.getByName("localhost"));

        System.out.println("Waiting for a client...");
        Socket clientSocket = waitClient(serverSocket);
        if (null == clientSocket) {
            //FIXME: close serverSocket
            System.out.println("Can't accept");
            return;
        }
        System.out.println("A client is connected");

        clientInputStream = clientSocket.getInputStream();
        clientOutputStream = clientSocket.getOutputStream();

        while (clientInputStream != null) {
            int value = clientInputStream.read();
            inputBytes.add(value);
            if (value == controlSum(inputBytes)) {
                System.out.println("input bytes: " + inputBytes);

                RequestHandler requestHandler = getRequestHandler(inputBytes.get(INDEX_REQUEST_CODE));
                byte[] outputData = requestHandler.handle(inputBytes);
                sendDataToClient(inputBytes.get(INDEX_NETWORK_ADDRESS), outputData);

                inputBytes.clear();
            }
        }
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
                        Integer.parseInt(prop.getProperty("delta")));
            case RH_READING_PARAMETERS:
                return new ReadParamsRequestHandler();
            case RH_RECORDING_PARAMETERS:
                try {
                    prop.load(new InputStreamReader(new FileInputStream("example.properties"), "UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new RecParamsRequestHandler();
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
        byte controlSum = (byte) controlSum(controlSumElements);

        // Set control sum value
        counterAnswerArray[outputData.length + 1] = controlSum;

        System.out.println("output mass: " + Arrays.toString(counterAnswerArray) + "\n");
        clientOutputStream.write(counterAnswerArray);
    }

    public static int controlSum(ArrayList<Integer> list) {
        int KC = 255;
        int k;
        for (int i = 0; i < list.size() - 1; i++) {
            k = list.get(i);
            KC = KC ^ k;
        }
        return KC;
    }
}