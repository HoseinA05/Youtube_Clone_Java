package youtube.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import youtube.YoutubeApplication;

public class PlaylistpageController {
    @FXML
    public VBox leftPanelMin;
    @FXML
    public VBox leftPanelMax;
    @FXML
    public ImageView plImage;
    @FXML
    public ImageView plVideoImage;

    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    public void initialize(){
        // Show The Left Menu in maximized mode at first
        leftPanelMin.setVisible(false);
        leftPanelMin.setManaged(false);

        Rectangle clip1 = new Rectangle(plImage.getFitWidth(), plImage.getFitHeight());
        clip1.setArcWidth(20);
        clip1.setArcHeight(20);
        plImage.setClip(clip1);

        Rectangle clip2 = new Rectangle(plVideoImage.getFitWidth(), plVideoImage.getFitHeight());
        clip2.setArcWidth(20);
        clip2.setArcHeight(20);
        plVideoImage.setClip(clip2);

    }

    @FXML
    public void leftPanelBtnHandler(ActionEvent actionEvent) {
        leftPanelMax.setVisible(!leftPanelMax.isVisible());
        leftPanelMax.setManaged(!leftPanelMax.isManaged());

        leftPanelMin.setVisible(!leftPanelMin.isVisible());
        leftPanelMin.setManaged(!leftPanelMin.isManaged());
    }

    @FXML
    public void toggleTheme(ActionEvent actionEvent) {
        if (leftPanelMax.getScene().getStylesheets().contains(darkTheme)){
            leftPanelMax.getScene().getStylesheets().remove(darkTheme);
            leftPanelMax.getScene().getStylesheets().add(lightTheme);
        }else {
            leftPanelMax.getScene().getStylesheets().remove(lightTheme);
            leftPanelMax.getScene().getStylesheets().add(darkTheme);
        }
    }

}
