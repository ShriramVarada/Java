

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.ArrayList;



public class Server{

    private List<Channel> channelList;

    private HashMap<Long, Channel> usersandChannels;

    private ServerSocket serverSocket;

    public Server(int port){

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();

        }
        channelList = new ArrayList<>();
        usersandChannels = new HashMap<>();
    }

    public void createClientThread(Socket socket, Server server){
        new Thread(new ClientThread(socket, server)).start();
    }

    private class ClientThread implements Runnable{
        private Socket socket;
        private Server server;

        ClientThread(Socket socket, Server server){
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            InputStream inp;
            BufferedReader inputfromClient;
            DataOutputStream outputtoClient;
            try {
                inp = socket.getInputStream();
                inputfromClient = new BufferedReader(new InputStreamReader(inp));
                outputtoClient = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                return;
            }
            String message;
            while(true){
                try{
                    message = inputfromClient.readLine();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

    public static void main(String[] args){
        Server server = new Server(3306);
        Socket socket = null;
        while(true) {
            try {
                socket = server.serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.createClientThread(socket, server);
        }
    }
}
