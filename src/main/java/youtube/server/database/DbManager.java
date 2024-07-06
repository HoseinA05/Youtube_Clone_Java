package youtube.server.database;

import java.sql.*;

public class DbManager {
    private static DbManager db = null;
    private static Connection connection;
    private DbManager() {
        // Establish the connection to database
        String addr = "jdbc:postgresql://localhost:5432/";
        String database = "youtube";
        String user = "postgres";
        String password = "h";

        try {
            Class.forName("org.postgresql.Driver");
            DbManager.connection = DriverManager.getConnection(addr + database, user, password);

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error During Getting Connection to Database: " + e.getMessage());
        }
    }

    public static Connection db(){
        if(db == null)
            db = new DbManager();
        return connection;
    }
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error during Closing database Connection: " + e.getMessage());
            }
        }
    }
}