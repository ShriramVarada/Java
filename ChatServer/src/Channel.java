import java.util.ArrayList;
import java.util.List;

public class Channel {

    public List<String> clientsUsernames;

    /**
     * This specifies the list of messages, each by a client
     */
    public List<String> messages;

    public Channel(){
        clientsUsernames = new ArrayList<>();
        messages = new ArrayList<>();
    }


}
