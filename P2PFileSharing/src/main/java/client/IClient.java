package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import server.IServer;

/**
 * The Interface IClient. Implemented by Client.java. 
 * It extends the java.rmi.Remote interface. 
 * This signifies the methods which can be invoked remotely.
 *
 * @author Akshith
 * @author Shantanoo
 * 
 */
public interface IClient extends Remote {
	
	/**
	 * Gets the client name.
	 *
	 * @return the client name
	 * @throws RemoteException the remote exception
	 */
	public String getClientName() throws RemoteException;
	
	/**
	 * Gets the client port.
	 *
	 * @return the client port
	 * @throws RemoteException the remote exception
	 */
	public String getClientPort() throws RemoteException;
	
	/**
	 * Gets the client directory.
	 *
	 * @return the client directory
	 * @throws RemoteException the remote exception
	 */
	public String getClientDirectory() throws RemoteException;
	
	/**
	 * Gets the client files.
	 *
	 * @return the client files
	 * @throws RemoteException the remote exception
	 */
	public String[] getClientFiles() throws RemoteException;
	
	/**
	 * Gets the server.
	 *
	 * @return the server
	 * @throws RemoteException the remote exception
	 */
	public IServer getServer() throws RemoteException;
	
	/**
	 * Update server.
	 *
	 * @throws RemoteException the remote exception
	 */
	public void updateServer() throws RemoteException;
	
	/**
	 * Receive client file.
	 *
	 * @param fileName the file name
	 * @param data the data
	 * @param bytesToRead the bytes to read
	 * @param totalBytesRead the total bytes read
	 * @return true, if successful
	 * @throws RemoteException the remote exception
	 */
	public boolean receiveClientFile(String fileName, byte[] data, int bytesToRead, long totalBytesRead, boolean fileTransferred) throws RemoteException;
}
