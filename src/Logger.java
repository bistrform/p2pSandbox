import java.util.ArrayList;
import java.util.List;

public class Logger {

    private long tick;
    private int processedNumberOfMessages;
    private List<Message> sentMessages;
    private List<Integer> isolatedNodes;
    private List<VirtualNode> nodes;

    public Logger() {
        tick = 0;
        processedNumberOfMessages = 0;
        sentMessages = new ArrayList<Message>();
        isolatedNodes = new ArrayList<Integer>();
        nodes = new ArrayList<VirtualNode>();
    }

    public void nodeCreated(VirtualNode node) {
        nodes.add(node);
    }

    public void setTick(long tick) {
        this.tick = tick;
    }

    public void messageSent(Message message) {
        sentMessages.add(message);
    }

    public void messageProcessed(){
        processedNumberOfMessages++;
    }

    public void nodeIsolated(int nodeId) {
        isolatedNodes.add(nodeId);
    }

    public void printWorldReport() {
        System.out.format("\n\n========= WORLD REPORT==========\n\n" +
                "Tick #%d\n Total nodes: %d\n Nodes isolated: %d\n Total messages sent: %d\n Total messages processed: %d\n" +
                "================================\n\n",
                tick, nodes.size(), isolatedNodes.size(), sentMessages.size(), processedNumberOfMessages);
    }

}
