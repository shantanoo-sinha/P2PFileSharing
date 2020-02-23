package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import util.Constants;

/**
 * The class ServerDriver. Main class which binds the Index Server 
 * and registers to the naming registry.
 * 
 * @author Akshith
 * @author Shantanoo
 *
 */
public class ServerDriver {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(ServerDriver.class);
		
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws RemoteException the remote exception
	 * @throws MalformedURLException the malformed URL exception
	 */
	public static void main(String[] args) throws RemoteException, MalformedURLException {
		// Setting system property for rmi server hostname as localhost
		System.setProperty(Constants.JAVA_RMI_SERVER_HOSTNAME, Constants.LOCALHOST);
		
		// Initiate stub
		initStub(args);
	}

	/**
	 * Inits the stub. This binds the Index Server remote object
	 *
	 * @param args the args
	 * @throws RemoteException the remote exception
	 * @throws MalformedURLException the malformed URL exception
	 */
	private static void initStub(String[] args) throws RemoteException, MalformedURLException {
		// Instantiate the server object
		Server server = new Server();
		// Binding the Index Server
		String portNumber = args[0];
		Naming.rebind(Constants.RMI_LOCALHOST + portNumber + Constants.PEER_SERVER, server);
    	logger.info("======================================================================");
		logger.info("                   PEER-TO-PEER FILE SHARING SYSTEM                   ");
		logger.info("               ========================================               ");
		logger.info("                           SERVER STARTED                             ");
		logger.info("======================================================================");
	}
}
