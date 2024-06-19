package youtube;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class YoutubeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("homepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
        ((HomepageController) fxmlLoader.getController()).setTheme(false);
        scene.getStylesheets().addAll(getClass().getResource("styles/homepage.css").toExternalForm(), getClass().getResource("styles/template.css").toExternalForm(), getClass().getResource("styles/light-theme.css").toExternalForm());
        stage.setTitle("Youtube");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}