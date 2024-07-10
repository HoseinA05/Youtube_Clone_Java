package youtube.client.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import youtube.YoutubeApplication;
import youtube.client.FileHandler;
import youtube.client.Navigator;
import youtube.client.Requests;
import youtube.client.Response;
import youtube.server.models.Playlist;
import youtube.server.models.PlaylistVideo;
import youtube.server.models.Video;

public class PlaylistpageController {
    @FXML
    public VBox leftPanelMin, leftPanelMax, videosContainer;
    @FXML
    public ImageView plImage;
    @FXML
    public ImageView plVideoImage;
    @FXML
    public Label plName, plCreator, plInfo, plDesc;

    boolean isDarkmode = false;
    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    private int playlistID;
    private int videoNumber = 1;
    private int currentPage = 1;

    public PlaylistpageController(boolean isDarkmode,int playlistID){
        this.isDarkmode = isDarkmode;
        this.playlistID = playlistID;
    }

    public void initialize(){
        loadPlaylistData();

    }

    private void loadPlaylistData(){

        Task<Response>playlistTask = Requests.GET_PLAYLIST(playlistID);

        playlistTask.setOnSucceeded(event -> {
            var playlist = playlistTask.getValue().toPlaylist();

            try {
                if (playlist.getThumbnailPath().isEmpty()){
                    plImage.setImage(new Image(YoutubeApplication.class.getResource("default_thumbnail.png").toExternalForm()));
                } else {
                    plImage.setImage(new Image(FileHandler.getPhoto(playlist.getThumbnailPath())));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            plName.setText(playlist.getName());
            plDesc.setText(playlist.getText());
            plCreator.setText(playlist.getCreatorUsername());
            plInfo.setText(playlist.getVideoCount() + " videos . " + playlist.getCreatedAt());
        });
        new Thread(playlistTask).start();

        Task<Response> playlistVideosTask = Requests.GET_PLAYLIST_VIDEOS(playlistID,5,currentPage);
        playlistVideosTask.setOnSucceeded(event -> {
            // Thread.sleep(1000);
            currentPage++;
            var res = playlistVideosTask.getValue();
            if (res.isError()){
                System.out.println(res.getMessage());
                return;
            }
            var videos = res.toPlaylistVideos();
            if (videos==null){
                disableLoadMore();
                System.out.println("playlist is empty");
                return;
            }
            for (var video : videos) {
                createPlaylistVideo(video);
            }
            if (videos.size()<5){
                disableLoadMore();
                //remove load more
            }
        });
        new Thread(playlistVideosTask).start();
    }
    private void disableLoadMore(){

    }

    private void createPlaylistVideo(PlaylistVideo v) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPrefHeight(90);
        hBox.setOnMouseClicked(mouseEvent -> videoPageHandler(v.getVideoId()));
        videoNumber++;
        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        Label l = new Label(String.valueOf(videoNumber));
        l.getStyleClass().add("VideoNumber");

        vb.getChildren().add(l);


        ImageView videoThumbnail = new ImageView();
        videoThumbnail.setFitWidth(150);
        videoThumbnail.setFitHeight(100);

        try {
            if (v.getVideoThumbnail().isEmpty()) {
                videoThumbnail.setImage(new Image(YoutubeApplication.class.getResource("default_thumbnail.png").toExternalForm()));
            } else {
                videoThumbnail.setImage(new Image(FileHandler.getPhoto(v.getVideoThumbnail())));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Rectangle clip = new Rectangle(150, 90);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        videoThumbnail.setClip(clip);


        VBox vBox = new VBox();
        vBox.setSpacing(6);

        Label title = new Label(v.getVideoTitle());
        title.getStyleClass().add("videoTitle");
        title.setWrapText(true);

        Label videoInfo = new Label( v.getVideoCreator() + Helper.coolformat(v.getVideoViewCount()) + " • " + v.getVideoCreatedAt());
        videoInfo.getStyleClass().add("videoInfo");
        Label addedBy = new Label("Added By: " + v.getAdderUsername());
        addedBy.getStyleClass().add("videoInfo");

        addedBy.setOnMouseClicked(mouseEvent -> channelPageHandler(mouseEvent, v.getAdderUserID()));

        vBox.getChildren().addAll(title, videoInfo, addedBy);

        hBox.getChildren().addAll(vb, videoThumbnail, vBox);

        videosContainer.getChildren().add(hBox);
    }

    private void videoPageHandler(int videoID) {
        Navigator.gotoVideoPage((Stage) (leftPanelMin).getScene().getWindow(), isDarkmode, videoID);
    }

    private void channelPageHandler(MouseEvent me, int userID) {
        Navigator.gotoChannelPage((Stage) (leftPanelMin).getScene().getWindow(), isDarkmode, userID, "videos");
        me.consume();
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

    @FXML
    public void homeBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoHomePage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode);
    }
}
