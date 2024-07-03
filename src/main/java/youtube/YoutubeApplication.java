package youtube;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import youtube.client.DataService;
import youtube.client.controllers.HomepageController;

import java.io.IOException;

public class YoutubeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DataService dataService = new DataService();

        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("homepage.fxml"));
        fxmlLoader.setController(new HomepageController(dataService, false));

        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent doubleClicked) {
                if(doubleClicked.getClickCount() == 2)
                {
                    stage.setFullScreen(true);
                    stage.show();
                }

            }
        });

        scene.getStylesheets().addAll(
                getClass().getResource("styles/homepage.css").toExternalForm(),
                getClass().getResource("styles/template.css").toExternalForm(),
                getClass().getResource("styles/light-theme.css").toExternalForm()
        );

        stage.setTitle("Youtube");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}