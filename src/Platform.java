import java.util.*;


public class Platform implements IPlatform {

    // this map holds queues of messages that have to be delivered to each node
    private Map<String, Queue<Message>> messagesToProcess;
    // this map holds all of the messages each node has sent
    private Map<String, ISocket> nodeSockets;
    private Map<String, IVirtualNode> registeredVirtualNodes;
    private Map<String, IPhysicalNode> registeredPhysicalNodes;
    private long tickNumber;


    public Platform() {
        this.registeredVirtualNodes = new LinkedHashMap<String, IVirtualNode>();
        this.registeredPhysicalNodes = new LinkedHashMap<String, IPhysicalNode>();
        messagesToProcess = new HashMap<String, Queue<Message>>();
        nodeSockets = new HashMap<String, ISocket>();
        tickNumber = 0;
    }

    public Map<String, IVirtualNode> getRegisteredVirtualNodes() {
        return registeredVirtualNodes;
    }

    public IPhysicalNode createPhysicalNode() {
        String id = UUID.randomUUID().toString();
        IPhysicalNode node = new PhysicalNode(id);
        registerPhysicalNode(id, node);
        return node;
    }

    public void createVirtualNode(IPhysicalNode physicalNode) {
        String id = UUID.randomUUID().toString();
        IVirtualNode node = new VirtualNode(id);
        physicalNode.registerVirtualNode(node);
        registerVirtualNode(id, node);
    }


    @Override
    public void registerPhysicalNode(String id, IPhysicalNode node) {
        if (!registeredPhysicalNodes.containsKey(id)) {
            registeredPhysicalNodes.put(id, node);
            for (IVirtualNode virtualNode : node.getVirtualNodes().values()) {
                registerVirtualNode(virtualNode.getNodeId(), virtualNode);
            }
        }
    }

    @Override
    public void registerVirtualNode(String id, IVirtualNode node) {
        if (!registeredVirtualNodes.containsKey(id)) {
            Queue<Message> nodeQueue = new LinkedList<Message>();
            registeredVirtualNodes.put(id, node);
            ISocket nodeSocket = new Socket();
            node.setSocket(nodeSocket);
            nodeSockets.put(id, nodeSocket);
            messagesToProcess.put(id, nodeQueue);
            node.registerOtherNodes(new ArrayList(this.getRegisteredVirtualNodes().keySet()));
        }
    }

    public boolean isRegisteredNode(String id) {
        return registeredVirtualNodes.containsKey(id);
    }

    @Override
    public void run() {
        int tick = 0;
        while (tick < 2000) {
            for (IVirtualNode currentNode: registeredVirtualNodes.values()) {
                System.out.format("Tick %d. Giving control to node with id %s \n", tick, currentNode.getNodeId());
                runStep(currentNode);
                tick++;
            }
        }
    }

    private void runStep(IVirtualNode node) {
        //first let the node process the next available message
        Queue<Message> currentNodeQueue = messagesToProcess.get(node.getNodeId());
        if (currentNodeQueue != null) {
            Message nextMessageForNode = currentNodeQueue.poll();
            //the node is responsible for checking for null message
            node.process(nextMessageForNode);
        }

        //then get all of its formed messages from it's socket
        ISocket currentNodeSocket = nodeSockets.get(node.getNodeId());
        distributeResponseMessages(currentNodeSocket.getAllMessages());
    }

    private void distributeResponseMessages(List<Message> responseMessages) {
        for (Message message : responseMessages) {
            List<String> receiverIds = message.getReceiverIds();
            for (String receiverId : receiverIds) {
                Queue<Message> receiverQueue = messagesToProcess.get(receiverId);
                if (receiverQueue != null)
                    receiverQueue.add(message);
            }
        }
    }

    private IVirtualNode getNodeByIndex(int index) {
        List<IVirtualNode> nodes = new ArrayList<IVirtualNode>(registeredVirtualNodes.values());
        return nodes.get(index);
    }

    public String getStatus() {
        StringBuilder netStatus = new StringBuilder();
        netStatus.append("Net status");
        netStatus.append(System.getProperty("line.separator"));
        netStatus.append("Tick " + tickNumber);
        netStatus.append(System.getProperty("line.separator"));
        netStatus.append("Registered nodes: " + registeredVirtualNodes.toString());
        netStatus.append(System.getProperty("line.separator"));
        for (String id : messagesToProcess.keySet()) {
            netStatus.append("Queue for node " + id + " " + messagesToProcess.get(id).toString());
        }

        return netStatus.toString();
    }

}
