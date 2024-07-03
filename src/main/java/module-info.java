module sbu.cs.youtube {
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
    exports youtube.client.test;
    opens youtube.client.test to javafx.fxml;
}