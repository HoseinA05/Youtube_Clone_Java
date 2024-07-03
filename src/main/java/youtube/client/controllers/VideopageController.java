package youtube.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import youtube.YoutubeApplication;
import youtube.client.DataService;
import youtube.client.Navigator;

public class VideopageController {

    @FXML
    public VBox leftPanelMin;
    @FXML
    public MediaView mediaView;
    @FXML
    public ImageView img;
    @FXML
    public ImageView profImg;
    @FXML
    public ImageView userProfImg;
    @FXML
    public ImageView otherUserProfImg;
    @FXML
    public ImageView repliedUserProfImg;
    @FXML
    public ScrollPane rightScrollpane;
    @FXML
    public ScrollPane centerScrollpane;
    @FXML
    public Button repliesBtn;
    @FXML
    public SVGPath repliesSvg;

    private DataService dataService;

    private boolean isDarkmode = false;
    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    public VideopageController(DataService dataService, boolean isDarkmode){
        this.dataService = dataService;
        this.isDarkmode = isDarkmode;
    }

    @FXML
    public void initialize() {
        // Don't show left panel by default in video page.
        leftPanelMin.setVisible(false);
        leftPanelMin.setManaged(false);

        // Bind the right and center scrollpanes
        centerScrollpane.vvalueProperty().bindBidirectional(rightScrollpane.vvalueProperty());
        centerScrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Make Recommended Video Image Rounded.
        Rectangle clip = new Rectangle(img.getFitWidth(), img.getFitHeight());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        img.setClip(clip);
        // Make Profile Image of video creator Rounded.
        Rectangle clip3 = new Rectangle(profImg.getFitWidth(), profImg.getFitHeight());
        clip3.setArcWidth(50);
        clip3.setArcHeight(50);
        profImg.setClip(clip3);
        // Make Profile Image of Add a Comment Rounded.
        Rectangle clip4 = new Rectangle(userProfImg.getFitWidth(), userProfImg.getFitHeight());
        clip4.setArcWidth(50);
        clip4.setArcHeight(50);
        userProfImg.setClip(clip4);
        // Make Profile Image of Other peoples Comment Rounded.
        Rectangle clip5 = new Rectangle(otherUserProfImg.getFitWidth(), otherUserProfImg.getFitHeight());
        clip5.setArcWidth(50);
        clip5.setArcHeight(50);
        otherUserProfImg.setClip(clip5);
        // Make Profile Image of replied Comment Rounded.
        Rectangle clip6 = new Rectangle(repliedUserProfImg.getFitWidth(), repliedUserProfImg.getFitHeight());
        clip6.setArcWidth(50);
        clip6.setArcHeight(50);
        repliedUserProfImg.setClip(clip6);

        // Set up the MediaView
        Media media = new Media(YoutubeApplication.class.getResource("test.mp4").toExternalForm()); // Replace with your video file
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        // Play the video (for demonstration purposes)
        mediaPlayer.setAutoPlay(true);

        // Making Video Rounded
        Rectangle clip2 = new Rectangle(mediaView.getFitWidth(), mediaView.getFitHeight());
        clip2.setArcWidth(20);
        clip2.setArcHeight(20);
        mediaView.setClip(clip2);


    }

    public void setTheme(boolean isDarkmode){
        this.isDarkmode = isDarkmode;
    }


    @FXML
    public void homeBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoHomePage((Stage) (leftPanelMin).getScene().getWindow(), dataService, isDarkmode);
    }
    @FXML
    public void signinBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoSigninPage((Stage) (leftPanelMin).getScene().getWindow(), dataService, isDarkmode);
    }
    
    @FXML
    public void repliesBtnHandler(ActionEvent actionEvent) {
        String downArrowSvg = "m18 9.28-6.35 6.35-6.37-6.35.72-.71 5.64 5.65 5.65-5.65z";
        if(repliesSvg.getContent().equals(downArrowSvg))
            repliesSvg.setContent("M18.4 14.6 12 8.3l-6.4 6.3.8.8L12 9.7l5.6 5.7z");
        else repliesSvg.setContent(downArrowSvg);
    }
    @FXML
    public void leftPanelBtnHandler(ActionEvent actionEvent) {
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
