package youtube.client.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;

import youtube.SortTypes;
import youtube.YoutubeApplication;
import youtube.client.*;
import youtube.server.models.Comment;
import youtube.server.models.Video;

import java.net.URISyntaxException;

public class VideopageController {

    @FXML
    public MediaView mediaView;
    @FXML
    public VBox leftPanelMin, centerContainer, rightContainer, commentsContainerfx;
    @FXML
    public AnchorPane videoPane;
    @FXML
    public ImageView img, profImg, userProfImg, otherUserProfImg, repliedUserProfImg, dashboardImg;
    @FXML
    public ScrollPane rightScrollpane, centerScrollpane;
    @FXML
    public SVGPath repliesSvg, pauseSvg, nextVideoSvg, muteVideoSvg;
    @FXML
    private Button pauseBtn, nextVideoBtn, muteVideoBtn, likeBtn, dislikeBtn, subscribeBtn, signinBtn, dashboardBtn;
    @FXML
    private BorderPane mainPane;
    @FXML
    private Slider timeSlider, soundSlider;
    @FXML
    private Label videoTimeLabel, videoTitle, channelName, subsCount, videoInfo, videoDescription, commentCount;
    @FXML
    private TilePane tagsContainer;
    @FXML
    public HBox dashboardContainer;

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

    private int videoID;
    private int currentUserID = 0, videoCreatorID;
    private int likeState = 2;

    // different tasks
    private Task<Response> subscibeTask, likeTask;
    private Task<Response> createCommentTask, commentLikeTask, commentDislikeTask, commentRemovelikeTask;

    public VideopageController(boolean isDarkmode, int videoID) {
        this.videoID = videoID;
        this.isDarkmode = isDarkmode;
    }

    @FXML
    public void initialize() throws URISyntaxException {
        // Bind the right and center scrollpanes
        centerScrollpane.vvalueProperty().bindBidirectional(rightScrollpane.vvalueProperty());
        centerScrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        centerScrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Make Profile Image of video creator Rounded.
        Rectangle clip3 = new Rectangle(profImg.getFitWidth(), profImg.getFitHeight());
        clip3.setArcWidth(50);
        clip3.setArcHeight(50);
        profImg.setClip(clip3);
        profImg.setOnMouseClicked(mouseEvent -> channelPageHandler(mouseEvent, currentUserID));

        if (Config.currentUser != null) {
            System.out.println("USER: " + Config.currentUser.getUsername());
            currentUserID = Config.currentUser.getId();
            signinBtn.setVisible(false);
            signinBtn.setManaged(false);

            dashboardBtn.setText(Config.currentUser.getUsername());
            // Todo: Make a task for getting dashboard image
            try {
                if (Config.currentUser.getProfilePhotoPath().isEmpty()) {
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

        loadVideoData();

        // initMediaPlayer();

        loadComments();
    }

    private void initMediaPlayer(Media media) {
        videoPaneWidth = centerScrollpane.getWidth(); // videoPane.getWidth() shows incorrect values when window resizes
        videoPaneHeight = 600;
        videoPaneAspectRation = videoPaneWidth / videoPaneHeight;
        mainPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            // currentWidth = newValue.intValue()-rightScrollpane.getWidth()-40;
            adjustSize();
        });

        // Set up the Media
        // Media media = new Media(YoutubeApplication.class.getResource("test.mp4").toExternalForm()); // Replace with your video file
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
            if (mediaPlayer.getMedia() != null) {
                timeSlider.valueProperty().addListener((observableValue, number, t1) -> {
                    if (t1.doubleValue() == mediaPlayer.getCurrentTime().toSeconds())
                        return;
                    mediaPlayer.seek(Duration.seconds(t1.doubleValue()));
                });
            }
        });

        soundSlider.setOnMousePressed(mouseEvent -> {
            soundSlider.valueProperty().addListener((observableValue, number, t1) -> {
                if (t1.doubleValue() == mediaPlayer.getVolume())
                    return;

                if (t1.doubleValue() == 0.0) muteVideoHandlerFunc();
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

    private void loadVideoData() {
        Task<Response> videoDataTask;
        videoDataTask = Requests.GET_VIDEO_BY_ID(videoID, currentUserID);

        videoDataTask.setOnSucceeded(event -> {
            var video = videoDataTask.getValue().toVideo();
            videoCreatorID = video.getUserId();
            if (currentUserID != 0) {
                likeState = video.getCurrentUserLike().ordinal();

                if (video.isCurrentUserSubscribed()) {
                    subscribeBtn.setText("Unsubscribe");
                } else {
                    subscribeBtn.setText("Subscribe");
                }
            }
            loadRelatedVideos();
            changeSubBtnStyle();
            changeLikeStyle();
            videoTitle.setText(video.getTitle());
            channelName.setText(video.getUserName());
            channelName.setOnMouseClicked(mouseEvent -> channelPageHandler(mouseEvent, video.getUserId()));
            subsCount.setText(Helper.coolformat(video.getUserSubscribesCount()) + " subscribers");
            likeBtn.setText(Helper.coolformat(video.getLikesCount()));
            dislikeBtn.setText(Helper.coolformat(video.getDislikesCount()));
            videoInfo.setText(Helper.coolformat(video.getViewsCount()) + " views " + video.getCreatedAt());
            videoDescription.setText(video.getDescription());
            commentCount.setText(Helper.coolformat(video.getCommentsCount()));
            for (String s : video.getTags().split(",")) {
                System.out.println(s);
                Label tag = new Label(s);
                tag.getStyleClass().add("tag");
                // Todo: Show Tag Page Video...
                tagsContainer.getChildren().add(tag);
            }

            if (video.getVideoPath().isEmpty()) {
                throw new RuntimeException("video path should not be empty");
            }
            Task<String> videoFileTask = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    System.out.println("loading video (from server or cache)");
                    Thread.sleep(5000);
                    return FileHandler.getVideo(video.getVideoPath());
                }
            };
            videoFileTask.setOnSucceeded(event2 -> {
                String path = videoFileTask.getValue();
                if (!path.isEmpty()) {
                    System.out.println("got video file:" + path);
                    initMediaPlayer(new Media(path));
                } else {
                    // show error
                    throw new RuntimeException("video receive failed");
                }
            });
            new Thread(videoFileTask).start();


        });
        new Thread(videoDataTask).start();
    }

    private void loadComments() {
        Task<Response> commetsTask = Requests.GET_COMMENTS_OF_VIDEO(videoID, currentUserID,10,1,SortTypes.NEWEST.ordinal());

        commetsTask.setOnSucceeded(event -> {
            var comments = commetsTask.getValue().toComments();

            for(Comment c: comments){
                createComment(c);
            }
        });

        new Thread(commetsTask).start();
    }

    private void loadRelatedVideos() {
        Task<Response> recommendedVidesTask = Requests.GET_USER_VIDEOS(SortTypes.MOST_POPULAR.ordinal(), videoCreatorID, 5, 1);

        recommendedVidesTask.setOnSucceeded(event -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            var videos = recommendedVidesTask.getValue().toVideos();

            for (Video v : videos) {
                createRecommendedVideo(v);
            }
        });
        new Thread(recommendedVidesTask).start();
    }

    private void createComment(Comment c) {
        HBox commentContainer = new HBox();
        commentContainer.setSpacing(10);

        ImageView profImage = new ImageView();
        profImage.setFitWidth(35);
        profImage.setFitHeight(35);
        try {
            // if (().isEmpty()){
            profImage.setImage(new Image(YoutubeApplication.class.getResource("default_pf.png").toExternalForm()));
            // } else {
            //     profImage.setImage(new Image(FileHandler.getPhoto(v.getThumbnailPath())));
            // }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        VBox vb = new VBox();
        vb.setSpacing(7);

        VBox vb2 = new VBox();
        vb2.setSpacing(2);
        HBox hBox1 = new HBox();
        Label usernameLabel = new Label(c.getUserName());
        usernameLabel.getStyleClass().add("commentUsername");
        Label createdAtLabel = new Label(c.getCreatedAt().toString());
        createdAtLabel.getStyleClass().add("commentHistory");

        hBox1.getChildren().addAll(usernameLabel, createdAtLabel);

        Label commentText = new Label(c.getText());
        commentText.getStyleClass().add("commentText");

        vb2.getChildren().addAll(hBox1, commentText);



        HBox hBox2 = new HBox();
        hBox2.setSpacing(10);

        HBox hBox3 = new HBox();
        hBox3.setSpacing(2);
        Button btn1 = new Button();
        btn1.getStyleClass().add("commentBtn");
        btn1.setAlignment(Pos.CENTER);
        SVGPath likesvg = new SVGPath();
        likesvg.setContent("M18.77,11h-4.23l1.52-4.94C16.38,5.03,15.54,4,14.38,4c-0.58,0-1.14,0.24-1.52,0.65L7,11H3v10h4h1h9.43 c1.06,0,1.98-0.67,2.19-1.61l1.34-6C21.23,12.15,20.18,11,18.77,11z M7,20H4v-8h3V20z M19.98,13.17l-1.34,6 C18.54,19.65,18.03,20,17.43,20H8v-8.61l5.6-6.06C13.79,5.12,14.08,5,14.38,5c0.26,0,0.5,0.11,0.63,0.3 c0.07,0.1,0.15,0.26,0.09,0.47l-1.52,4.94L13.18,12h1.35h4.23c0.41,0,0.8,0.17,1.03,0.46C19.92,12.61,20.05,12.86,19.98,13.17z");
        btn1.setGraphic(likesvg);
        Button btn2 = new Button();
        btn2.getStyleClass().add("commentLikeCount");
        btn2.setText(String.valueOf(c.getLikesCount()));
        btn2.setDisable(true);
        btn2.setAlignment(Pos.CENTER);

        hBox3.getChildren().addAll(btn1, btn2);

        Button btn4 = new Button();
        btn4.getStyleClass().add("commentBtn");
        btn4.setAlignment(Pos.CENTER);
        SVGPath dislike = new SVGPath();
        likesvg.setContent("M17,4h-1H6.57C5.5,4,4.59,4.67,4.38,5.61l-1.34,6C2.77,12.85,3.82,14,5.23,14h4.23l-1.52,4.94C7.62,19.97,8.46,21,9.62,21 c0.58,0,1.14-0.24,1.52-0.65L17,14h4V4H17z M10.4,19.67C10.21,19.88,9.92,20,9.62,20c-0.26,0-0.5-0.11-0.63-0.3 c-0.07-0.1-0.15-0.26-0.09-0.47l1.52-4.94l0.4-1.29H9.46H5.23c-0.41,0-0.8-0.17-1.03-0.46c-0.12-0.15-0.25-0.4-0.18-0.72l1.34-6 C5.46,5.35,5.97,5,6.57,5H16v8.61L10.4,19.67z M20,13h-3V5h3V13z");
        btn4.setGraphic(likesvg);

        Button btn5 = new Button();
        btn5.getStyleClass().add("commentBtn");
        btn5.setText("Reply");

        hBox2.getChildren().addAll(hBox3, btn4, btn5);

        Button replyBtn = new Button();
        replyBtn.getStyleClass().add("repliesBtn");
        replyBtn.setAlignment(Pos.CENTER);
        replyBtn.setText(String.valueOf(c.getRepliesCount()));
        replyBtn.setGraphicTextGap(8);
        SVGPath replysvg = new SVGPath();
        replysvg.setContent("m18 9.28-6.35 6.35-6.37-6.35.72-.71 5.64 5.65 5.65-5.65z");
        replyBtn.setGraphic(replysvg);


        vb.getChildren().addAll(vb2, hBox2, replyBtn);


        commentContainer.getChildren().addAll(profImage, vb);
        commentsContainerfx.getChildren().add(commentContainer);
    }

    private void createRecommendedVideo(Video v) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("rvContainer");
        hBox.setOnMouseClicked(mouseEvent -> videoPageHandler(v.getId()));


        ImageView videoThumbnail = new ImageView();
        videoThumbnail.setFitWidth(150);
        videoThumbnail.setFitHeight(90);

        try {
            if (v.getThumbnailPath().isEmpty()) {
                videoThumbnail.setImage(new Image(YoutubeApplication.class.getResource("default_thumbnail.png").toExternalForm()));
            } else {
                videoThumbnail.setImage(new Image(FileHandler.getPhoto(v.getThumbnailPath())));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Rectangle clip = new Rectangle(150, 90);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        videoThumbnail.setClip(clip);


        VBox vBox = new VBox();
        vBox.setSpacing(5);

        Label title = new Label(v.getTitle());
        title.getStyleClass().add("videoTitle");
        title.setWrapText(true);
        VBox vb = new VBox();
        Label videoCreator = new Label(v.getUserName());
        videoCreator.getStyleClass().add("videoInfo");
        videoCreator.setOnMouseClicked(mouseEvent -> channelPageHandler(mouseEvent, v.getUserId()));

        Label videoInfo = new Label(Helper.coolformat(v.getViewsCount()) + " • " + v.getCreatedAt());
        videoInfo.getStyleClass().add("videoInfo");

        vb.getChildren().addAll(videoCreator, videoInfo);

        vBox.getChildren().addAll(title, vb);

        hBox.getChildren().addAll(videoThumbnail, vBox);

        rightContainer.getChildren().add(hBox);
    }

    @FXML
    private void likeBtnHandler(ActionEvent ae) {
        if (Config.currentUser == null) {
            Navigator.gotoSigninPage((Stage) (leftPanelMin).getScene().getWindow(), isDarkmode);
            return;
        }
        if (likeTask!=null && likeTask.isRunning()){
            return;
        }

        // Liked Before
        if (likeState == 1) {
            likeTask = Requests.DELETE_VIDEO_LIKE(videoID, currentUserID);

            likeTask.setOnSucceeded(event -> {
                var res = likeTask.getValue();
                System.out.println(res.getMessage());
                if (!res.isError()){
                    likeBtn.setText(String.valueOf(Integer.parseInt(likeBtn.getText())-1));
                    likeState = 2;
                    changeLikeStyle();
                }
            });

            new Thread(likeTask).start();
        } else if (likeState == 2) {
            likeTask = Requests.PUT_VIDEO_LIKE(videoID, currentUserID, true);

            likeTask.setOnSucceeded(event -> {
                var res = likeTask.getValue();
                System.out.println(res.getMessage());
                if (!res.isError()){
                    likeBtn.setText(String.valueOf(Integer.parseInt(likeBtn.getText())+1));
                    likeState = 1;
                    changeLikeStyle();
                }
            });

            new Thread(likeTask).start();
        }
    }

    @FXML
    private void dislikeBtnHandler(ActionEvent ae) {
        if (Config.currentUser == null) {
            Navigator.gotoSigninPage((Stage) (leftPanelMin).getScene().getWindow(), isDarkmode);
            return;
        }
        if (likeTask!=null && likeTask.isRunning()){
            return;
        }

        // disLiked Before
        if (likeState == 0) {
            likeTask = Requests.DELETE_VIDEO_LIKE(videoID, currentUserID);

            likeTask.setOnSucceeded(event -> {
                var res = likeTask.getValue();
                System.out.println(res.getMessage());
                if (!res.isError()){
                    dislikeBtn.setText(String.valueOf(Integer.parseInt(dislikeBtn.getText())-1));
                    likeState = 2;
                    changeLikeStyle();
                }
            });
            new Thread(likeTask).start();

        } else if (likeState == 2) {
            likeTask = Requests.PUT_VIDEO_LIKE(videoID, currentUserID, false);

            likeTask.setOnSucceeded(event -> {
                var res = likeTask.getValue();
                System.out.println(res.getMessage());
                if (!res.isError()){
                    dislikeBtn.setText(String.valueOf(Integer.parseInt(dislikeBtn.getText())+1));
                    likeState = 0;
                    changeLikeStyle();
                }
            });

            new Thread(likeTask).start();
        }
    }

    @FXML
    public void subscribeBtnHandler(ActionEvent ae) {
        if (Config.currentUser == null) {
            Navigator.gotoSigninPage((Stage) (leftPanelMin).getScene().getWindow(), isDarkmode);
            return;
        }
        if (subscibeTask != null && subscibeTask.isRunning()) {
            return;
        }
        System.out.println("sub btn pressed");

        if (subscribeBtn.getText().equals("Subscribe")) {
            subscibeTask = Requests.FOLLOW(Config.currentUser.getId(), currentUserID);
        } else {
            subscibeTask = Requests.UNFOLLOW(Config.currentUser.getId(), currentUserID);
        }
        subscibeTask.setOnSucceeded(event -> {
            var res = subscibeTask.getValue();
            System.out.println(res.getMessage());
            if (!res.isError()) {
                if (subscribeBtn.getText().equals("Subscribe")) {
                    subscribeBtn.setText("Unsubscribe");
                    changeSubBtnStyle();
                } else {
                    subscribeBtn.setText("Subscribe");
                    changeSubBtnStyle();
                }
            }
        });
        new Thread(subscibeTask).start();
    }

    public void changeLikeStyle() {
        if (likeState == 0){
            // dislikeBtn.getStyleClass().add("selected");
            dislikeBtn.setStyle("-fx-background-color: grey;");
        } else if (likeState == 1){
            // likeBtn.getStyleClass().add("selected");
            likeBtn.setStyle("-fx-background-color: grey;");
        } else{
            // dislikeBtn.getStyleClass().remove("selected");
            // likeBtn.getStyleClass().remove("selected");
            likeBtn.setStyle("-fx-background-color: hoverBg;");
            dislikeBtn.setStyle("-fx-background-color: hoverBg;");
        }
    }

    public void changeSubBtnStyle() {
        if (subscribeBtn.getText().equals("Subscribe")) {
            subscribeBtn.getStyleClass().remove("subBtn");
            subscribeBtn.getStyleClass().add("unSubBtn");
        } else {
            subscribeBtn.getStyleClass().remove("unSubBtn");
            subscribeBtn.getStyleClass().add("subBtn");
        }
    }

    private void videoPageHandler(int videoID) {
        Navigator.gotoVideoPage((Stage) (leftPanelMin).getScene().getWindow(), isDarkmode, videoID);
    }

    private void channelPageHandler(MouseEvent me, int userID) {
        Navigator.gotoChannelPage((Stage) (leftPanelMin).getScene().getWindow(), isDarkmode, userID, "videos");
        me.consume();
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

    private String secToMinuteFormat(double seconds) {
        int sec = (int) seconds;
        String secondsStringPart = (sec % 60) < 10 ? ("0" + sec % 60) : String.valueOf(sec % 60);

        return sec / 60 + ":" + secondsStringPart;
    }

    @FXML
    public void pauseBtnHandler(MouseEvent me) {
        pauseHandler();
    }

    private void pauseHandler() {
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
    public void videoKeyPressedHandler(KeyEvent ke) {
        System.out.println(ke.getCharacter());
    }

    @FXML
    public void mousePressesVideoHandler(MouseEvent me) {
        if (me.getClickCount() == 1) pauseHandler();
        if (me.getClickCount() == 2) ((Stage) leftPanelMin.getScene().getWindow()).setFullScreen(true);
    }

    @FXML
    public void mouseEnterVideoHandler(MouseEvent me) {
        pauseBtn.setVisible(true);
        if (sliderThumb != null) sliderThumb.setStyle("-fx-opacity: 1");
        nextVideoBtn.setVisible(true);
        muteVideoBtn.setVisible(true);
        timeSlider.setVisible(true);
        videoTimeLabel.setVisible(true);
    }

    @FXML
    public void mouseExitVideoHandler(MouseEvent me) {
        pauseBtn.setVisible(false);
        if (sliderThumb != null) sliderThumb.setStyle("-fx-opacity: 0");
        nextVideoBtn.setVisible(false);
        muteVideoBtn.setVisible(false);
        timeSlider.setVisible(false);
        videoTimeLabel.setVisible(false);
    }


    @FXML
    public void nextVideoBtnHandler(MouseEvent me) {
        // Todo: Show The Next Video
    }

    @FXML
    public void muteVideoBtnHandler(MouseEvent me) {
        muteVideoHandlerFunc();
    }

    private void muteVideoHandlerFunc() {
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
    public void soundAreaMouseEnterHandler(MouseEvent me) {
        soundSlider.setVisible(true);
        AnchorPane.setLeftAnchor(videoTimeLabel, 290.0);
    }

    @FXML
    public void soundAreaMouseExitHandler(MouseEvent me) {
        soundSlider.setVisible(false);
        AnchorPane.setLeftAnchor(videoTimeLabel, 135.0);
    }

    @FXML
    public void homeBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoHomePage((Stage) (leftPanelMin).getScene().getWindow(), isDarkmode);
    }

    @FXML
    public void signinBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoSigninPage((Stage) (leftPanelMin).getScene().getWindow(), isDarkmode);
    }

    @FXML
    public void dashboardBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoDashboard((Stage) (leftPanelMin).getScene().getWindow(), isDarkmode,"account");
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
