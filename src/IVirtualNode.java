import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IVirtualNode {

    public String getNodeId();

    public void registerOtherNodes(List<String> otherIds);

    public void setSocket(ISocket socket);

    public void setPhysicalNodeId(String id);

    public void executePut(String key, String value);

    public void executeGet(String key);

    public void process(Message message);

}
