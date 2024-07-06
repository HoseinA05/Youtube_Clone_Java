package youtube.client.controllers;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;

import youtube.YoutubeApplication;
import youtube.client.DataService;
import youtube.client.Navigator;

import java.net.URISyntaxException;

public class VideopageController {

    @FXML
    public MediaView mediaView;
    @FXML
    public VBox leftPanelMin, centerContainer;
    @FXML
    public AnchorPane videoPane;
    @FXML
    public ImageView img, profImg, userProfImg, otherUserProfImg, repliedUserProfImg;
    @FXML
    public ScrollPane rightScrollpane, centerScrollpane;
    @FXML
    public SVGPath repliesSvg, pauseSvg, nextVideoSvg, muteVideoSvg;
    @FXML
    private Button pauseBtn, nextVideoBtn, muteVideoBtn;
    @FXML
    private BorderPane mainPane;
    @FXML
    private Slider timeSlider, soundSlider;
    @FXML
    private Label videoTimeLabel;

    private Region sliderThumb;
    private Rectangle videoClip;

    private int videoInitWidth;
    private int videoInitHeight;
    private double videoAspectRatio;

    private boolean isPaused = true;
    private boolean isMute = false;

    private MediaPlayer mediaPlayer;
    private double videoPaneWidth, videoPaneHeight;
    private double videoPaneAspectRation;


    private boolean isDarkmode = false;
    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    private DataService dataService;

    public VideopageController(DataService dataService, boolean isDarkmode) {
        this.dataService = dataService;
        this.isDarkmode = isDarkmode;
    }

    @FXML
    public void initialize() throws URISyntaxException {
        // Don't show left panel by default in video page.
        leftPanelMin.setVisible(false);
        leftPanelMin.setManaged(false);

        // Bind the right and center scrollpanes
        centerScrollpane.vvalueProperty().bindBidirectional(rightScrollpane.vvalueProperty());
        centerScrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        centerScrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


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


        videoPaneWidth = centerScrollpane.getWidth(); // videoPane.getWidth() shows incorrect values when window resizes
        videoPaneHeight = 600;
        videoPaneAspectRation = videoPaneWidth / videoPaneHeight;
        mainPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            // currentWidth = newValue.intValue()-rightScrollpane.getWidth()-40;
            adjustSize();
        });

        // Set up the Media
        Media media = new Media(YoutubeApplication.class.getResource("test.mp4").toExternalForm()); // Replace with your video file
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(false);

        mediaPlayer.setOnReady(() -> {
            // Make The Thumb of slider invisible unless on mouse enter
            sliderThumb = (Region) timeSlider.lookup(".thumb");
            sliderThumb.setStyle("-fx-opacity: 0");

            // Make Sound Slider invisible
            soundSlider.setVisible(false);

            videoInitWidth = media.getWidth();
            videoInitHeight = media.getHeight();
            videoAspectRatio = (double) videoInitWidth / videoInitHeight;
            adjustSize();

            // adjust time slider
            timeSlider.setMax(media.getDuration().toSeconds());
            timeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
                double percentage = 100.0 * newValue.doubleValue() / timeSlider.getMax();
                String style = String.format(
                        "-track-color: linear-gradient(to right, " +
                                "red 0%%, " +
                                "red %1$.1f%%, " +
                                "grey %1$.1f%%, " +
                                "grey 100%%);",
                        percentage);
                timeSlider.setStyle(style);
            });

            // adjust sound slider
            soundSlider.setValue(mediaPlayer.getVolume());
            soundSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
                double percentage = 100.0 * newValue.doubleValue() / soundSlider.getMax();
                String style = String.format(
                        "-track-color: linear-gradient(to right, " +
                                "white 0%%, " +
                                "white %1$.1f%%, " +
                                "grey %1$.1f%%, " +
                                "grey 100%%);",
                        percentage);
                soundSlider.setStyle(style);
            });
        });

        // Make The slider change on video progress
        mediaPlayer.currentTimeProperty().addListener(observable -> {
            timeSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
            videoTimeLabel.setText(secToMinuteFormat(mediaPlayer.getCurrentTime().toSeconds()) + " / " + secToMinuteFormat(mediaPlayer.getMedia().getDuration().toSeconds()));
        });

        timeSlider.setOnMousePressed(mouseEvent -> {
            if(mediaPlayer.getMedia() != null){
                timeSlider.valueProperty().addListener((observableValue, number, t1) -> {
                    if(t1.doubleValue() == mediaPlayer.getCurrentTime().toSeconds())
                        return;
                    mediaPlayer.seek(Duration.seconds(t1.doubleValue()));
                });
            }
        });

        soundSlider.setOnMousePressed(mouseEvent -> {
            soundSlider.valueProperty().addListener((observableValue, number, t1) -> {
                if(t1.doubleValue() == mediaPlayer.getVolume())
                    return;

                if(t1.doubleValue() == 0.0) muteVideoHandlerFunc();
                else {
                    muteVideoSvg.setContent("M8,21 L12,21 L17,26 L17,10 L12,15 L8,15 L8,21 Z M19,14 L19,22 C20.48,21.32 21.5,19.77 21.5,18 C21.5,16.26 20.48,14.74 19,14 ZM19,11.29 C21.89,12.15 24,14.83 24,18 C24,21.17 21.89,23.85 19,24.71 L19,26.77 C23.01,25.86 26,22.28 26,18 C26,13.72 23.01,10.14 19,9.23 L19,11.29 Z");
                    isMute = false;
                }

                mediaPlayer.volumeProperty().setValue(t1.doubleValue());
            });
        });

        // mediaPlayer.setOnPlaying(() -> {
        //     System.out.println("playing");
        //     adjustSize();
        //     mediaView.setFitWidth(videoPaneWidth);
        // });

        mediaView.setFitHeight(600);
        adjustSize();


    }

    private void adjustSize() {
        videoPaneWidth = mainPane.getWidth() - rightScrollpane.getWidth() - 40;
        videoPaneAspectRation = videoPaneWidth / videoPaneHeight;
        mediaView.setFitWidth(videoPaneWidth);

        System.out.println("videoAspectRatio: " + videoAspectRatio);
        System.out.println("videoPaneAspectRation: " + videoPaneAspectRation);
        // checking media get the full width or full height
        double videoWidth, videoHeight;
        if (videoAspectRatio < videoPaneAspectRation) {
            System.out.println("yes");
            // height is full
            videoWidth = videoInitWidth * (videoPaneHeight / videoInitHeight);
            videoHeight = videoPaneHeight;
            System.out.println(videoPaneWidth);
            System.out.println(videoWidth);
            AnchorPane.setLeftAnchor(mediaView, (videoPaneWidth - videoWidth) / 2);
        } else {
            System.out.println("no");
            // width is full
            videoWidth = videoPaneWidth;
            videoHeight = videoInitHeight * (videoPaneWidth / videoInitWidth);
            System.out.println(videoPaneHeight);
            System.out.println(videoHeight);
            AnchorPane.setTopAnchor(mediaView, (videoPaneHeight - videoHeight) / 2);
        }
        // Center The media View
        System.out.println("videoPaneWidth: " + videoPaneWidth);
        System.out.println("mediaView width: " + mediaView.getFitWidth());
    }

    private String secToMinuteFormat(double seconds){
        int sec = (int) seconds;
        String secondsStringPart = (sec % 60) < 10 ? ("0" + sec % 60): String.valueOf(sec % 60);

        return sec / 60 + ":" + secondsStringPart;
    }

    @FXML
    public void pauseBtnHandler(MouseEvent me) {
        pauseHandler();
    }
    private void pauseHandler(){
        String playSvgContent = "M 12,26 18.5,22 18.5,14 12,10 z M 18.5,22 25,18 25,18 18.5,14 z";
        String pauseSvgContent = "M 12,26 16,26 16,10 12,10 z M 21,26 25,26 25,10 21,10 z";
        if (isPaused) {
            if (mediaView.getMediaPlayer().getMedia() != null) {
                mediaPlayer.play();
                isPaused = false;
                pauseSvg.setContent(pauseSvgContent);
            }
        } else {
            if (mediaView.getMediaPlayer().getMedia() != null) {
                mediaPlayer.pause();
                isPaused = true;
                pauseSvg.setContent(playSvgContent);
            }
        }
    }

    @FXML
    public void videoKeyPressedHandler(KeyEvent ke){
        System.out.println(ke.getCharacter());
    }

    @FXML
    public void mousePressesVideoHandler(MouseEvent me){
        if(me.getClickCount() == 1) pauseHandler();
        if(me.getClickCount() == 2) ((Stage)leftPanelMin.getScene().getWindow()).setFullScreen(true);
    }

    @FXML
    public void mouseEnterVideoHandler(MouseEvent me) {
        pauseBtn.setVisible(true);
        if(sliderThumb != null) sliderThumb.setStyle("-fx-opacity: 1");
        nextVideoBtn.setVisible(true);
        muteVideoBtn.setVisible(true);
        timeSlider.setVisible(true);
        videoTimeLabel.setVisible(true);
    }

    @FXML
    public void mouseExitVideoHandler(MouseEvent me) {
        pauseBtn.setVisible(false);
        if(sliderThumb != null) sliderThumb.setStyle("-fx-opacity: 0");
        nextVideoBtn.setVisible(false);
        muteVideoBtn.setVisible(false);
        timeSlider.setVisible(false);
        videoTimeLabel.setVisible(false);
    }


    @FXML
    public void nextVideoBtnHandler(MouseEvent me){
        // Todo: Show The Next Video
    }

    @FXML
    public void muteVideoBtnHandler(MouseEvent me){
        muteVideoHandlerFunc();
    }
    private void muteVideoHandlerFunc(){
        String openSoundSvgContent = "M8,21 L12,21 L17,26 L17,10 L12,15 L8,15 L8,21 Z M19,14 L19,22 C20.48,21.32 21.5,19.77 21.5,18 C21.5,16.26 20.48,14.74 19,14 ZM19,11.29 C21.89,12.15 24,14.83 24,18 C24,21.17 21.89,23.85 19,24.71 L19,26.77 C23.01,25.86 26,22.28 26,18 C26,13.72 23.01,10.14 19,9.23 L19,11.29 Z";
        String muteSoundSvgContent = "m 21.48,17.98 c 0,-1.77 -1.02,-3.29 -2.5,-4.03 v 2.21 l 2.45,2.45 c .03,-0.2 .05,-0.41 .05,-0.63 z m 2.5,0 c 0,.94 -0.2,1.82 -0.54,2.64 l 1.51,1.51 c .66,-1.24 1.03,-2.65 1.03,-4.15 0,-4.28 -2.99,-7.86 -7,-8.76 v 2.05 c 2.89,.86 5,3.54 5,6.71 z M 9.25,8.98 l -1.27,1.26 4.72,4.73 H 7.98 v 6 H 11.98 l 5,5 v -6.73 l 4.25,4.25 c -0.67,.52 -1.42,.93 -2.25,1.18 v 2.06 c 1.38,-0.31 2.63,-0.95 3.69,-1.81 l 2.04,2.05 1.27,-1.27 -9,-9 -7.72,-7.72 z m 7.72,.99 -2.09,2.08 2.09,2.09 V 9.98 z";
        if (isMute) {
            if (mediaView.getMediaPlayer().getMedia() != null) {
                mediaPlayer.setMute(false);
                isMute = false;
                muteVideoSvg.setContent(openSoundSvgContent);
            }
        } else {
            if (mediaView.getMediaPlayer().getMedia() != null) {
                mediaPlayer.setMute(true);
                isMute = true;
                muteVideoSvg.setContent(muteSoundSvgContent);
            }
        }
    }

    @FXML
    public void soundAreaMouseEnterHandler(MouseEvent me){
        soundSlider.setVisible(true);
        AnchorPane.setLeftAnchor(videoTimeLabel, 290.0);
    }

    @FXML
    public void soundAreaMouseExitHandler(MouseEvent me){
        soundSlider.setVisible(false);
        AnchorPane.setLeftAnchor(videoTimeLabel, 135.0);
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
        if (repliesSvg.getContent().equals(downArrowSvg))
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
        if (leftPanelMin.getScene().getStylesheets().contains(darkTheme)) {
            isDarkmode = false;
            leftPanelMin.getScene().getStylesheets().remove(darkTheme);
            leftPanelMin.getScene().getStylesheets().add(lightTheme);
        } else {
            isDarkmode = true;
            leftPanelMin.getScene().getStylesheets().remove(lightTheme);
            leftPanelMin.getScene().getStylesheets().add(darkTheme);
        }
    }

}
