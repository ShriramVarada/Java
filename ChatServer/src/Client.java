import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends Application implements Runnable{

    private HBox channelButtons;

    private TextField enterMessage;

    private String[] args;

    private static int clientNumber;

    public String getClientusername() {
        return username;
    }

    public void setClientusername(String username) {
        this.username = username;
    }

    private String username;


    @Override
    public void run() {
        launch(args);
    }



    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     * <p>
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane borderpane = new BorderPane();
        channelButtons = new HBox(5);
        channelButtons.setStyle("-fx-background-color: #008080");
        channelButtons.setPadding(new Insets(12,12,12,12));
        borderpane.setTop(channelButtons);

        TextArea textarea = new TextArea();
        borderpane.setCenter(textarea);

        enterMessage = new TextField();
        borderpane.setBottom(enterMessage);

        primaryStage.setScene(new Scene(borderpane, 300,300));
        String clientnumbers = "Client "+ clientNumber;
        primaryStage.setTitle(clientnumbers);
        primaryStage.show();
    }

    public static void main(String[] args){
        clientNumber = Integer.parseInt(args[0]);
        Client client = new Client();
        client.args = args;
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(client);
        executorService.shutdown();

    }
}
