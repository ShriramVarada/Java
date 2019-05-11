
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server{

    private final List<Channel> channelList = new CopyOnWriteArrayList<>();

    private List<String> onlineUsers = new CopyOnWriteArrayList<>();

    private ServerSocket serverSocket;

    private ExecutorService executorService;

    public Server(int port){
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
                        outputtoClient.close();
                        inputfromClient.close();
                        socket.close();
                        return;
                    }

                    message = inputfromClient.readLine();
                    if(message.startsWith(":"))
                    {
                        username = message.substring(1);
                        if(onlineUsers.contains(username))
                        {
                            sendMessage("USERNAMEAINUSE");
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
                        channelnumber = message.charAt(11) - '0';
                        channelList.get(channelnumber).activeUsernames.remove(username);
                    }
                    else if(message.startsWith("ENTER"))
                    {
                        //TODO handle the channel chat here
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
