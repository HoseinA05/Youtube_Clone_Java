package youtube.client.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static final int PORT = 2345;
    private static final ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {

        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);
            System.out.println("[SERVER] Server started. Waiting for client connections...");

            while (true) {
                // Accept a client
                Socket client = server.accept();
                System.out.println("[SERVER] Client Connected: " + client.getInetAddress());

                // Give Service to the Client
                ClientHandler ch = new ClientHandler(client);
                pool.execute(ch);
            }

        } catch (IOException e) {
            System.out.println("Error ? : " + e.getMessage());
        } finally {
            try {
                if (server != null) server.close();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
            pool.shutdown();
        }
    }
}