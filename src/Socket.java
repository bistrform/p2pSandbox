import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Socket implements ISocket {

    private List<Message> messageQueue;

    public Socket() {
        messageQueue = new LinkedList<Message>();
    }

    @Override
    public void acceptMessage(Message message) {
        messageQueue.add(message);
    }

    @Override
    public List<Message> getAllMessages() {
        List<Message> result = new LinkedList<Message>(messageQueue);
        messageQueue.clear();
        return result;
    }
}
