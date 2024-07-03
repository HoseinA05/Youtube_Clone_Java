package youtube.client.test;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private final Socket client;
    private final PrintWriter outputStream; // Input stream to receive data from the client
    private final BufferedReader inputStream; // Output stream to send data to the client

    public ClientHandler(Socket client) throws IOException {
        this.client = client;
        this.outputStream = new PrintWriter(client.getOutputStream(), true);
        this.inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }
    @Override
    public void run() {
        try {
            while (true) {
                boolean exitFlag = false;
                String request = this.inputStream.readLine();
                System.out.println("[SERVER] Request Received: " + request);
                switch (request) {
                    case "getVideos":
                        sendVideos();
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error > : " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
    }

    private void sendVideos() {
        System.out.println("Sending Videos");
        JSONObject response = new JSONObject();

        JSONObject resBody = new JSONObject();

        JSONObject video1 = new JSONObject();
        video1.put("title", "Hosein");
        video1.put("views", "123124");
        video1.put("owner", "Hosein Studios");

        JSONObject video2 = new JSONObject();
        video2.put("title", "Testing");
        video2.put("views", "500000");
        video2.put("owner", "Honeypot");
        JSONObject video3 = new JSONObject();
        video3.put("title", "The Making of This project");
        video3.put("views", "80000");
        video3.put("owner", "Mario Brothers");

        response.put("0", video1);
        response.put("1", video2);
        response.put("2", video3);
        response.put("type", "Testing");
        response.put("body", resBody);

        outputStream.println(response.toString());
    }
}