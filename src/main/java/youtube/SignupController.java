package youtube;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignupController {

    @FXML
    public TextField nameInput;
    @FXML
    public TextField usernameInput;
    @FXML
    public PasswordField passwordInput;


    @FXML
    public void toggleTheme(ActionEvent actionEvent) {
        if (nameInput.getScene().getStylesheets().contains(getClass().getResource("styles/dark-theme.css").toExternalForm())){
            nameInput.getScene().getStylesheets().remove(getClass().getResource("styles/dark-theme.css").toExternalForm());
            nameInput.getScene().getStylesheets().add(getClass().getResource("styles/light-theme.css").toExternalForm());
        }else {
            nameInput.getScene().getStylesheets().remove(getClass().getResource("styles/light-theme.css").toExternalForm());
            nameInput.getScene().getStylesheets().add(getClass().getResource("styles/dark-theme.css").toExternalForm());
        }
    }
}
