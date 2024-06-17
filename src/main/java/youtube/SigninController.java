package youtube;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SigninController {

    @FXML
    public TextField usernameInput;
    @FXML
    public PasswordField passwordInput;

    @FXML
    public void toggleTheme(ActionEvent actionEvent) {
        if (usernameInput.getScene().getStylesheets().contains(getClass().getResource("styles/dark-theme.css").toExternalForm())){
            usernameInput.getScene().getStylesheets().remove(getClass().getResource("styles/dark-theme.css").toExternalForm());
            usernameInput.getScene().getStylesheets().add(getClass().getResource("styles/light-theme.css").toExternalForm());
        }else {
            usernameInput.getScene().getStylesheets().remove(getClass().getResource("styles/light-theme.css").toExternalForm());
            usernameInput.getScene().getStylesheets().add(getClass().getResource("styles/dark-theme.css").toExternalForm());
        }
    }
}
