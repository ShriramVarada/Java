import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

    private TextArea textArea  = new TextArea();

    private Boolean stop = true;

    private PrintWriter out;
    private BufferedReader in;

    public String getClientusername() {
        return username;
    }

    public void setClientusername(String username) {
        this.username = username;
    }

    private Socket socket;

    public void init(String[] args, String hostname, int port)
    {
        this.args = args;

        try {
            socket = new Socket(hostname, port);
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private String username;


    @Override
    public void run() {
        System.out.println("sdf");
        String message="";
        System.out.println(in);
        int read=0;
        try {
            read = in.read();
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println(read);
        System.out.println("asd");
        while(true)
        {
            try{
                read = in.read();
                System.out.println(read);
                textArea.appendText("sda");
                out.println("dsf");
                /*if (message.startsWith("?*"))
                {
                    createButtons(message);
                }
                /* Different
                 * channel message

                else if (message.startsWith("STARTCHANNELTRANS"))
                {
                    textArea.clear();
                    message = "";
                    while(!message.startsWith("ENDCHANNELTRANS"))
                    {
                        textArea.appendText(message);
                        message = in.readLine();
                    }
                }
                else if(message.startsWith("USERNAME:"))
                {
                    setClientusername(message.substring(9));
                    textArea.setText("Success! Username was set");
                }
                else if(message.equals("USERNAMEAINUSE"))
                {
                    textArea.setText("That username is already in use. Try a different one");
                }
                else if(message.startsWith("***"))
                {
                    textArea.appendText(message.substring(3));
                }*/
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void createButtons(String message){
        VBox vbox = new VBox(2);
        String channelNumber = message.substring(2,3).toUpperCase() + message.substring(3);
        Button button = new Button(channelNumber);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                out.println("ENTER"+channelNumber);
            }
        });
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                out.println("EXIT" + channelNumber);
            }
        });
        vbox.getChildren().addAll(button, exitButton);
        channelButtons.getChildren().addAll(vbox);
    }


    @Override
    public void start(Stage primaryStage) {
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

    @Override
    public void stop(){
        System.out.println("Stopping...");
//        out.println("@@@");
//        try {
//            stop = false;
//            out.close();
//            in.close();
//            socket.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args){
        int clientNumber = 5;
        Client client = new Client();
        client.init(args, "localhost", 6000);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(client);
        executorService.shutdown();
        launch(args);
    }
}
