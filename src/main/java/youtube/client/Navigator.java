package youtube.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import youtube.YoutubeApplication;
import youtube.client.controllers.*;

import java.io.IOException;

public class Navigator {
    private static final String darkTheme = YoutubeApplication.class.getResource("styles/dark-theme.css").toExternalForm();
    private static final String lightTheme = YoutubeApplication.class.getResource("styles/light-theme.css").toExternalForm();

    public static void gotoSigninPage(Stage appStage, DataService dataService, boolean isDarkmode) {
        // Load the Menu View
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("signin.fxml"));
        fxmlLoader.setController(new SigninController(dataService, isDarkmode));

        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 1200, 600);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        scene.getStylesheets().addAll(
                YoutubeApplication.class.getResource("styles/signup.css").toExternalForm(),
                YoutubeApplication.class.getResource("styles/template.css").toExternalForm()
        );
        scene.getStylesheets().add(isDarkmode ? darkTheme : lightTheme);

        Stage stage = appStage;
        stage.setScene(scene);
        stage.show();
    }

    public static void gotoSignupPage(Stage appStage, DataService dataService, boolean isDarkmode) {
        // Load the Menu View
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("signup.fxml"));
        fxmlLoader.setController(new SignupController(dataService, isDarkmode));

        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 1200, 600);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        scene.getStylesheets().addAll(
                YoutubeApplication.class.getResource("styles/signup.css").toExternalForm(),
                YoutubeApplication.class.getResource("styles/template.css").toExternalForm()
        );
        scene.getStylesheets().add(isDarkmode ? darkTheme : lightTheme);

        Stage stage = appStage;
        stage.setScene(scene);
        stage.show();
    }

    public static void gotoHomePage(Stage appStage, DataService dataService, boolean isDarkmode) {
        // Load the Menu View
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("homepage.fxml"));
        fxmlLoader.setController(new HomepageController(dataService, isDarkmode));

        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 1200, 600);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        scene.getStylesheets().addAll(
                YoutubeApplication.class.getResource("styles/homepage.css").toExternalForm(),
                YoutubeApplication.class.getResource("styles/template.css").toExternalForm()
        );
        scene.getStylesheets().add(isDarkmode ? darkTheme : lightTheme);

        Stage stage = appStage;
        stage.setScene(scene);
        stage.show();
    }

    public static void gotoVideoPage(Stage appStage, DataService dataService, boolean isDarkmode) {
        // Load the Menu View
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("videopage.fxml"));
        fxmlLoader.setController(new VideopageController(dataService, isDarkmode));

        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 1200, 600);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        scene.getStylesheets().addAll(
                YoutubeApplication.class.getResource("styles/videopage.css").toExternalForm(),
                YoutubeApplication.class.getResource("styles/template.css").toExternalForm()
        );
        scene.getStylesheets().add(isDarkmode ? darkTheme : lightTheme);

        Stage stage = appStage;
        stage.setScene(scene);
        stage.show();
    }

    public static void gotoChannelPage(Stage appStage, DataService dataService, boolean isDarkmode) {
        // Load the Menu View
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("channelpage.fxml"));
        fxmlLoader.setController(new ChannelpageController(dataService, isDarkmode));

        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 1200, 600);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        scene.getStylesheets().addAll(
                YoutubeApplication.class.getResource("styles/channelpage.css").toExternalForm(),
                YoutubeApplication.class.getResource("styles/template.css").toExternalForm()
        );
        scene.getStylesheets().add(isDarkmode ? darkTheme : lightTheme);

        Stage stage = appStage;
        stage.setScene(scene);
        stage.show();
    }

}