module sbu.cs.youtube {
    requires org.json;
    requires com.google.gson;

    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.desktop;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    exports youtube;
    exports youtube.client.controllers;
    exports youtube.client;
    opens youtube to javafx.fxml;
    opens youtube.client.controllers to javafx.fxml;
    opens youtube.client to javafx.fxml;
    opens youtube.server.models to com.google.gson;
    opens youtube.server.controllers to com.google.gson;
}
