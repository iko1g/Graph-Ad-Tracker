package app;

import app.java.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainController controller = new MainController();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/Main.fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        primaryStage.setTitle("Ad Auction Software");
        primaryStage.setScene(new Scene(root, 1200, 660));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
