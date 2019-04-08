import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends Application implements Runnable{

    private HBox channelButtons;

    private TextField enterMessage;

    private String[] args;

    private static int clientNumber;

    private TextArea textArea;

    private PrintWriter out;
    private InputStreamReader in;

    public String getClientusername() {
        return username;
    }

    public void setClientusername(String username) {
        this.username = username;
    }

    private String username;

    public Client(String hostname, int port){
        try {
            Socket socket = new Socket(hostname, port);
            out = new PrintWriter(socket.getOutputStream());
            in = new InputStreamReader(socket.getInputStream());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        launch(args);
        // TODO create buttons in HBox according to commands
    }

    public void createButtons(){

    }

    public void getChannelText(){

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

        textArea = new TextArea();
        borderpane.setCenter(textArea);

        enterMessage = new TextField();
        borderpane.setBottom(enterMessage);

        enterMessage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String outmessage = enterMessage.getText();
                try {
                    out.println(outmessage);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        primaryStage.setScene(new Scene(borderpane, 300,300));
        String clientnumbers = "Client "+ clientNumber;
        primaryStage.setTitle(clientnumbers);
        primaryStage.show();
    }

    public static void main(String[] args){
        clientNumber = Integer.parseInt(args[0]);
        Client client = new Client("localhost", 3306);
        client.args = args;
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(client);
        executorService.shutdown();
    }
}
