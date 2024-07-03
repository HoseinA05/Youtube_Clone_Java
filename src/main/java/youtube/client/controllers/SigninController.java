package youtube.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import youtube.YoutubeApplication;
import youtube.client.DataService;
import youtube.client.Navigator;

public class SigninController {

    @FXML
    public TextField usernameInput;
    @FXML
    public PasswordField passwordInput;

    private DataService dataService;

    private boolean isDarkmode = false;
    private final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();



    public SigninController(DataService dataService, boolean isDarkmode) {
        this.dataService = dataService;
        this.isDarkmode = isDarkmode;
    }

    public void setTheme(boolean isDarkmode) {
        this.isDarkmode = isDarkmode;
    }

    @FXML
    public void createAccountBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoSignupPage((Stage) (usernameInput).getScene().getWindow(), dataService, isDarkmode);
    }

    @FXML
    public void homeBtnHandler(ActionEvent actionEvent) {
        Navigator.gotoHomePage((Stage) (usernameInput).getScene().getWindow(), dataService, isDarkmode);
    }

    @FXML
    public void toggleTheme(ActionEvent actionEvent) {
        if (usernameInput.getScene().getStylesheets().contains(darkTheme)) {
            isDarkmode = false;
            usernameInput.getScene().getStylesheets().remove(darkTheme);
            usernameInput.getScene().getStylesheets().add(lightTheme);
        } else {
            isDarkmode = true;
            usernameInput.getScene().getStylesheets().remove(lightTheme);
            usernameInput.getScene().getStylesheets().add(darkTheme);
        }
    }

}
