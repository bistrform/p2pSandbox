import java.util.*;

public class VirtualNode implements IVirtualNode {

    private String nodeId;
    private String physicalNodeId;
    private ISocket socket;
    //this map holds other nodes' ids and the occupied space in it
    private List<String> otherNodes;
    private Map<String, String> data;
    private Map<String, Integer> checksums;
    private List<Message> consistencyReplies;
    private boolean checkingConsistency;
    private int consistencyWait;
    private static final int consistencyTimeout = 3;

    public String getNodeId() {
        return nodeId;
    }

    public VirtualNode(String nodeId) {
        this.nodeId = nodeId;
        socket = new Socket();
        otherNodes = new ArrayList<>();
        data = new HashMap<>();
        checksums = new HashMap<>();
        consistencyReplies = new ArrayList<>();
        checkingConsistency = false;
        consistencyWait = 0;
    }

    @Override
    public void registerOtherNodes(List<String> otherIds) {
        for (String id: otherIds) {
            if (!this.otherNodes.contains(id))
                //in the beginning all nodes are empty (for now)
                this.otherNodes.add(id);
        }
    }

    @Override
    public void setSocket(ISocket socket) {
        this.socket = socket;
    }

    @Override
    public void setPhysicalNodeId(String id) {
        this.physicalNodeId = id;
    }

    @Override
    public void process(Message message) {
        if (message != null)
            processMessage(message);

        //don't stop working while waiting for answers, only start analyzing after a timeout
        else if (checkingConsistency) {
            if (consistencyWait < consistencyTimeout) {
                System.out.println("\n\n\nConsistency waiting\n\n\n");
                consistencyWait++;
                return;
            }
            else {
                consistencyWait = 0;
                checkingConsistency = false;
                analyzeConsistencyReplies();
            }

        }
        else {
            String menuMessage = "Please select next option\n1 - insert data\n2 - get data\n3 - continue\n" +
                    "4 - get status\n5 - manually delete data\n6 - insert data locally\n7 - initiate consistency check";
            System.out.format("Node with id %s is processing\n", nodeId);
            try {
                System.out.println(menuMessage);
                Scanner userInputScanner = new Scanner(System.in);
                String reply = userInputScanner.nextLine();
                int command = Integer.parseInt(reply);
                processUserCommand(command);
            }
            catch (Exception ex) {

            }
        }
    }

    private void processUserCommand(int command) {
        switch (command) {
            case 1:
                initiatePut();
                break;

            case 2:
                initiateGet();
                break;

            case 3:
                break;

            case 4:
                System.out.println(getStatus());
                this.process(null);
                break;

            case 5:
                initiateRemoveData();
                this.process(null);
                break;

            case 6:
                initiateLocalPut();
                break;

            case 7:
                initiateConsistencyCheck();
                break;

            default:
                break;
        }
    }

    private void processMessage(Message message) {
        MessageData data = message.getMessageData();
        String messageKey = data.getKey();
        String messageValue = data.getValue();
        //if the message is a "get" request
        if (messageValue.equals("")) {
            String value = this.data.get(messageKey);
            if (value != null) {
                int checksum = this.checksums.get(messageKey);
                MessageData valueMessageData = new MessageData(messageKey, value);
                Message valueMessage = new Message(getNodeId(), message.getSender(), valueMessageData, checksum);
                valueMessage.setIsAnswer();
                socket.acceptMessage(valueMessage);
            }
        }
        //if the message is either a "put" request or a "get" answer
        else {
            if (message.isAnswer()) {
                consistencyReplies.add(message);
                System.out.format("\nThe value for the key \"%s\" is \"%s\"\n\n", messageKey, messageValue);
            }
            else {
                System.out.println("Data inserted to node " + nodeId);
                this.insertData(messageKey, messageValue);
            }
        }
    }

    private void initiateConsistencyCheck() {
        checkingConsistency = true;
        for (String key : this.data.keySet()) {
            List<String> ids = getNodeIdsByKey(key);
            boolean dataMissing = true;
            for (String id : ids) {
                MessageData data = new MessageData(key);
                Message getMessage = new Message(getNodeId(), id, data, this.checksums.get(key));
                socket.acceptMessage(getMessage);
            }
            System.out.println("\n\nsending out messages\n\n");
        }
    }

    private void analyzeConsistencyReplies() {
        System.out.println("\n\nCONSISTENCY REPORT:");
        //magic number 3 - replicas number
        if (this.consistencyReplies.size() != 3) {
            System.out.println("\n\nNOT ALL NODES HAVE REPLICAS. RESOLVING\n\n");
            String key = this.consistencyReplies.get(0).getMessageData().getKey();
            String value = this.consistencyReplies.get(0).getMessageData().getValue();
            executePut(key, value);
            System.out.println("PUT INITIATED WITH KEY " + key + " AND VALUE " + value + "\n\n");
        }
        else {
            //check checksums
            int cs1 = this.consistencyReplies.get(0).getChecksum();
            int cs2 = this.consistencyReplies.get(1).getChecksum();
            int cs3 = this.consistencyReplies.get(2).getChecksum();
            if (cs1 == cs2 && cs2 == cs3) {
                System.out.println("\n\nCHECKSUMS OK, EVERYTHING CONSISTENG\n\n");
            }
            else {
                System.out.println("\n\nCHECKSUMS WRONG, RESOLUTION REQUIRED\n\n");
            }
        }
    }

    private void insertData(String key, String value) {
        int checksum = hash(key) + hash(value);
        this.data.put(key, value);
        this.checksums.put(key, new Integer(checksum));

    }

    private void initiatePut() {
        System.out.println("Please enter data in format key:value");
        try {
            Scanner userInputScanner = new Scanner(System.in);
            String input = userInputScanner.nextLine();
            String[] splittedInput = input.split(":");
            String key = splittedInput[0];
            String value = splittedInput[1];
            executePut(key, value);
            System.out.println("Data processed");
        }
        catch (Exception ex) {

        }
    }

    private void initiateLocalPut() {
        System.out.println("Please enter local data in format key:value");
        try {
            Scanner userInputScanner = new Scanner(System.in);
            String input = userInputScanner.nextLine();
            String[] splittedInput = input.split(":");
            String key = splittedInput[0];
            String value = splittedInput[1];
            this.data.put(key, value);
            int checksum = hash(key) + hash(value);
            this.checksums.put(key, new Integer(checksum));
            System.out.println("Data processed locally");
        }
        catch (Exception ex) {

        }
    }

    private void initiateRemoveData() {
        try {
            System.out.println("Please enter data key");
            Scanner userInputScanner = new Scanner(System.in);
            String key = userInputScanner.nextLine();
            if (this.data.containsKey(key)) {
                this.data.remove(key);
                this.checksums.remove(key);
                System.out.println("Data removed");
            }
            else {
                System.out.println("No data with such key on node");
            }
        }
        catch (Exception ex) {

        }
    }

    private void initiateGet() {
        try {
            System.out.println("Please enter data key");
            Scanner userInputScanner = new Scanner(System.in);
            String key = userInputScanner.nextLine();
            executeGet(key);
            System.out.println("Get command executed");
        }
        catch (Exception ex) {

        }
    }

    @Override
    public void executePut(String key, String value) {
        List<String> receiverIds = getNodeIdsByKey(key);
        MessageData data = new MessageData(key, value);
        Message putMessage = new Message(getNodeId(), receiverIds, data);
        socket.acceptMessage(putMessage);
    }

    private List<String> getNodeIdsByKey(String key) {
        int centralNodeIndex = hash(key) % this.otherNodes.size();
        int leftNodeIndex = centralNodeIndex - 1;
        if (leftNodeIndex < 0) {
            leftNodeIndex = this.otherNodes.size() - 1;
        }
        int rightNodeIndex = centralNodeIndex + 1;
        if (rightNodeIndex > this.otherNodes.size() - 1) {
            rightNodeIndex = 0;
        }
        List<String> result = new ArrayList<String>();
        result.add(this.otherNodes.get(centralNodeIndex));
        result.add(this.otherNodes.get(leftNodeIndex));
        result.add(this.otherNodes.get(rightNodeIndex));
        return result;
    }

    private int hash(String id) {
        int hash=7;
        for (int i=0; i < id.length(); i++) {
            hash = hash * 31 + id.charAt(i);
        }
        return hash;
    }

    //this only sends the request to other nodes. the value is returned when the answer arrives
    @Override
    public void executeGet(String key) {
        String receiverNodeId = getNodeIdsByKey(key).get(0);
        MessageData data = new MessageData(key);
        Message getMessage = new Message(getNodeId(), receiverNodeId, data, 0);
        socket.acceptMessage(getMessage);
    }

    public String getStatus() {
        StringBuilder nodeStatus = new StringBuilder();
        nodeStatus.append("Node: " + nodeId);
        nodeStatus.append(System.getProperty("line.separator"));
        nodeStatus.append(data.toString());
        nodeStatus.append(System.getProperty("line.separator"));
        nodeStatus.append(checksums.toString());
        nodeStatus.append(System.getProperty("line.separator"));
        nodeStatus.append("Physical node: " + this.physicalNodeId);
        nodeStatus.append(System.getProperty("line.separator"));
        nodeStatus.append("Other nodes " + otherNodes.toString());
        nodeStatus.append(System.getProperty("line.separator"));
        return nodeStatus.toString();
    }

}
