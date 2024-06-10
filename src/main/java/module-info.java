module sbu.cs.youtube {
    requires javafx.controls;
    requires javafx.fxml;


    opens youtube to javafx.fxml;
    exports youtube;
}