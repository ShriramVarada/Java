
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server{

    private final List<Channel> channelList = new CopyOnWriteArrayList<>();

    private List<String> onlineUsers;

    private ServerSocket serverSocket;

    private ExecutorService executorService;

    private Connection connection;

    public Server(int port){
        onlineUsers = new CopyOnWriteArrayList<>();
        executorService = Executors.newCachedThreadPool();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/sample" +
                    "user=root&password=Narayana!2");
        }catch(SQLException e){
            e.printStackTrace();
        }
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();

        }
        /*
        Change this later to the client having to create channels
         */
        channelList.add(new Channel("Channel1"));
        channelList.add(new Channel("Channel2"));
    }

    private void createClientThread(Socket socket){
        executorService.execute(new ClientThread(socket));
    }

    private class ClientThread implements Runnable{
        private Socket socket;
        private BufferedReader inputfromClient;
        private PrintWriter outputtoClient;
        String username;
        private int messageNumber;
        ClientThread(Socket socket){
            this.socket = socket;
            try {
                outputtoClient = new PrintWriter(socket.getOutputStream());
                inputfromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            outputtoClient.println("Connected");
            System.out.println("Connected");
            outputtoClient.flush();
            String message;
            int channelnumber = 0;
            while(true){
                try{

                    if(Thread.interrupted())
                    {
                        onlineUsers.remove(username);
                        outputtoClient.close();
                        inputfromClient.close();
                        socket.close();
                        return;
                    }

                    message = inputfromClient.readLine();

                    // For signing in
                    if(message.startsWith(":"))
                    {
                        String username = message.substring(1);
                        String password;
                        Statement statement = null;
                        ResultSet resultSet = null;
                        // TODO See if username in use from the database
                        try{
                            statement = connection.createStatement();
                            resultSet = statement.executeQuery("SELECT * FROM (SELECT EXISTS(SELECT * FROM usernames WHERE Username="+username+")) AS SAMPLE");
                            resultSet.absolute(1);
                            if(Integer.parseInt(resultSet.getNString(1)) == 0){
                                sendMessage("--");
                            }else{
                                sendMessage("++");
                                if(!inputfromClient.readLine().startsWith("p***")){

                                    password = inputfromClient.readLine();
                                    resultSet = statement.executeQuery("SELECT * FROM (SELECT Password FROM usernames WHERE Username="+username+") AS sample2");
                                    byte[] userpass = resultSet.getBytes(1);

                                    resultSet = statement.executeQuery("SELECT * FROM (SELECT DES_ENCRYPT("+password+"\"0AD243\")) AS SAMPLE3");
                                    byte[] datapass = resultSet.getBytes(1);

                                    if(Arrays.equals(userpass, datapass)) {
                                        sendMessage("AAAA");
                                    }else{
                                        sendMessage("XXxx");
                                    }
                                }
                            }
                        }catch(SQLException e){
                            e.printStackTrace();
                        }finally {
                            if (resultSet != null) {
                                try {
                                    resultSet.close();
                                } catch (SQLException sqlEx) { } // ignore

                                resultSet = null;
                            }

                            if (statement != null) {
                                try {
                                    statement.close();
                                } catch (SQLException sqlEx) { } // ignore

                                statement = null;
                            }
                        }
                    }
                    else if(message.startsWith(";"))
                    {
                        String username = message.substring(1);
                        String password;
                        Statement statement = null;
                        ResultSet resultSet = null;

                        try{
                            statement = connection.createStatement();
                            resultSet = statement.executeQuery("SELECT * FROM (SELECT NOT EXISTS(SELECT * FROM usernames WHERE Username="+username+")) AS SAMPLE");
                            resultSet.absolute(1);
                            if(Integer.parseInt(resultSet.getNString(1)) == 0){
                                sendMessage("---");
                            }else{
                                sendMessage("+++");
                                password = inputfromClient.readLine();
                                statement.executeQuery("INSERT INTO Usernames VALUES ("+username+", DES_ENCRYPT("+password+", \"0AD243\"))");
                            }
                        }catch(SQLException e){

                        }finally {
                            if (resultSet != null) {
                                try {
                                    resultSet.close();
                                } catch (SQLException sqlEx) { } // ignore

                            }

                            if (statement != null) {
                                try {
                                    statement.close();
                                } catch (SQLException sqlEx) { } // ignore

                            }
                        }
                    }
                    else if (message.startsWith("JOIN"))
                    {
                        channelnumber = message.charAt(11) - '0';
                        channelList.get(channelnumber).Users.put(username, Boolean.TRUE);
                        channelList.get(channelnumber).activeUsernames.add(username);
                        channelList.get(channelnumber).clientsUsernames.add(username);

                        // TODO handle if username not present. This is where Mysql comes in for handling usernames in channels

                        sendMessage("?*"+message.substring(4));
                    }
                    else if(message.equals("EXIT"))
                    {
                        channelList.get(channelnumber).Users.put(username, Boolean.FALSE);
                    }
                    else if(message.startsWith("EXIT"))
                    {
                        // See if channel number equals in the message and if so, then exit otherwise send mesage
                        // that wrong channel
                        int prevchannel = channelnumber;

                        channelList.get(channelnumber).Users.put(username, Boolean.FALSE);
                        channelList.get(channelnumber).activeUsernames.remove(username);
                    }
                    else if(message.startsWith("ENTER"))
                    {
                        int prevchannel = channelnumber;
                        channelnumber = message.charAt(12) - '0';
                        if(channelnumber != prevchannel) {
                            sendMessage("STARTCHANNELTRANS");

                            for (String message2 : channelList.get(channelnumber).messages) {
                                sendMessage(message2);
                            }

                            sendMessage("ENDCHANNELTRANS");
                            messageNumber = channelList.get(channelnumber).messages.size();
                        }

                    }
                    else if(message.startsWith("@@@"))
                    {
                        Thread.currentThread().interrupt();
                    }
                    else if(message.startsWith())
                    {

                    }
                    else
                    {
                        // if not in any channel, then send message that not in any channel

                        channelList.get(channelnumber).messages.add(message);
                        for (int i = messageNumber; i < channelList.get(channelnumber).messages.size(); i++)
                        {
                            sendMessage(channelList.get(channelnumber).messages.get(i));
                        }
                        messageNumber = channelList.get(channelnumber).messages.size();
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message)
        {
            outputtoClient.println(message);
            outputtoClient.flush();
        }

    }

    public static void main(String[] args){

        Server server = new Server(6000);

        Socket socket;
        while(true) {
            try {
                socket = server.serverSocket.accept();
                server.createClientThread(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
