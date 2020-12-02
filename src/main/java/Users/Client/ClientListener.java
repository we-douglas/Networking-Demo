package Users.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * For this implementation of the network we do not need a client listener.
 * However this stub is left here for future updates.
  */
public interface ClientListener extends Remote{
    public String clientMsg(String msg) throws RemoteException;
}
