public interface IPlatform {

    public void registerPhysicalNode(String id, IPhysicalNode node);
    public void registerVirtualNode(String id, IVirtualNode node);
    public boolean isRegisteredNode(String id);

    public void run();

}
