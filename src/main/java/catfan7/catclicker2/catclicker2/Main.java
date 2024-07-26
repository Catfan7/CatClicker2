package catfan7.catclicker2.catclicker2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    String version = "v1.0.2";

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Game.fxml")));
        Scene scene = new Scene(root);
        String css = this.getClass().getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("CatClicker2 - " + version);
        stage.setScene(scene);
        stage.show();

        Platform.setImplicitExit(false);

        stage.setOnCloseRequest(event -> {
            event.consume();
            Controller.logout(stage);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}