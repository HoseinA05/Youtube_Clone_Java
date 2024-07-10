package youtube.client;

import youtube.server.models.User;


import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class RequestHandler {

    private static final String SERVER_IP = "127.0.0.1"; // localhost
    private static final int SERVER_PORT = 2000;
    private static Socket socket;
    // private static ObjectOutputStream outStream; // Output stream to send data to the server
    // private static ObjectInputStream inpStream; // Input stream to receive data from the server

    private static PrintWriter outStream; // Output stream to send data to the server
    private static BufferedReader inpStream; // Input stream to receive data from the server

    private RequestHandler() {
    }



    public static void connectToServer() {
        try {
            System.out.println("trying to connect to server " + SERVER_IP + ":" + SERVER_PORT);
            socket = new Socket();
            InetAddress inetAddress = InetAddress.getByName("localhost");
            SocketAddress socketAddress = new InetSocketAddress(inetAddress, SERVER_PORT);
            socket.setKeepAlive(true);
            socket.setReuseAddress(true);
            socket.connect(socketAddress, 0);

            System.out.println("Connected to server!");

            // outStream = new ObjectOutputStream(socket.getOutputStream());
            // outStream.flush();
            // inpStream = new ObjectInputStream(socket.getInputStream());
            outStream = new PrintWriter(socket.getOutputStream(), true);
            inpStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException("connection to server failed");
        }
    }

    // public RequestHandler() {
    //     try {
    //         // Connect to server
    //         socket = new Socket(SERVER_IP, SERVER_PORT);
    //
    //         outStream = new PrintWriter(socket.getOutputStream(), true);
    //         inpStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    //         System.out.println("Connected to server!");
    //
    //     } catch (IOException e) {
    //         System.out.println("Error: " + e.getMessage());
    //     }
    // }

    public void test() {
        System.out.println(socket.getLocalSocketAddress());
        System.out.println(socket.isConnected());
    }


    public static synchronized String sendRequest(String requestBody) {
        outStream.println(requestBody);
        outStream.flush();
        // try {
        //     // outStream.writeUTF(requestBody);
        //     // outStream.flush();
        // } catch (IOException e) {
        //     System.out.println(e.getMessage());
        //     throw new RuntimeException(e);
        // }

        String response = null;
        try {
            // response = inpStream.readUTF();
            response = inpStream.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        // phaser.arriveAndAwaitAdvance();

        return response;
    }
}
