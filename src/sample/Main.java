package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.fxmisc.richtext.demo.JavaKeywordsAsync;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle("Automata");
        Scene windowScene = new Scene(root, 1000, 700);

        primaryStage.setScene(windowScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
