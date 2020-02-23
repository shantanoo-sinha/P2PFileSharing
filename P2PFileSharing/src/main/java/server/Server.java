package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import client.IClient;

/**
 * The class Server implements IServer interface.
 * It registers the peer clients to the Index Server
 * and logs its available files.
 *  
 * @author Akshith
 * @author Shantanoo
 *
 */
public class Server extends UnicastRemoteObject implements IServer {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(Server.class);
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6066745673857818638L;
	
	/** The peers. */
	private ArrayList<IClient> peers = new ArrayList<IClient>();

    /**
     * Instantiates a new server.
     *
     * @throws RemoteException the remote exception
     */
    public Server() throws RemoteException {
        super();
    }
    
    /**
     * RegisterClient registers the peer client to the Index Server
     * and logs its available files.
     *
     * @param requestingClient the requesting client
     * @return the string
     * @throws RemoteException the remote exception
     */
    public synchronized String registerClient(IClient requestingClient) throws RemoteException {
    	
    	// Register the clients
        peers.add(requestingClient);
        StringBuilder files = new StringBuilder();
        
        // Get client files
        String[] clientFiles = requestingClient.getClientFiles();
        
        // Available files
        int i = 0;
        while(i < clientFiles.length) {
        	files.append("\n=> " + clientFiles[i]);
        	i++;
        }
        logger.info("Peer Client '" + requestingClient.getClientName() + "' registered");
        
        // Show client details when Index Server is updated
        showClientDetails();
        return "Peer Client '" + requestingClient.getClientName() + "' registered with Index Server with following files:" + files;
    }

    /* (non-Javadoc)
     * @see server.IServer#updatePeers(client.IClient)
     */
    public synchronized boolean updatePeers(IClient peerClient) throws RemoteException {

    	// Update Index Server with the peers
    	int i = 0;
    	while(i < peers.size()) {
            if (peerClient.getClientName().equals(peers.get(i).getClientName())) {
                peers.remove(i);
                peers.add(peerClient);
            }
            i++;
        }
        logger.info("Index Server is updated");
        
        // Show client details when Index Server is updated
        showClientDetails();
        return true;
    }

	/**
	 * Displays the peer client details registered with the Index Server
	 *
	 * @throws RemoteException the remote exception
	 */
    private void showClientDetails() throws RemoteException {
    	
    	// Show client details registered with the Index Server
    	logger.info("==========================");
    	logger.info("Registered Client details:");

    	// Looping over the peer clients
    	int i = 0;
    	while(i < peers.size()) {
    		int j = 0;
    		logger.info("Peer Client Name: " + peers.get(i).getClientName());
            logger.info("Peer Client Directory: " + peers.get(i).getClientDirectory());
            logger.info("Available files on the peer client:");
            
            // Files associated with the client
            String[] files = peers.get(i).getClientFiles();
            while(j < files.length) {
                logger.info("=>" + files[j++]);
            }
            logger.info("==========================");
            i++;
        }
    }

	/**
	 * This method provides the implementation to search for the file on the peer clients
	 *
	 * @param fileName the file name
	 * @param requestingPeerClient the requesting peer client
	 * @return the i client[]
	 * @throws RemoteException the remote exception
	 */
    public synchronized IClient[] searchFileOnPeers(String fileName, String requestingPeerClient) throws RemoteException {
        
    	// Searching file on peer clients
    	logger.info("Peer client '" + requestingPeerClient + "' requesting to download file '" + fileName + "'");
        
    	int i = 0;
    	int count = 0;
    	boolean fileFound = false;
        IClient[] peerClients = new IClient[peers.size()];
        
        logger.info("Searching the requested file on Index Server");
        // Looping the peers
        while(i < peers.size()) {
        	// Getting files available at a peer client
            String[] clientFileList = peers.get(i).getClientFiles();
            
            // Looping over the files to check for the requested file
            int j = 0;
            while(j < clientFileList.length) {
            	// Matching file
                if (fileName.equals(clientFileList[j])) {
                    peerClients[count++] = peers.get(i);
                    fileFound = true;
                }
                j++;
            }
            i++;
        }
        
        if (fileFound) {
            logger.info("Requesting file '" + fileName + "' for peer client '" + requestingPeerClient + "'");
            return peerClients;
        } else {
            logger.info("Requested file: '" + fileName + "' is not present on any peer");
        }
        return null;
    }
}
