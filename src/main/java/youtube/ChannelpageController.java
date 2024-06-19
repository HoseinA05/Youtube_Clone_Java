package youtube;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class ChannelpageController {

    @FXML
    public VBox leftPanelMin;
    @FXML
    public VBox leftPanelMax;
    @FXML
    public ImageView channelImage;
    @FXML
    public ImageView userImg;
    @FXML
    public ImageView videoImg;

    private final String darkTheme = getClass().getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = getClass().getResource("styles/light-theme.css").toExternalForm();

    public void initialize(){
        // Show The Left Menu in maximized mode at first
        leftPanelMax.setVisible(false);
        leftPanelMax.setManaged(false);

        // Need to be Called At some other point
        // Rectangle clip1 = new Rectangle(channelImage.getFitWidth(), channelImage.getFitHeight());
        // clip1.setArcWidth(20);
        // clip1.setArcHeight(20);
        // channelImage.setClip(clip1);

        Rectangle clip2 = new Rectangle(userImg.getFitWidth(), userImg.getFitHeight());
        clip2.setArcWidth(130);
        clip2.setArcHeight(130);
        userImg.setClip(clip2);

        Rectangle clip3 = new Rectangle(videoImg.getFitWidth(), videoImg.getFitHeight());
        clip3.setArcWidth(20);
        clip3.setArcHeight(20);
        videoImg.setClip(clip3);
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