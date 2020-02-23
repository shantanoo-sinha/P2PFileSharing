package client.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import client.IClient;

/**
 * The Interface IClientServer.
 * Implemented by ClientServer.java. 
 * It extends the java.rmi.Remote interface. 
 * This signifies the methods which can be invoked remotely.
 * 
 * @author Akshith
 * @author Shantanoo
 *
 */
public interface IClientServer  extends Remote{
	
	/**
	 * Gets the client name.
	 *
	 * @return the client name
	 * @throws RemoteException the remote exception
	 */
	public String getClientName() throws RemoteException;
	
	/**
	 * Send file from one client to other.
	 *
	 * @param filename the filename
	 * @param peerClient the peer client
	 * @return true, if successful
	 * @throws RemoteException the remote exception
	 */
	public boolean sendFileFromOneClientToOther(String filename, IClient peerClient) throws RemoteException;
}
