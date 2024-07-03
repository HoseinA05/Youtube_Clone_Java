package youtube.client.controllers;


import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import youtube.YoutubeApplication;
import youtube.client.DataService;
import youtube.client.Navigator;
import youtube.client.test.Video;

import java.util.ArrayList;

public class HomepageController {

    @FXML
    public ImageView img;
    @FXML
    public ImageView profImg;
    @FXML
    public VBox leftPanelMax;
    @FXML
    public VBox leftPanelMin;
    @FXML
    public ProgressIndicator progress;
    @FXML
    public TilePane tilePane;

    private DataService dataService;

    private boolean isDarkmode = false;
    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    public HomepageController(DataService dataService, boolean isDark){
        this.dataService = dataService;
        this.isDarkmode = isDark;
    }

    @FXML
    public void initialize() {
        // Get New Videos
        if(dataService != null) {
            System.out.println("Loading Videos");
            loadHomepageVideo();
        }

//        // Hide Progress Bar at first
//        progress.setVisible(false);

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

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
        loadHomepageVideo();
    }

    public void loadHomepageVideo() {
        progress.setVisible(true);

        Task<ArrayList<Video>> videoTask = dataService.getNewHomepageVideos();

        videoTask.setOnSucceeded(event -> {
            for (Video v : videoTask.getValue()) {
                createHomeVideo(v);
            }
            progress.setVisible(false);
        });
        new Thread(videoTask).start();
    }

    // Creating a Video in Homepage
    public void createHomeVideo(Video v) {
        VBox videoBox = new VBox();
        videoBox.getStyleClass().add("videoBox");
        // Todo: Make a proper Video page handler
        videoBox.setOnMouseClicked(this::videoClickHandler);

        // Making Video Image
        ImageView videoImage = new ImageView();
        videoImage.setFitWidth(320);
        videoImage.setFitHeight(180);
        videoImage.setImage(new Image(YoutubeApplication.class.getResource("testImg.jpg").toExternalForm()));

        // Making Video Image rounded
        Rectangle clip = new Rectangle(videoImage.getFitWidth(), videoImage.getFitHeight());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        videoImage.setClip(clip);

        HBox hBox = new HBox();
        hBox.setSpacing(10);

        // Profile Image
        ImageView profileImage = new ImageView();
        profileImage.setFitWidth(40);
        profileImage.setFitHeight(40);
        // TODO: make a proper Channel page handler
        profileImage.setOnMouseClicked(this::channelpageHandler);
        profileImage.setImage(new Image(YoutubeApplication.class.getResource("testImg.jpg").toExternalForm()));

        Rectangle clip2 = new Rectangle(profileImage.getFitWidth(), profileImage.getFitHeight());
        clip2.setArcWidth(50);
        clip2.setArcHeight(50);
        profileImage.setClip(clip2);

        HBox.setMargin(profileImage, new Insets(5, 0, 0, 0));

        // Making Info of Video
        VBox vb = new VBox();
        Label videoTitle = new Label(v.getTitle());
        videoTitle.getStyleClass().add("videoTitle");
        Label videoOwner = new Label(v.getOwner());
        videoOwner.getStyleClass().add("videoInfo");
        videoOwner.setOnMouseClicked(this::channelpageHandler);
        Label videoInfo = new Label(v.getViews() + " • " + "1 year ago");
        videoInfo.getStyleClass().add("videoInfo");

        vb.getChildren().addAll(videoTitle, videoOwner, videoInfo);
        hBox.getChildren().addAll(profileImage, vb);

        videoBox.getChildren().addAll(videoImage, hBox);

        tilePane.getChildren().add(videoBox);
    }

    public void setTheme(boolean isDarkmode) {
        this.isDarkmode = isDarkmode;
    }

    @FXML
    public void channelpageHandler(MouseEvent mouseEvent) {
        Navigator.gotoChannelPage((Stage) (leftPanelMax).getScene().getWindow(), dataService, isDarkmode);
        mouseEvent.consume();
    }

    @FXML
    public void videoClickHandler(MouseEvent mouseEvent) {
        Navigator.gotoVideoPage((Stage) (leftPanelMax).getScene().getWindow(), dataService, isDarkmode);
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
        if (leftPanelMax.getScene().getStylesheets().contains(darkTheme)) {
            isDarkmode = false;
            leftPanelMax.getScene().getStylesheets().remove(darkTheme);
            leftPanelMax.getScene().getStylesheets().add(lightTheme);
        } else {
            isDarkmode = true;
            leftPanelMax.getScene().getStylesheets().remove(lightTheme);
            leftPanelMax.getScene().getStylesheets().add(darkTheme);
        }
    }

}