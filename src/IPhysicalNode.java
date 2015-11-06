import java.util.Map;

public interface IPhysicalNode {

    public String getNodeId();

    public void registerVirtualNode(IVirtualNode virtualNode);

    public IVirtualNode getVirtualNode(String virtualNodeId);

    public Map<String, IVirtualNode> getVirtualNodes();

    public void transferVirtualNode(String virtualNodeId, IPhysicalNode physicalNodeId);

}
