package youtube.server;

import org.json.JSONObject;
import youtube.RequestTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Test {
    private static final String SERVER_IP = "127.0.0.1"; // localhost
    private static final int SERVER_PORT = 1234;
    private static Socket clientsSocket;
    private static PrintWriter outStream; // Output stream to send data to the server
    private static BufferedReader inpStream; // Input stream to receive data from the server

    public static void main(String[] args) {
        try {
            // Connect to server
            clientsSocket = new Socket(SERVER_IP, SERVER_PORT);

            outStream = new PrintWriter(clientsSocket.getOutputStream(), true);
            inpStream = new BufferedReader(new InputStreamReader(clientsSocket.getInputStream()));
            System.out.println("[CLIENT] connected to server.");


            JSONObject requestJson = new JSONObject();
//            var requestTypes = RequestTypes.values();
            requestJson.put("type", RequestTypes.GET_USERINFO.ordinal());
            requestJson.put("body", new JSONObject("{}"));

            outStream.println(requestJson);
            System.out.println("[CLIENT] Request Sent.");

            System.out.println("response: " + inpStream.readLine());

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
