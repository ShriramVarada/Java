import java.util.ArrayList;
import java.util.List;

public class Channel {

    public List<String> clientsUsernames;

    public List<String> activeUsernames;

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
    }


}
