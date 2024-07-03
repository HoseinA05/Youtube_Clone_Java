package youtube.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import youtube.YoutubeApplication;

public class DashboardpageController {

    @FXML
    public VBox leftPanelMax;
    @FXML
    public VBox leftPanelMin;

    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    public void initialize(){
        // Don't show left panel by default in video page.
        leftPanelMax.setVisible(false);
        leftPanelMax.setManaged(false);

    }
    @FXML
    public void leftPanelBtnHandler(ActionEvent actionEvent) {
        leftPanelMin.setVisible(!leftPanelMin.isVisible());
        leftPanelMin.setManaged(!leftPanelMin.isManaged());
    }

    @FXML
    public void toggleTheme(ActionEvent actionEvent) {
        if (leftPanelMin.getScene().getStylesheets().contains(darkTheme)){
            leftPanelMin.getScene().getStylesheets().remove(darkTheme);
            leftPanelMin.getScene().getStylesheets().add(lightTheme);
        }else {
            leftPanelMin.getScene().getStylesheets().remove(lightTheme);
            leftPanelMin.getScene().getStylesheets().add(darkTheme);
        }
    }
}
