package client.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;

import client.IClient;
import util.Constants;

/**
 * ClientServer implements IClientServer class. Its responsible for transferring files between peers clients.
 * 
 * @author Akshith
 * @author Shantanoo
 *
 */
public class ClientServer extends UnicastRemoteObject implements IClientServer, Runnable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2738602830735419294L;
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(ClientServer.class);
	
	/** The file. */
	private String file; 
	
	/** The client. */
	private IClient client;
	
	/** The peer with file. */
	private IClientServer peerWithFile;
	
	/**
	 * Instantiates a new client server.
	 *
	 * @param file the file
	 * @param client the client
	 * @param peerWithFile the peer with file
	 * @throws RemoteException the remote exception
	 */
	public ClientServer(String file, IClient client, IClientServer peerWithFile) throws RemoteException {
		super();
		this.file = file;
		this.client = client;
		this.peerWithFile = peerWithFile;
//		System.out.println("File:" + this.file);
//		System.out.println("Client:" + this.client.getClientName());
	}

	/**
	 * Gets the peer with file.
	 *
	 * @return the peer with file
	 */
	public IClientServer getPeerWithFile() {
		return peerWithFile;
	}

	/**
	 * Sets the peer with file.
	 *
	 * @param peerWithFile the new peer with file
	 */
	public void setPeerWithFile(IClientServer peerWithFile) {
		this.peerWithFile = peerWithFile;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Sets the file.
	 *
	 * @param file the new file
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * Gets the client.
	 *
	 * @return the client
	 */
	public IClient getClient() {
		return client;
	}

	/**
	 * Sets the client.
	 *
	 * @param client the new client
	 */
	public void setClient(IClient client) {
		this.client = client;
	}

	/**
	 * Instantiates a new client server.
	 *
	 * @param file the file
	 * @param client the client
	 * @throws RemoteException the remote exception
	 */
	public ClientServer(String file, IClient client) throws RemoteException {
		super();
		this.file = file;
		this.client = client;
	}

	/**
	 * Instantiates a new client server.
	 *
	 * @throws RemoteException the remote exception
	 */
	public ClientServer() throws RemoteException {
		super();
	}

	/* (non-Javadoc)
	 * @see client.server.IClientServer#getClientName()
	 */
	@Override
	public String getClientName() throws RemoteException {
		return "";
	}

	/**
	 * Sending the file from one peer client to the requesting peer client as FileStream.
	 *
	 * @param file the file
	 * @param client the client
	 * @return true, if successful
	 * @throws RemoteException the remote exception
	 */
	public boolean sendFileFromOneClientToOther(String file, IClient client) throws RemoteException {

		// Send files from one peer client to another
		boolean fileSent = false;
		
		int bytesToRead = Integer.MIN_VALUE;
		long totalBytesRead = 0;
		long startTime = 0;
		
		byte[] byteArray = null;
		
		File fileToSend = null;
		FileInputStream fileInputStream = null;
		
		try {
			// Calculating time taken for transfer
			startTime = System.currentTimeMillis();
			
//			System.out.println("File :" + file);
//			System.out.println("Client :" + client.getClientName());
			
			logger.info("Sending '" + file + "' file to requested peer client: " + client.getClientName());
			fileToSend = new File(Constants.FILES_FOLDER  + File.separator + file);
			
			fileInputStream = new FileInputStream(fileToSend);

			byteArray = new byte[1024 * 1024];
			bytesToRead = fileInputStream.read(byteArray);
			totalBytesRead = bytesToRead;
			
			// Looping and transferring data 1MB at a time
			while (bytesToRead > 0) {
				if (client.receiveClientFile(fileToSend.getName(), byteArray, bytesToRead, totalBytesRead, false)) {
					bytesToRead = fileInputStream.read(byteArray);
					totalBytesRead += bytesToRead;
					fileSent = true;
				} else {
					logger.info("Failed to send the requested file '" + file + "'");
				}
			}
			
			// Calculating time taken for transfer
			if(fileSent) {
				logger.info("File '" + file + "' sent to the requested peer client: " + client.getClientName());
				logger.info("Time taken: "+(System.currentTimeMillis() - startTime) / 1000 + " ms");
				client.receiveClientFile(fileToSend.getName(), byteArray, bytesToRead, totalBytesRead, true);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Closing resources
			try {
				if (fileInputStream != null)
					fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileSent;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
//			System.out.println("File.:" + this.file);
//			System.out.println("Client.:" + this.client.getClientName());
			peerWithFile.sendFileFromOneClientToOther(this.file, this.client);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}