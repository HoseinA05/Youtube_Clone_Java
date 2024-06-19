module sbu.cs.youtube {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens youtube to javafx.fxml;
    exports youtube;
}