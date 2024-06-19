package youtube;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigator {
    private static final String darkTheme = Navigator.class.getResource("styles/dark-theme.css").toExternalForm();
    private static final String lightTheme = Navigator.class.getResource("styles/light-theme.css").toExternalForm();

    public static void gotoSigninPage(Stage appStage, boolean isDarkmode){
        // Load the Menu View
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("signin.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error While Getting to Sign in Page: " + e.getMessage());
        }

        ((SigninController) fxmlLoader.getController()).setTheme(isDarkmode);

        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().addAll(Navigator.class.getResource("styles/signup.css").toExternalForm(), Navigator.class.getResource("styles/template.css").toExternalForm());
        scene.getStylesheets().add(isDarkmode ? darkTheme : lightTheme);

        Stage stage = appStage;
        stage.setScene(scene);
        stage.show();
    }

    public static void gotoSignupPage(Stage appStage, boolean isDarkmode){
        // Load the Menu View
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("signup.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error While Getting to HomePage: " + e.getMessage());
        }

        ((SignupController) fxmlLoader.getController()).setTheme(isDarkmode);

        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().addAll(Navigator.class.getResource("styles/signup.css").toExternalForm(), Navigator.class.getResource("styles/template.css").toExternalForm());
        scene.getStylesheets().add(isDarkmode ? darkTheme : lightTheme);

        Stage stage = appStage;
        stage.setScene(scene);
        stage.show();
    }

    public static void gotoHomePage(Stage appStage, boolean isDarkmode){
        // Load the Menu View
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("homepage.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error While Getting to HomePage: " + e.getMessage());
        }

        ((HomepageController) fxmlLoader.getController()).setTheme(isDarkmode);

        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().addAll(Navigator.class.getResource("styles/homepage.css").toExternalForm(), Navigator.class.getResource("styles/template.css").toExternalForm());
        scene.getStylesheets().add(isDarkmode ? darkTheme : lightTheme);

        Stage stage = appStage;
        stage.setScene(scene);
        stage.show();
    }

    public static void gotoVideoPage(Stage appStage, boolean isDarkmode){
        // Load the Menu View
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("videopage.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error While Getting to HomePage: " + e.getMessage());
        }

        ((VideopageController) fxmlLoader.getController()).setTheme(isDarkmode);

        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().addAll(Navigator.class.getResource("styles/videopage.css").toExternalForm(), Navigator.class.getResource("styles/template.css").toExternalForm());
        scene.getStylesheets().add(isDarkmode ? darkTheme : lightTheme);

        Stage stage = appStage;
        stage.setScene(scene);
        stage.show();
    }

    public static void gotoChannelPage(Stage appStage, boolean isDarkmode){
        // Load the Menu View
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeApplication.class.getResource("channelpage.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error While Getting to HomePage: " + e.getMessage());
        }

        ((ChannelpageController) fxmlLoader.getController()).setTheme(isDarkmode);

        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().addAll(Navigator.class.getResource("styles/channelpage.css").toExternalForm(), Navigator.class.getResource("styles/template.css").toExternalForm());
        scene.getStylesheets().add(isDarkmode ? darkTheme : lightTheme);

        Stage stage = appStage;
        stage.setScene(scene);
        stage.show();
    }

}
