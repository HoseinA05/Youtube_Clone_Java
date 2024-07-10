package youtube.server;


import youtube.server.controllers.Helper;

import java.io.*;
import java.net.Socket;


public class ServiceProvider implements Runnable {

    private final Socket client;

    private final PrintWriter outputStream; // Output stream to send data to the client
    private final BufferedReader inputStream; // Input stream to receive data from the client

    // private final ObjectOutputStream outputStream; // Output stream to send data to the client
    // private final ObjectInputStream inputStream; // Input stream to receive data from the client

    public ServiceProvider(Socket client) throws IOException {
        this.client = client;
        this.outputStream = new PrintWriter(client.getOutputStream(), true);
        this.inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));

        // this.outputStream = new ObjectOutputStream(client.getOutputStream());
        // this.inputStream = new ObjectInputStream(client.getInputStream());
    }

    @Override
    public void run() {
        Router router = new Router();

        while (!client.isClosed()) {

            try {
                String request = this.inputStream.readLine();
                // String request = this.inputStream.readUTF();
                sendResponse(router, request);
            } catch (IOException e) {
                System.out.println("Error in getting request: " + e.getMessage());
                e.printStackTrace();
                try {
                    client.close();
                } catch (IOException e2) {
                    System.out.println("Error in closing socket: " + e2.getMessage());
                }
            }
        }

    }

    private void sendResponse(Router router, String request) throws IOException {

        try {
            String response = router.handleRequest(request);
            // System.out.println(response);
            outputStream.println(response);
            outputStream.flush();
            // outputStream.writeUTF(response);
            // outputStream.flush();
        } catch (Exception e) {
            System.out.println(Helper.errorToJson(e));
            outputStream.println(Helper.errorToJson(e));
            // outputStream.writeUTF(Helper.errorToJson(e));
        }
    }

}
