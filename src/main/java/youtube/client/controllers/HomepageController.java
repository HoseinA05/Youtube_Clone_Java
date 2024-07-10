package youtube.client.controllers;


import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import youtube.YoutubeApplication;
import youtube.client.*;
import youtube.server.models.Video;

public class HomepageController {

    @FXML
    public ImageView dashboardImg;
    @FXML
    public VBox leftPanelMax, leftPanelMin;
    @FXML
    public ProgressIndicator progress;
    @FXML
    public TilePane tilePane;
    @FXML
    public VBox videosContainer;
    @FXML
    public HBox dashboardContainer;
    @FXML
    public Button signinBtn, dashboardBtn;


    private boolean isDarkmode = false;
    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    public HomepageController(boolean isDark) {
        this.isDarkmode = isDark;
    }

    @FXML
    public void initialize() {
        if(Config.currentUser != null){
            System.out.println("USER: " + Config.currentUser.getUsername());
            signinBtn.setVisible(false);
            signinBtn.setManaged(false);

            dashboardBtn.setText(Config.currentUser.getUsername());
            // Todo: Make a task for getting dashboard image
            try {
                if (Config.currentUser.getProfilePhotoPath().isEmpty()){
                    dashboardImg.setImage(new Image(YoutubeApplication.class.getResource("default_pf.png").toExternalForm()));
                } else {
                    dashboardImg.setImage(new Image(FileHandler.getPhoto(Config.currentUser.getProfilePhotoPath())));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            dashboardContainer.setVisible(true);
            dashboardContainer.setManaged(true);
        }

        // Get New Videos
        loadHomepageVideo();

    }

    public void loadHomepageVideo() {
        progress.setVisible(true);

        Task<Response> responseTask = Requests.GET_TRENDING_VIDEOS(1, 4);

        responseTask.setOnSucceeded(event -> {
            var videos = responseTask.getValue().toVideos();
            System.out.println(videos);

            VBox vb = new VBox();
            Label title = new Label("Trending");
            title.getStyleClass().add("videosTitle");
            TilePane tp = new TilePane();
            tp.getStyleClass().add("tilepane");

            if (videos != null) {
                for (Video v : videos) {
                    createHomeVideo(v, tp);
                }
                vb.getChildren().addAll(title, tp);
                videosContainer.getChildren().add(vb);
            }
            progress.setVisible(false);
        });
        new Thread(responseTask).start();

        Task<Response> responseTask2 = Requests.GET_NEW_VIDEOS(1, 4);
        responseTask2.setOnSucceeded(event -> {
            var videos = responseTask2.getValue().toVideos();
            System.out.println(videos);

            VBox vb = new VBox();
            Label title = new Label("New Videos");
            title.getStyleClass().add("videosTitle");
            TilePane tp = new TilePane();
            tp.getStyleClass().add("tilepane");

            if (videos != null) {
                for (Video v : videos) {
                    createHomeVideo(v, tp);
                }
                vb.getChildren().addAll(title, tp);
                videosContainer.getChildren().add(vb);
            }

            progress.setVisible(false);
        });
        new Thread(responseTask2).start();
    }

    // Creating a Video in Homepage
    public void createHomeVideo(Video v, TilePane tp) {
        VBox videoBox = new VBox();
        videoBox.getStyleClass().add("videoBox");

        videoBox.setOnMouseClicked(mouseEvent -> videoPageHandler(v.getId()));

        // Making Video Image
        ImageView videoImage = new ImageView();
        videoImage.setFitWidth(320);
        videoImage.setFitHeight(180);
        try {
            if (v.getThumbnailPath().isEmpty()){
                videoImage.setImage(new Image(YoutubeApplication.class.getResource("default_thumbnail.png").toExternalForm()));
            } else {
                videoImage.setImage(new Image(FileHandler.getPhoto(v.getThumbnailPath())));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

        profileImage.setOnMouseClicked(mouseEvent -> channelPageHandler(mouseEvent, v.getUserId()));
        try {
            if (v.getUserProfilePhotoPath().isEmpty()){
                profileImage.setImage(new Image(YoutubeApplication.class.getResource("default_pf.png").toExternalForm()));
            } else {
                profileImage.setImage(new Image(FileHandler.getPhoto(v.getUserProfilePhotoPath())));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // profileImage.setImage(new Image(YoutubeApplication.class.getResource("testImg.jpg").toExternalForm()));

        Rectangle clip2 = new Rectangle(profileImage.getFitWidth(), profileImage.getFitHeight());
        clip2.setArcWidth(50);
        clip2.setArcHeight(50);
        profileImage.setClip(clip2);

        HBox.setMargin(profileImage, new Insets(5, 0, 0, 0));

        // Making Info of Video
        VBox vb = new VBox();

        Label videoTitle = new Label(v.getTitle());
        videoTitle.getStyleClass().addAll("userLabel","videoTitle");

        Label videoOwner = new Label(v.getUserName());
        videoOwner.getStyleClass().addAll("userLabel","videoInfo");
        videoOwner.setOnMouseClicked(mouseEvent -> channelPageHandler(mouseEvent, v.getUserId()));

        Label videoInfo = new Label(Helper.coolformat(v.getViewsCount()) + " • " + v.getCreatedAt());
        videoInfo.getStyleClass().add("videoInfo");

        vb.getChildren().addAll(videoTitle, videoOwner, videoInfo);
        hBox.getChildren().addAll(profileImage, vb);

        videoBox.getChildren().addAll(videoImage, hBox);

        tp.getChildren().add(videoBox);
    }


    private void videoPageHandler(int videoID) {
        Navigator.gotoVideoPage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode, videoID);
    }



    private void channelPageHandler(MouseEvent me, int userID) {
        Navigator.gotoChannelPage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode, userID,"videos");
        me.consume();
    }

    @FXML
    public void dashboardBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoDashboard((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode,"account");
    }

    @FXML
    public void signinBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoSigninPage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode);
    }

    @FXML
    public void leftPanelBtnHandler(ActionEvent actionEvent) {
        leftPanelMax.setVisible(!leftPanelMax.isVisible());
        leftPanelMax.setManaged(!leftPanelMax.isManaged());

        leftPanelMin.setVisible(!leftPanelMin.isVisible());
        leftPanelMin.setManaged(!leftPanelMin.isManaged());
    }

    public void setTheme(boolean isDarkmode) {
        this.isDarkmode = isDarkmode;
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