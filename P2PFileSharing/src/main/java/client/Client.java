package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import org.apache.log4j.Logger;

import client.server.ClientServer;
import client.server.IClientServer;
import server.IServer;
import util.Constants;
import util.DirectoryWatcher;

/**
 * The Class Client. It implements the methods defined in the IClient Interface
 *
 * @author Akshith
 * @author Shantanoo
 */
public class Client extends UnicastRemoteObject implements IClient, Runnable {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(Client.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The server. */
	private IServer server;

	/** The client name. */
	private String clientName = null;

	/** The client port. */
	private String clientPort;

	/** The file to download. */
	private String fileToDownload;

	/** The client dir path. */
	private String clientDirPath = null;

	/** The files. */
	private String[] files;

	/**
	 * Instantiates a new client.
	 *
	 * @throws RemoteException
	 *             the remote exception
	 */
	public Client() throws RemoteException {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.IClient#getClientName()
	 */
	public String getClientName() {
		return clientName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.IClient#getClientFiles()
	 */
	public String[] getClientFiles() {
		return files;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.IClient#getClientDirectory()
	 */
	public String getClientDirectory() {
		return clientDirPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.IClient#getServer()
	 */
	public IServer getServer() {
		return server;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.IClient#getClientPort()
	 */
	public String getClientPort() {
		return clientPort;
	}

	/**
	 * Instantiates a new client.
	 *
	 * @param clientName the client name
	 * @param clientPort the client port
	 * @param peerServer the peer server
	 * @throws RemoteException the remote exception
	 */
	public Client(String clientName, String clientPort, IServer peerServer) throws RemoteException {

		this.clientName = clientName;
		this.clientPort = clientPort;
		this.server = peerServer;

		// Initiating peer client registration
		initiateClient(peerServer);
	}

	/**
	 * Updating the index server when a peer is added. This register all the peer client files to the Index Server.
	 *
	 * @throws RemoteException the remote exception
	 */
	public synchronized void updateServer() throws RemoteException {
		// Update the index server
		// logger.info("Updating Index Server...");
		File file = new File(clientDirPath);
		this.files = file.list();
		if (!server.updatePeers(this)) {
			logger.info("Failed to update the Index Server");
		}
	}

	/**
	 * Starts a new thread when a new peer is registered with the Index Server.
	 */
	public void run() {
		// Thread body starts
		String commandLineInput = null, filename;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		logger.info("*****************************************************************");
		logger.info("******************* Peer to Peer File Sharing *******************");
		logger.info("*****************************************************************");
		logger.info("---------------------------------------------");
		logger.info("1. Enter filename with extension to download");
		logger.info("2. Exit");
		logger.info("---------------------------------------------");
		
		while (true) {
			// Read user input
			try {
				commandLineInput = reader.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			CharSequence dot = Constants.DOT, space = Constants.SPACE;
			// Command Line Arguments for entering the Options
			if (commandLineInput != null && !commandLineInput.trim().contains(space)
					&& commandLineInput.trim().contains(dot)) {

				fileToDownload = filename = commandLineInput.trim();

				// Check if requested file is already present on the requesting server 
				// and display appropriate message
				if (Arrays.asList(this.getClientFiles()).contains(filename)) {
					logger.info(
							"Requested file is already present on this node. Please try with a different file name.");
					continue;
				}
				
				// Search the file on peer clients
				IClient[] peer = searchFileOnPeers(filename);
				/*
				 * logger.info("peer:" + peer); logger.info("peer:" + peer.length);
				 */

				// Check if peer is not null & download the file
				if (peer != null) {
					int choice = 0;
					boolean isNumber = false;
					
					// Keep on checking untill user enters a valid input
					while (!isNumber) {
						try {
							commandLineInput = reader.readLine();
							choice = Integer.parseInt(commandLineInput);
							if (choice <= 0 || choice > peer.length || peer[choice - 1] == null) {
								logger.info("Enter the index number of the client from which you want to download:");
								continue;
							}
							isNumber = true;
							break;
						} catch (Exception e) {
							logger.info("Please enter only integers");
							logger.info("Enter the index number of the client from which you want to download:");
						}
					}

					// Download the file
					downloadFile(filename, peer, choice);

				} else {
					logger.info(
							"Requested file is not present on any of the nodes. Please try with a different file name.");
				}
			} else if (commandLineInput.trim().equalsIgnoreCase("e")
					|| commandLineInput.trim().equalsIgnoreCase("exit")) {
				// Checks if the user want to exit
				try {
					// Close the resources 
					if (reader != null)
						reader.close();

					// Exit the client
					logger.info("Exiting client...");
					// Runtime.getRuntime().exec("taskkill /f /im cmd.exe");
					System.exit(0);
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// Display the command usage
				//logger.info(commandLineInput);
				logger.info("Incorrect command");
				logger.info("USAGE: <file name with extension>");
				logger.info("EXAMPLE: <123.txt>");
				logger.info("EXAMPLE: <e or exit>");
			}
		}
	}

	/**
	 * Download files from one peer client to another. It checks the files on remote peer clients
	 *
	 * @param filename the filename
	 * @param peer the peer
	 * @param choice the choice
	 */
	private void downloadFile(String filename, IClient[] peer, int choice) {
		IClientServer cServer = null;
		String peerUrl = null;
		try {
			peerUrl = Constants.RMI_LOCALHOST + peer[choice - 1].getClientPort() + Constants.CLIENT_SERVER;
			
			// Lookup the remote object
			cServer = (IClientServer) Naming.lookup(peerUrl);
			
			// Downloads the file
			downloadFile(cServer, filename);
			
			// Calculates the average file search response time
			calculateResponseTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Download file from one peer client to another.
	 *
	 * @param peerWithFile the peer with file
	 * @param filename the filename
	 */
	private void downloadFile(IClientServer peerWithFile, String filename) {

		// Download file from one peer client to another
		try {
//			System.out.println("File..:" + filename);
//			System.out.println("Client..:" + this.getClientName());
			Runnable fileSender = new ClientServer(filename, this, peerWithFile);
			Thread fileSenderThread = new Thread(fileSender);
			fileSenderThread.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Searches the file on available peer clients over the network
	 *
	 * @param filename the filename
	 * @return the i client[]
	 */
	public synchronized IClient[] searchFileOnPeers(String filename) {
		// Lookup file on peer clients
		logger.info("File requested for download:" + filename);
		int i = 0;
		IClient[] peerClient = null;
		try {
			// Returns all peer clients having the requested file
			peerClient = server.searchFileOnPeers(filename, clientName);
			if (peerClient != null) {
				logger.info("Requested file is available on the following peers:");
				// List all peers clients having the requested file				
				while (i < peerClient.length) {
					if (peerClient[i] != null)
						logger.info((i + 1) + " - " + peerClient[i].getClientName());
					i++;
				}
			} else {
				logger.info("File is not available on any of the peers. Please try with a different file.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return peerClient;
	}

	/**
	 * Receives the file from the other peer client
	 *
	 * @param fileName the file name
	 * @param data the data
	 * @param bytes the bytes
	 * @param totalBytesRead the total bytes read
	 * @return true, if successful
	 */
	public synchronized boolean receiveClientFile(String fileName, byte[] data, int bytes, long totalBytesRead) {
		// Receives the file from sending peer client
		logger.info("Requested file '" + fileName + "' is downloading. Please wait.");
		logger.info("Receiving " + bytes + " bytes");
		File newFile = null;
		FileOutputStream fileOut = null;
		boolean isFileDownloaded = false;
		try {
			// Creating file
			newFile = new File(System.getProperty(Constants.USER_DIR) + File.separator + Constants.FILES_FOLDER + File.separator + fileName);
			newFile.createNewFile();

			// Writing file content
			fileOut = new FileOutputStream(newFile, true);
			fileOut.write(data, 0, bytes);

			isFileDownloaded = true;
		} catch (Exception e) {
			isFileDownloaded = false;
			e.printStackTrace();
		} finally {
			try {
				// Closing file stream
				if (fileOut != null) {
					fileOut.flush();
					fileOut.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (isFileDownloaded)
			logger.info("Total " + totalBytesRead + " bytes received");
		else
			logger.info("Failed to downlaod the file");
		return isFileDownloaded;
	}

	/**
 	 * Calculate average response time for searching a file on the Index Server.
 	 */
	public void calculateResponseTime() {
		// Lookup a file for 1000 iterations on the Index Server and calculate the average response time
		logger.info("Calculating average file search response time...");
		int i = 0;
		long avgResponseTime = 0;
		IClient[] client;
		try {
			// Search file for 1000 iterations
			while (i < 1000) {
				long startTime = System.currentTimeMillis();
				client = server.searchFileOnPeers(fileToDownload, clientName);
				// Calculate average response time
				avgResponseTime += System.currentTimeMillis() - startTime;
				i++;
			}
			logger.info("Average file search response time of the peer client:" + this.getClientName() + " is:"
					+ avgResponseTime / 1000.000 + "ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initiate client registration.
	 *
	 * @param peerServer the peer server
	 */
	private void initiateClient(IServer peerServer) {
		// Initiates the client registration
		File file = null;
		try {
			// Get peer client file directory
			this.clientDirPath = new File(
					System.getProperty(Constants.USER_DIR) + File.separator + Constants.FILES_FOLDER).getPath();
			logger.info("Peer Client directory: " + clientDirPath.replace(Constants.TARGET, Constants.REPLACEMENT));
			// Get all files in peer client file directory
			file = new File(clientDirPath);
			if (file != null) {
				this.files = file.list();
				// Register peer client
				logger.info(peerServer.registerClient(this));
				
				// Trigger the directory watcher which watches for any file changes 
				// in the peer client directory and updates the Index Server
				new Thread(new DirectoryWatcher(this)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}