module sbu.cs.youtube {
    requires org.json;

    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.desktop;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.json;


    opens youtube to javafx.fxml;
    exports youtube;
    exports youtube.client.controllers;
    opens youtube.client.controllers to javafx.fxml;
    exports youtube.client;
    opens youtube.client to javafx.fxml;
}