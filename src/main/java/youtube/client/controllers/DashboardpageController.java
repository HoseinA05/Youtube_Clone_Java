package youtube.client.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import youtube.YoutubeApplication;
import youtube.client.*;
import youtube.server.models.User;

import java.io.File;

public class DashboardpageController {

    @FXML
    public VBox leftPanelMax;
    @FXML
    public Button accountBtnMax, contentBtnMax, accountBtnMin, contentBtnMin, createPanelBtn; //left buttons

    @FXML
    public VBox leftPanelMin, accountInfoPanel, contentPanel, createPanel, videosSection, playlistSection;

    @FXML
    public ImageView profileMinImg, profileMaxImg, accountPanelProfileImg, editVideoThumbnail;

    @FXML
    public CheckBox videoPrivateCheckBox;

    private boolean isDarkmode = false;
    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    private User user;
    private String section;

    public DashboardpageController(boolean isDarkmode, String section) {
        this.isDarkmode = isDarkmode;
        this.section = section;
    }

    public void initialize() {
        if (Config.currentUser == null || Config.currentUser.getId() == 0) {
            throw new RuntimeException("Only signed in users should come");
        }
        user = Config.currentUser;

        switch (section) {
            case "account":
                accountBtnMax.getStyleClass().add("selected");
                accountBtnMin.getStyleClass().add("selected");
                initAccount();
                break;
            case "videos":
                contentBtnMax.getStyleClass().add("selected");
                contentBtnMin.getStyleClass().add("selected");
                createPanel.setVisible(true);
                createPanel.setManaged(true);
                initVideos();
                break;
            case "playlists":
                contentBtnMax.getStyleClass().add("selected");
                contentBtnMin.getStyleClass().add("selected");
                initPlaylists();
                break;
            case "create-video":
                contentBtnMax.getStyleClass().add("selected");
                contentBtnMin.getStyleClass().add("selected");
                initCreateVideo();
                break;
            case "edit-video":
                contentBtnMax.getStyleClass().add("selected");
                contentBtnMin.getStyleClass().add("selected");
                initEditVideo();
                break;
            case "create-playlist":
                contentBtnMax.getStyleClass().add("selected");
                contentBtnMin.getStyleClass().add("selected");
                initCreatePlaylist();
                break;
            case "edit-playlist":
                contentBtnMax.getStyleClass().add("selected");
                contentBtnMin.getStyleClass().add("selected");
                initEditPlaylist();
                break;
            default:
                throw new RuntimeException("invalid section");
        }
    }

    private void initAccount() {
        accountInfoPanel.setVisible(true);
        accountInfoPanel.setManaged(true);

        // load profile image
        Task<String> userImageTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                if (user.getProfilePhotoPath().isEmpty()) {
                    profileMinImg.setImage(new Image(YoutubeApplication.class.getResource("default_pf.png").toExternalForm()));
                } else {
                    profileMinImg.setImage(new Image(FileHandler.getPhoto(user.getProfilePhotoPath())));
                }
                if (user.getProfilePhotoPath().isEmpty()) {
                    profileMaxImg.setImage(new Image(YoutubeApplication.class.getResource("default_pf.png").toExternalForm()));
                } else {
                    profileMaxImg.setImage(new Image(FileHandler.getPhoto(user.getProfilePhotoPath())));
                }
                if (user.getChannelPhotoPath().isEmpty()) {
                    accountPanelProfileImg.setImage(new Image(YoutubeApplication.class.getResource("default_thumbnail.png").toExternalForm()));
                } else {
                    accountPanelProfileImg.setImage(new Image(FileHandler.getPhoto(user.getChannelPhotoPath())));
                }

                return "";
            }
        };
        new Thread(userImageTask).start();

        Rectangle clip = new Rectangle(profileMinImg.getFitWidth(), profileMinImg.getFitHeight());
        clip.setArcWidth(50);
        clip.setArcHeight(50);
        profileMinImg.setClip(clip);

        Rectangle clip2 = new Rectangle(profileMaxImg.getFitWidth(), profileMaxImg.getFitHeight());
        clip2.setArcWidth(100);
        clip2.setArcHeight(100);
        profileMaxImg.setClip(clip2);

        Rectangle clip3 = new Rectangle(accountPanelProfileImg.getFitWidth(), accountPanelProfileImg.getFitHeight());
        clip3.setArcWidth(200);
        clip3.setArcHeight(200);
        accountPanelProfileImg.setClip(clip3);
    }


    private void initVideos() {}
    private void initPlaylists() {}
    private void initCreateVideo() {}
    private void initEditVideo() {}
    private void initCreatePlaylist() {}
    private void initEditPlaylist() {}

    @FXML
    public void selectVideoHandler(ActionEvent ae) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Please choose a Video");
        // fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("jpg file", "*.jpg"));
        File file = fc.showOpenDialog(null);
        if (file != null) {
            if (!file.toString().endsWith(".mp4")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Warning");
                alert.setHeaderText("it seems you have not selected a Video file");
                alert.show();
            } else {
                // Todo: Upload Video File.
            }
        }
    }

    @FXML
    public void selectThumbnailHandler(MouseEvent mouseEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Please choose an image");
        // fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("jpg file", "*.jpg"));
        File file = fc.showOpenDialog(null);
        if (file != null) {
            // if (!file.toString().endsWith(".jpg")) {
            //     Alert alert = new Alert(Alert.AlertType.ERROR);
            //     alert.setTitle("Warning");
            //     alert.setHeaderText("it seems you have not selected an image file");
            //     alert.show();
            // } else {
            editVideoThumbnail.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    public void editVideoHandler(ActionEvent actionEvent) {
        contentPanel.setManaged(false);
        contentPanel.setVisible(false);

        createPanel.setManaged(true);
        createPanel.setVisible(true);
    }

    @FXML
    public void mouseEnterVideoTagHandler(MouseEvent mouseEvent) {
        ((Button) mouseEvent.getSource()).lookup(".tagRemoveSvg").setStyle("-fx-opacity: 1;");
    }

    @FXML
    public void mouseExitedVideoTagHandler(MouseEvent mouseEvent) {
        ((Button) mouseEvent.getSource()).lookup(".tagRemoveSvg").setStyle("-fx-opacity: 0;");
    }

    @FXML
    public void accountPanelClickHandler(ActionEvent ae) {
        // if (section != "account") {
        // }
        Navigator.gotoDashboard((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode, "account");
        // contentBtnMax.getStyleClass().remove("selected");
        // contentBtnMin.getStyleClass().remove("selected");
        // if (!accountBtnMax.getStyleClass().contains("selected")) {
        //     accountBtnMax.getStyleClass().add("selected");
        //     accountBtnMin.getStyleClass().add("selected");
        // }
        //
        // contentPanel.setVisible(false);
        // contentPanel.setManaged(false);
        // createPanel.setVisible(false);
        // createPanel.setManaged(false);
        //
        //
        // accountInfoPanel.setVisible(true);
        // accountInfoPanel.setManaged(true);
    }

    @FXML
    public void contentPanelClickHandler(ActionEvent ae) {
        Navigator.gotoDashboard((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode, "videos");
        // accountBtnMax.getStyleClass().remove("selected");
        // accountBtnMin.getStyleClass().remove("selected");
        // if (!contentBtnMax.getStyleClass().contains("selected")) {
        //     contentBtnMax.getStyleClass().add("selected");
        //     contentBtnMin.getStyleClass().add("selected");
        // }
        //
        // accountInfoPanel.setVisible(false);
        // accountInfoPanel.setManaged(false);
        // createPanel.setVisible(false);
        // createPanel.setManaged(false);
        //
        //
        // contentPanel.setVisible(true);
        // contentPanel.setManaged(true);
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

    @FXML
    public void homeBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoHomePage((Stage) (leftPanelMax).getScene().getWindow(), isDarkmode);
    }

}