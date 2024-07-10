package youtube.server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final int PORT = 2000;
    private static final ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        ServerSocket server = null;
        try {

            server = new ServerSocket(PORT);
            InetAddress inetAddress = InetAddress.getByName("localhost");
            // SocketAddress socketAddress = new InetSocketAddress(inetAddress, PORT);
            // server.setKeepAlive(true);

            System.out.println("[SERVER] Server started. Waiting for client connections...");

            while (!server.isClosed()){
                Socket client = server.accept();
                System.out.println("[SERVER] Client Connected: " + client.getInetAddress());

                // Give Service to the Client
                ServiceProvider task = new ServiceProvider(client);
                pool.execute(task);
            }

        } catch (IOException e) {
            System.out.println("Error inside server running: " + e.getMessage());
        } finally {
            try {
                if(server != null) server.close();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
            pool.shutdown();
        }
    }

}
