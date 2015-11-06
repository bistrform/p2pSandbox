import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Platform platform = new Platform();
        IPhysicalNode physicalNode1 = platform.createPhysicalNode();
        IPhysicalNode physicalNode2 = platform.createPhysicalNode();
        IPhysicalNode physicalNode3 = platform.createPhysicalNode();
        platform.createVirtualNode(physicalNode1);
        platform.createVirtualNode(physicalNode1);
        platform.createVirtualNode(physicalNode2);
        platform.createVirtualNode(physicalNode2);
        platform.createVirtualNode(physicalNode3);
        List<String> registeredVirtualNodes = new ArrayList(platform.getRegisteredVirtualNodes().keySet());
        for (IVirtualNode node: platform.getRegisteredVirtualNodes().values()) {
            node.registerOtherNodes(registeredVirtualNodes);
        }
        platform.run();

    }

}
