package youtube;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignupController {

    @FXML
    public TextField nameInput;
    @FXML
    public TextField usernameInput;
    @FXML
    public PasswordField passwordInput;


    private boolean isDarkmode = false;
    private final String darkTheme = getClass().getResource("styles/dark-theme.css").toExternalForm();
    private final String lightTheme = getClass().getResource("styles/light-theme.css").toExternalForm();


    public void setTheme(boolean isDarkmode){
        this.isDarkmode = isDarkmode;
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
