package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import client.server.ClientServer;
import server.IServer;
import util.Constants;

/**
 * ClientDriver provides the multi-threaded implementation of the peer clients and peer client server
 * 
 * @author Akshith
 * @author Shantanoo
 *
 */
public class ClientDriver {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(ClientDriver.class);
	
	/** The server port. */
	private static String serverPort = null;
	
	/** The peer client port. */
	private static String peerClientPort = null;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws MalformedURLException the malformed URL exception
	 * @throws RemoteException the remote exception
	 * @throws NotBoundException the not bound exception
	 */
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		init(args);
	}

	/**
	 * Inits the peer client & peer client server.
	 *
	 * @param args the args
	 * @throws NotBoundException the not bound exception
	 * @throws MalformedURLException the malformed URL exception
	 * @throws RemoteException the remote exception
	 */
	private static void init(String[] args) throws NotBoundException, MalformedURLException, RemoteException {
		
		// Initiates the peer client stub lookup
		// Parameters are passed from the client init file
		String indexServerUrl = null;
		IServer indexServer = null;
		if (args.length == 3) {
			// Passing server port and client port from the init script
			serverPort = args[0];
			peerClientPort = args[1];

			// client stub
			indexServerUrl = Constants.RMI + Constants.LOCALHOST + ":" + serverPort + Constants.PEER_SERVER;
			indexServer = (IServer) Naming.lookup(indexServerUrl);
			
			//Instantiating the client object
			Client clientServer = new Client(args[2], args[1], indexServer);
			
			// Starting the peer client and peer client server
			new Thread(new PeerClientServer()).start();
			new Thread(clientServer).start();
		} else {
			// Display the correct usage
			logger.error("USAGE: ClientDriver <Server port number> <Client port number> <Client name>");
			logger.error("EXAMPLE: ClientDriver 2000 2001 Client1");
		}
	}

	/**
	 * The Class PeerClientServer for starting the Peer Client server.
	 */
	static class PeerClientServer implements Runnable {
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			initStub();
		}

		/**
		 * Inits the stub.
		 */
		private void initStub() {
			try {
				// Binding the remote object
				String clientURL = Constants.RMI + Constants.LOCALHOST + ":" + peerClientPort + Constants.CLIENT_SERVER;
				client.server.IClientServer clientServer = new ClientServer();
				Naming.rebind(clientURL, clientServer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
