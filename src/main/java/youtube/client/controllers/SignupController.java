package youtube.client.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import youtube.YoutubeApplication;
import youtube.client.*;
import youtube.server.models.User;

public class SignupController {

    @FXML
    public TextField emailInput;
    @FXML
    public TextField usernameInput;
    @FXML
    public PasswordField passwordInput;
    @FXML
    public Label toastText;

    private boolean isDarkmode = false;
    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    public SignupController(boolean isDarkmode) {
        this.isDarkmode = isDarkmode;
    }

    @FXML
    public void signupBtnHandler(ActionEvent ae) {
        Task<Response> signupTask = Requests.CREATE_USER(emailInput.getText(), usernameInput.getText(), passwordInput.getText());

        signupTask.setOnSucceeded(event -> {
            boolean isError = signupTask.getValue().isError();
            var res = signupTask.getValue();
            System.out.println(res.getMessage());

            if(isError)
                showToast(res.getMessage(), Color.RED);
            else{
                showToast(res.getMessage(), Color.GREEN);
                Config.currentUser = new User();
                Config.currentUser.setId(res.getId());
                Config.currentUser.setUsername(usernameInput.getText());
                Config.currentUser.setEmail(emailInput.getText());
                homeBtnHandler(ae);
            }
        });
        new Thread(signupTask).start();
    }

    public void showToast(String text, Color textColor){
        toastText.setText(text);
        toastText.setTextFill(textColor);

        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(500), new KeyValue(toastText.opacityProperty(), 1));
        fadeInTimeline.getKeyFrames().add(fadeInKey1);
        fadeInTimeline.setOnFinished((ae) ->
        {
            new Thread(() -> {
                try {
                    Thread.sleep(3500);
                }
                catch (InterruptedException ignored) {}

                Timeline fadeOutTimeline = new Timeline();
                KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(500), new KeyValue(toastText.opacityProperty(), 0));
                fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
                fadeOutTimeline.setOnFinished((aeb) -> toastText.setOpacity(0));
                fadeOutTimeline.play();
            }).start();
        });
        fadeInTimeline.play();
    }

    @FXML
    public void signinBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoSigninPage((Stage) (usernameInput).getScene().getWindow(), isDarkmode);
    }

    @FXML
    public void homeBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoHomePage((Stage) (usernameInput).getScene().getWindow(), isDarkmode);
    }


    @FXML
    public void toggleTheme(ActionEvent actionEvent) {
        if (usernameInput.getScene().getStylesheets().contains(darkTheme)){
            isDarkmode = false;
            usernameInput.getScene().getStylesheets().remove(darkTheme);
            usernameInput.getScene().getStylesheets().add(lightTheme);
        }else {
            isDarkmode = true;
            usernameInput.getScene().getStylesheets().remove(lightTheme);
            usernameInput.getScene().getStylesheets().add(darkTheme);
        }
    }
}
