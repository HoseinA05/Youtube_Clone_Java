package youtube.client;

import javafx.concurrent.Task;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class DataService {
    private static final String SERVER_IP = "127.0.0.1"; // localhost
    private static final int SERVER_PORT = 2345;
    private Socket socket;
    private PrintWriter outStream; // Output stream to send data to the server
    private BufferedReader inpStream; // Input stream to receive data from the server

    public DataService() {
        try {
            // Connect to server
            socket = new Socket(SERVER_IP, SERVER_PORT);

            outStream = new PrintWriter(socket.getOutputStream(), true);
            inpStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // public Task<ArrayList<Video>> getNewHomepageVideos(){
    //     return new Task<ArrayList<Video>>() {
    //         @Override
    //         protected ArrayList<Video> call() throws Exception {
    //
    //             outStream.println("getVideos");
    //             System.out.println("Send The Request");
    //             Thread.sleep(3000);
    //             String response = "Testing";
    //             try {
    //                 response = inpStream.readLine();
    //             } catch (IOException e) {
    //                 System.out.println(e.getMessage());
    //             }
    //             System.out.println("Got The Response");
    //
    //             return Video.parseJsonToVideos(response);
    //         }
    //     };
    // }
}
