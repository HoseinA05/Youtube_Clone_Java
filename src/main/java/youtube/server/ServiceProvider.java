package youtube.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ServiceProvider implements Runnable{

    private final Socket client;
    private final PrintWriter outputStream; // Output stream to send data to the client
    private final BufferedReader inputStream; // Input stream to receive data from the client

    public ServiceProvider(Socket client) throws IOException {
        this.client = client;
        this.outputStream = new PrintWriter(client.getOutputStream(), true);
        this.inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    @Override
    public void run() {
        Router router = new Router();

        while (client.isConnected()) {

            try {
                String request = this.inputStream.readLine();
                sendResponse(router, request);

                return;
            } catch (IOException e) {
                    System.out.println("Error in getting request: " + e.getMessage());
                    e.printStackTrace();
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    System.out.println("Error in closing socket: " + e.getMessage());
                }

            }
        }

    }

    private void sendResponse(Router router, String request) {
        try {
            String response = router.handleRequest(request);
            outputStream.println(response);
        } catch (Exception e) {
            System.out.println("sever error: " + e.getMessage());
            outputStream.println("Something went wrong");
        }
    }

}
