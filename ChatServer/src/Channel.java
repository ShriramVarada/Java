import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Channel {

    public List<String> clientsUsernames;

    public List<String> activeUsernames;

    public HashMap<String, Boolean> Users;

    private String ChannelName;

    /**
     * This specifies the list of messages, each by a client
     */
    public List<String> messages;

    public Channel(String name){
        ChannelName = name;
        clientsUsernames = new ArrayList<>();
        messages = new ArrayList<>();
        activeUsernames = new ArrayList<>();
        Users = new HashMap<>();
    }


}
