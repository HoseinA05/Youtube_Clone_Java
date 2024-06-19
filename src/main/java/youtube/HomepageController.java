package youtube;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class HomepageController {

    @FXML
    public ImageView img;
    @FXML
    public ImageView profImg;
    @FXML
    public VBox leftPanelMax;
    @FXML
    public VBox leftPanelMin;
    private final String darkTheme = getClass().getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = getClass().getResource("styles/light-theme.css").toExternalForm();

    @FXML
    public void initialize() {
        // Show The Left Menu in maximized mode at first
        leftPanelMin.setVisible(false);
        leftPanelMin.setManaged(false);

        // Making Video Thumbnail and prof Image Rounded.
        Rectangle clip = new Rectangle(img.getFitWidth(), img.getFitHeight());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        img.setClip(clip);

        Rectangle clip2 = new Rectangle(profImg.getFitWidth(), profImg.getFitHeight());
        clip2.setArcWidth(50);
        clip2.setArcHeight(50);
        profImg.setClip(clip2);
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