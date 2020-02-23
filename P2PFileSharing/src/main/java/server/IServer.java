package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import client.IClient;

/**
 * The Interface IServer. Implemented by Server class. 
 * It extends the java.rmi.Remote interface. 
 * This contains the methods which can be invoked remotely
 * by peer clients to interact with the Index Server.
 * 
 * @author Akshith
 * @author Shantanoo
 *
 */
public interface IServer extends Remote {
	
	/**
	 * Register client.
	 *
	 * @param peerClient the peer client
	 * @return the string
	 * @throws RemoteException the remote exception
	 */
	public String registerClient(IClient peerClient) throws RemoteException;
	
	/**
	 * Update peers.
	 *
	 * @param peerClient the peer client
	 * @return true, if successful
	 * @throws RemoteException the remote exception
	 */
	public boolean updatePeers(IClient peerClient) throws RemoteException;
	
	/**
	 * Search file on peers.
	 *
	 * @param file the file
	 * @param requestingPeer the requesting peer
	 * @return the i client[]
	 * @throws RemoteException the remote exception
	 */
	public IClient[] searchFileOnPeers(String file, String requestingPeer) throws RemoteException;
}
