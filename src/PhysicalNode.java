import java.util.HashMap;
import java.util.Map;

public class PhysicalNode implements IPhysicalNode {

    private String id;
    private Map<String, IVirtualNode> virtualNodes;

    public PhysicalNode(String nodeId) {
        this.id = nodeId;
        this.virtualNodes = new HashMap<String, IVirtualNode>();
    }

    @Override
    public String getNodeId() {
        return this.id;
    }

    @Override
    public void registerVirtualNode(IVirtualNode virtualNode) {
        if (!this.virtualNodes.containsKey(virtualNode.getNodeId())) {
            this.virtualNodes.put(virtualNode.getNodeId(), virtualNode);
            virtualNode.setPhysicalNodeId(this.id);
        }
    }

    @Override
    public IVirtualNode getVirtualNode(String virtualNodeId) {
        return this.virtualNodes.get(virtualNodeId);
    }

    @Override
    public Map<String, IVirtualNode> getVirtualNodes() {
        return this.virtualNodes;
    }

    @Override
    public void transferVirtualNode(String virtualNodeId, IPhysicalNode physicalNodeId) {
        if (this.virtualNodes.containsKey(virtualNodeId)) {
            IVirtualNode nodeToTransfer = this.getVirtualNode(virtualNodeId);
            if (nodeToTransfer != null) {
                this.virtualNodes.remove(virtualNodeId);
                physicalNodeId.registerVirtualNode(nodeToTransfer);
            }
        }
    }
}
