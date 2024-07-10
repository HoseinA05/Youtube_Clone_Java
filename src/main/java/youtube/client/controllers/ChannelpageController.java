package youtube.client.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import youtube.SortTypes;
import youtube.YoutubeApplication;
import youtube.client.*;
import youtube.server.models.Playlist;
import youtube.server.models.Video;

public class ChannelpageController {

    @FXML
    public VBox leftPanelMin, leftPanelMax;
    @FXML
    public ImageView channelImage, userImg;
    @FXML
    public Label channelName, channelInfo, channelAboutMe;
    @FXML
    public TilePane videoContainer;
    @FXML
    public Button subscribeBtn;
    @FXML
    public Button videosSectionBtn, playlistsSectionBtn, aboutSectionBtn, loadmoreBtn;

    private int currentPage = 1;

    private Task<Response> subscibeTask;
    private Task<Response> videosTask;
    private Task<Response> playlistVideosTask;


    private int userID;
    private String currentSection;

    private boolean isDarkmode = false;
    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    public ChannelpageController(boolean isDarkmode, int userID, String section) {
        this.isDarkmode = isDarkmode;
        this.userID = userID;
        switch (section) {
            case "videos", "about", "playlists":
                break;
            default:
                throw new RuntimeException("Invalid section:" + section);
        }
        this.currentSection = section;
    }

    public void initialize() {

        int currentUserId = 0;
        if (Config.currentUser != null) {
            currentUserId = Config.currentUser.getId();
        }

        // Load User Info
        Task<Response> userTask = Requests.GET_USERINFO(userID, currentUserId);
        userTask.setOnSucceeded(event -> {
            var user = userTask.getValue().toUser();
            System.out.println(user);
            channelName.setText(user.getUsername());
            channelInfo.setText(String.format("%s subscribers .  %s videos", Helper.coolformat(user.getSubscribers()), Helper.coolformat(user.getUploadCount())));
            channelAboutMe.setText(user.getAboutMe());
            if (user.isCurrentUserSubscribed()) {
                subscribeBtn.setText("Unsubscribe");
                subscribeBtn.getStyleClass().add("subBtn");
                subscribeBtn.getStyleClass().remove("unSubBtn");
            }

            try {
                if (user.getChannelPhotoPath().isEmpty()) {
                    channelImage.setImage(new Image(YoutubeApplication.class.getResource("default_thumbnail.png").toExternalForm()));
                } else {
                    channelImage.setImage(new Image(FileHandler.getPhoto(user.getChannelPhotoPath())));
                }
                if (user.getProfilePhotoPath().isEmpty()) {
                    userImg.setImage(new Image(YoutubeApplication.class.getResource("default_thumbnail.png").toExternalForm()));
                } else {
                    userImg.setImage(new Image(FileHandler.getPhoto(user.getProfilePhotoPath())));
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        new Thread(userTask).start();


        switch (currentSection) {
            case "videos":
                videosSectionBtn.getStyleClass().add("channelItemSelected");
                loadVideos();
                break;
            case "about":
                aboutSectionBtn.getStyleClass().add("channelItemSelected");
                loadmoreBtn.setVisible(false);
                break;
            case "playlists":
                playlistsSectionBtn.getStyleClass().add("channelItemSelected");
                loadmoreBtn.setVisible(false);
                loadPlaylists();
                break;
        }


        Rectangle userImgClip = new Rectangle(userImg.getFitWidth(), userImg.getFitHeight());
        userImgClip.setArcWidth(130);
        userImgClip.setArcHeight(130);
        userImg.setClip(userImgClip);

        // Rectangle videoImgClip = new Rectangle(videoImg.getFitWidth(), videoImg.getFitHeight());
        // videoImgClip.setArcWidth(20);
        // videoImgClip.setArcHeight(20);
        // videoImg.setClip(videoImgClip);
    }

    private void loadVideos(){
        videosTask = Requests.GET_USER_VIDEOS(SortTypes.NEWEST.ordinal(), userID, 8, currentPage);
        videosTask.setOnSucceeded(event -> {
            currentPage++;
            var videos = videosTask.getValue().toVideos();
            if (videos != null) {
                for (Video v : videos) {
                    createVideo(v);
                }
            }

            if(videos.size() < 8)
                loadmoreBtn.setVisible(false);
        });
        new Thread(videosTask).start();
    }

    private void loadPlaylists(){
        playlistVideosTask = Requests.GET_USER_PLAYLIST(userID);
        playlistVideosTask.setOnSucceeded(event -> {
            // currentPage++;
            var ps = playlistVideosTask.getValue().toPlaylists();
            if (ps != null) {
                for (Playlist p : ps) {
                    createPlaylist(p);
                }
            }

            // if(videos.size() < 8)
            loadmoreBtn.setVisible(false);
        });
        new Thread(playlistVideosTask).start();
    }

    private void createVideo(Video v) {
        VBox vb = new VBox();
        vb.getStyleClass().add("videoBox");

        vb.setOnMouseClicked(mouseEvent -> videoPageHandler(v.getId()));

        ImageView videoImage = new ImageView();
        videoImage.setFitWidth(250);
        videoImage.setFitHeight(130);
        try {
            if (v.getThumbnailPath().isEmpty()) {
                videoImage.setImage(new Image(YoutubeApplication.class.getResource("default_thumbnail.png").toExternalForm()));
            } else {
                videoImage.setImage(new Image(FileHandler.getPhoto(v.getThumbnailPath())));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Rectangle clip = new Rectangle(250, 130);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        videoImage.setClip(clip);

        Label title = new Label(v.getTitle());
        title.setWrapText(true);
        title.getStyleClass().add("videoTitle");

        Label info = new Label(Helper.coolformat(v.getViewsCount()) + " • " + v.getCreatedAt());
        info.getStyleClass().add("videoInfo");

        vb.getChildren().addAll(videoImage, title, info);
        videoContainer.getChildren().add(vb);
    }

    private void createPlaylist(Playlist pl) {
        VBox vb = new VBox();
        vb.getStyleClass().add("videoBox");

        vb.setOnMouseClicked(mouseEvent -> playListPageHandler(pl.getId()));

        ImageView videoImage = new ImageView();
        videoImage.setFitWidth(250);
        videoImage.setFitHeight(130);
        try {
            if (pl.getThumbnailPath().isEmpty()) {
                videoImage.setImage(new Image(YoutubeApplication.class.getResource("default_thumbnail.png").toExternalForm()));
            } else {
                videoImage.setImage(new Image(FileHandler.getPhoto(pl.getThumbnailPath())));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Label title = new Label(pl.getName());
        title.setWrapText(true);
        title.getStyleClass().add("videoTitle");

        Label info = new Label(pl.getVideoCount() + " videos • " + pl.getCreatedAt());
        info.getStyleClass().add("videoInfo");

        vb.getChildren().addAll(videoImage, title, info);
        videoContainer.getChildren().add(vb);
    }

    @FXML
    public void loadmoreHandler(ActionEvent ae) {
        loadVideos();
    }

    @FXML
    public void videoSectionHandler(ActionEvent ae) {
        if (currentSection.equals("videos")) {
            return;
        }
        Navigator.gotoChannelPage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode, userID, "videos");
    }

    @FXML
    public void playlistSectionHandler(ActionEvent ae) {
        if (currentSection.equals("playlists")) {
            return;
        }
        Navigator.gotoChannelPage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode, userID, "playlists");
    }

    @FXML
    public void aboutSectionHandler(ActionEvent ae) {
        if (currentSection.equals("about")) {
            return;
        }
        Navigator.gotoChannelPage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode, userID, "about");
    }


    @FXML
    public void subscribeBtnHandler(ActionEvent ae) {
        if (Config.currentUser == null) {
            Navigator.gotoSigninPage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode);
            return;
        }
        if (subscibeTask!=null && subscibeTask.isRunning()){
            return;
        }
        System.out.println("sub btn pressed");

        if (subscribeBtn.getText().equals("Subscribe")) {
            subscibeTask = Requests.FOLLOW(Config.currentUser.getId(), userID);
        } else {
            subscibeTask = Requests.UNFOLLOW(Config.currentUser.getId(), userID);
        }
        subscibeTask.setOnSucceeded(event -> {
            var res = subscibeTask.getValue();
            System.out.println(res.getMessage());
            if (!res.isError()) {
                if (subscribeBtn.getText().equals("Subscribe")) {
                    subscribeBtn.setText("Unsubscribe");
                    subscribeBtn.getStyleClass().remove("subBtn");
                    subscribeBtn.getStyleClass().add("unSubBtn");
                } else {
                    subscribeBtn.setText("Subscribe");
                    subscribeBtn.getStyleClass().remove("subBtn");
                    subscribeBtn.getStyleClass().add("unSubBtn");
                }
            }
        });
        new Thread(subscibeTask).start();
    }

    private void playListPageHandler(int playlistID) {
        Navigator.gotoPlaylistPage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode,playlistID);
    }

    private void videoPageHandler(int videoID) {
        Navigator.gotoVideoPage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode, videoID);
    }


    @FXML
    public void homeBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoHomePage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode);
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
