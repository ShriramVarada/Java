import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends Application implements Runnable{

    private HBox channelButtons = new HBox(5);

    private TextField enterMessage = new TextField();

    private String[] args;

    private static int clientNumber;

    private TextArea textArea = new TextArea();

    private PrintWriter out;
    private BufferedReader in;

    private Scene scene;
    private Scene scene2;

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
        String message;
        while(true)
        {
            try{
                if(Thread.interrupted())
                {
                    out.println("@@@");
                    out.close();
                    in.close();
                    socket.close();
                    return;
                }
                message = in.readLine();
                System.out.println(message);
                textArea.setText("fsd");

                // Handle if not in any channel

                if (message.startsWith("?*"))
                {
                    createButtons(message);
                }
                /* Different
                 * channel message
                */
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
                else if(message.equals("USERNAMEINUSE"))
                {
                    textArea.setText("That username is already in use. Try a different one");
                }
                else if(message.startsWith("***"))
                {
                    textArea.appendText(message.substring(3));
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message){
        out.println(message);
        out.flush();
    }

    private void createButtons(String message){
        VBox vbox = new VBox(2);
        String channelNumber = message.substring(2,3).toUpperCase() + message.substring(3);
        Button button = new Button(channelNumber);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendMessage("ENTER"+channelNumber);
            }
        });
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendMessage("EXIT" + channelNumber);
            }
        });
        vbox.getChildren().addAll(button, exitButton);
        channelButtons.getChildren().addAll(vbox);
    }


    @Override
    public void start(Stage primaryStage) {
        BorderPane borderpane = new BorderPane();
        channelButtons.setStyle("-fx-background-color: #008080");
        channelButtons.setPadding(new Insets(12,12,12,12));
        borderpane.setTop(channelButtons);

        borderpane.setCenter(textArea);
        borderpane.setBottom(enterMessage);

        enterMessage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String outmessage = enterMessage.getText();
                try {
                    sendMessage(outmessage);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        scene = new Scene(borderpane, 300,300);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        Button btn2 = new Button("Register");
        HBox hbBtn2 = new HBox(10);
        hbBtn2.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn2.getChildren().add(btn2);
        grid.add(btn2, 1, 5);

        scene2 = new Scene(grid, 200,200);

        btn.setOnAction((ActionEvent e) -> {
            String username, password;
            username = userTextField.getText();
            password = pwBox.getText();
            if(username.startsWith(" ") || username.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Enter your username", ButtonType.OK);
                alert.showAndWait();

                if(alert.getResult() == ButtonType.OK){
                    alert.close();
                }
            }else{

                sendMessage(":"+username);
                try {
                    if (in.readLine().equals("++")) {
                        if (password.equals("") || password.startsWith("")) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please enter password", ButtonType.OK);
                            alert.showAndWait();

                            if (alert.getResult() == ButtonType.OK) {
                                alert.close();
                            }
                        } else {
                            sendMessage("p***");
                            sendMessage(password);

                            primaryStage.setScene(scene);
                            primaryStage.setTitle("Chat");
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Uername incorrect", ButtonType.CLOSE);
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.CLOSE) {
                            alert.close();
                        }
                    }
                }catch(IOException er){
                    er.printStackTrace();
                }
            }
        });


        btn2.setOnAction((ActionEvent e) -> {
            String username = userTextField.getText();
            String password = pwBox.getText();

            if(username.startsWith(" ") || username.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please enter a username to REGISTER", ButtonType.OK);
                alert.showAndWait();

                if(alert.getResult() == ButtonType.OK){
                    alert.close();
                }
            }else{
                sendMessage(";"+username);
                try {
                    if (in.readLine().equals("+++")) {
                        sendMessage("p****");
                        sendMessage(password);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Username already taken", ButtonType.OK);
                        alert.showAndWait();

                        if (alert.getResult() == ButtonType.OK) {
                            alert.close();
                        }
                    }
                }catch(IOException er){
                    er.printStackTrace();
                }
            }


        });

        primaryStage.setScene(scene2);
        primaryStage.setTitle("LOGIN");
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.out.println("Stopping...");
        Thread.currentThread().interrupt();
    }

    public static void main(String[] args){

        Client client = new Client();
        client.init(args, "localhost", 6000);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(client);
        executorService.shutdown();

    }
}
