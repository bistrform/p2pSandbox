import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Message {

    private final MessageData messageData;
    private final String sender;
    private final List<String> receivers;
    private boolean isAnswer;
    private int checksum;

    public Message(String sender, List<String> receivers, MessageData messageData) {
        this.sender = sender;
        this.receivers = receivers;
        this.messageData = messageData;
        this.isAnswer = false;
        checksum = 0;
    }

    public Message(String sender, String receiver, MessageData messageData, int checksum) {
        this.sender = sender;
        this.receivers = new ArrayList<String>(Arrays.asList(receiver));
        this.messageData = messageData;
        this.isAnswer = false;
        this.checksum = checksum;
    }

    public String getSender() {
        return this.sender;
    }

    public List<String> getReceiverIds() {
        return this.receivers;
    }

    public MessageData getMessageData() {
        return this.messageData;
    }

    public int getChecksum() { return this.checksum; }

    public void setIsAnswer() {
        this.isAnswer = true;
    }

    public boolean isAnswer() {
        return this.isAnswer;
    }
}
