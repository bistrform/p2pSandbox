import java.util.List;

public interface ISocket {

    public void acceptMessage(Message message);

    public List<Message> getAllMessages();

}
