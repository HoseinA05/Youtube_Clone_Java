package youtube.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import youtube.YoutubeApplication;
import youtube.client.DataService;
import youtube.client.Navigator;

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

    private DataService dataService;

    private boolean isDarkmode = false;
    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    public ChannelpageController(DataService dataService, boolean isDarkmode) {
        this.dataService = dataService;
        this.isDarkmode = isDarkmode;
    }

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


    public void setTheme(boolean isDarkmode){
        this.isDarkmode = isDarkmode;
    }

    @FXML
    public void homeBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoHomePage((Stage) (leftPanelMax).getScene().getWindow(), dataService, isDarkmode);
    }
    @FXML
    public void signinBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoSigninPage((Stage) (leftPanelMax).getScene().getWindow(), dataService, isDarkmode);
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
        if (leftPanelMin.getScene().getStylesheets().contains(darkTheme)){
            isDarkmode = false;
            leftPanelMin.getScene().getStylesheets().remove(darkTheme);
            leftPanelMin.getScene().getStylesheets().add(lightTheme);
        }else {
            isDarkmode = true;
            leftPanelMin.getScene().getStylesheets().remove(lightTheme);
            leftPanelMin.getScene().getStylesheets().add(darkTheme);
        }
    }
}
