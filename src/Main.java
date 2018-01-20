import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.EventListener;


public class Main extends Application /*implements EventHandler<ActionEvent>*/ {

    Button button;
    Stage window;
    Scene scene1, scene2;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        HBox hbox = new HBox();
        Button buttonA = new Button("gs");
        Button buttonB = new Button("fh");
        hbox.getChildren().addAll(buttonA, buttonB);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hbox);
        StackPane layout = new StackPane(borderPane);
        Scene scene = new Scene(layout, 300, 300);

        window.setScene(scene);
        window.show();
    }
}
