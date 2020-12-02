package Network;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {

    public String msg(String msg) throws RemoteException;
    public String ASNTmsg(String msg) throws RemoteException;
    public String cmd(int idx) throws RemoteException;
    String cmdDone(int idx,String msg) throws RemoteException;
    public String pong(int idx,String msg) throws RemoteException;
    public String ping(int idx) throws RemoteException;
    public int getFallbackPort(int idx) throws RemoteException;
    public int requestJoin(long freq,String cmd,int port) throws RemoteException;
}