
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

    public Server(int port){

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
        new Thread(new ClientThread(socket)).start();
    }

    private class ClientThread implements Runnable{
        private Socket socket;
        private BufferedReader inputfromClient;
        private PrintWriter outputtoClient;
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
            String message;
            while(true){
                try{
                    message = inputfromClient.readLine();
                    if(message.startsWith("@@@"))
                    {
                        Thread.currentThread().interrupt();
                        if(Thread.interrupted())
                        {
                            //onlineUsers.remove(username);
                            inputfromClient.close();
                            outputtoClient.close();
                            socket.close();
                            return;
                        }
                    }else
                    {
                        outputtoClient.println(message);
                    }
                    outputtoClient.println("df\n");
                    /*if(message.startsWith(":"))
                    {
                        username = message.substring(1);
                        if(onlineUsers.contains(username))
                        {
                            outputtoClient.writeChars("USERNAMEAINUSE");
                        }
                        else {
                            onlineUsers.add(username);
                            outputtoClient.writeChars("USERNAME:" + message.substring(1));
                        }
                    }
                    else if (message.startsWith("JOIN"))
                    {
                        int channelnumber = message.charAt(11) - '0';
                        channelList.get(channelnumber).activeUsernames.add(username);
                        channelList.get(channelnumber).clientsUsernames.add(username);
                        outputtoClient.writeChars("?*"+message.substring(4));
                    }
                    else if(message.startsWith("EXIT"))
                    {
                        int channelnumber = message.charAt(11) - '0';
                        channelList.get(channelnumber).activeUsernames.remove(username);
                    }
                    else if(message.startsWith("ENTER"))
                    {
                        //TODO handle the channel chat here
                    }
                    else if(message.startsWith("@@@"))
                    {
                        Thread.currentThread().interrupt();
                        if(Thread.interrupted())
                        {

                        }
                    }*/
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
//            onlineUsers.remove(username);
//
//            try {
//                inputfromClient.close();
//                outputtoClient.close();
//                socket.close();
//            }catch(IOException e)
//            {
//                e.printStackTrace();
//            }
        }
    }

    public static void main(String[] args){
        Server server = new Server(6000);
        Socket socket=null;
        //ExecutorService executorService = Executors.newFixedThreadPool(3);
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
