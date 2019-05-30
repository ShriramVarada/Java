
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
                    if(message.startsWith(":"))
                    {
                        Boolean inUse = false;
                        username = message.substring(1);

                        for (Channel channel : channelList)
                        {
                            for (String username2: channel.clientsUsernames)
                            {
                                if(username2.equals(username))
                                {
                                    inUse = true;
                                }
                            }
                        }

                        // TODO handle this in a mysql database from the server

                        if(inUse)
                        {
                            sendMessage("USERNAMEINUSE");
                        }
                        else {
                            onlineUsers.add(username);
                            sendMessage("USERNAME:" + message.substring(1));
                        }
                    }
                    else if (message.startsWith("JOIN"))
                    {
                        channelnumber = message.charAt(11) - '0';
                        channelList.get(channelnumber).activeUsernames.add(username);
                        channelList.get(channelnumber).clientsUsernames.add(username);

                        // TODO handle if username not present. This is where Mysql comes in for handling usernames in channels

                        sendMessage("?*"+message.substring(4));
                    }
                    else if(message.startsWith("EXIT"))
                    {
                        // See if channel number equals in the message and if so, then exit otherwise send mesage
                        // that wrong channel
                        channelList.get(channelnumber).activeUsernames.remove(username);
                    }
                    else if(message.startsWith("ENTER"))
                    {
                        //TODO if already in this channel, then do nothing
                        channelnumber = message.charAt(12) - '0';
                        sendMessage("STARTCHANNELTRANS");

                        for(String message2 : channelList.get(channelnumber).messages)
                        {
                            sendMessage(message2);
                        }

                        sendMessage("ENDCHANNELTRANS");
                        messageNumber = channelList.get(channelnumber).messages.size();

                    }
                    else if(message.startsWith("@@@"))
                    {
                        Thread.currentThread().interrupt();
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

        try {
            server.connection = DriverManager.getConnection("jdbc:mysql://localhost/sample" +
                    "user=root&password=Narayana!2");
        }catch(SQLException e){
            e.printStackTrace();
        }

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
