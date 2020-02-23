package util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.log4j.Logger;

import client.Client;
import client.IClient;

/**
 * DirectoryWatcher monitors the directory of the server and triggers a
 * notification if there is any change in the directory contents
 * 
 * Reference:
 * https://docs.oracle.com/javase/tutorial/essential/io/notification.html
 * 
 * @author Akshith
 * @author Shantanoo
 * 
 */
public class DirectoryWatcher implements Runnable {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(DirectoryWatcher.class);

	/** The client. */
	private IClient client;

	/**
	 * Instantiates a new directory watcher.
	 *
	 * @param peer the peer
	 */
	public DirectoryWatcher(Client peer) {
		this.client = peer;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			Path dir = Paths.get(client.getClientDirectory());
			WatchService watchService = FileSystems.getDefault().newWatchService();
			WatchKey WatcherKey = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			for (;;) {
				try {
					WatcherKey = watchService.take();
				} catch (InterruptedException x) {
					return;
				}
				boolean doUpdateForNewFileFlag = false;
				for (WatchEvent<?> eventValue : WatcherKey.pollEvents()) {
					WatchEvent.Kind<?> kind_event = eventValue.kind();
					if (kind_event == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}
					
					// Update the Index Server on file\directory delete & modify events
					if (kind_event == StandardWatchEventKinds.ENTRY_DELETE
							|| kind_event == StandardWatchEventKinds.ENTRY_MODIFY) {
						client.updateServer();
					}
					
					// Update the Index Server on file\directory create event
					if (kind_event == StandardWatchEventKinds.ENTRY_CREATE) {
						if (doUpdateForNewFileFlag)
							client.updateServer();
						else
							doUpdateForNewFileFlag = true;
					}
				}
				boolean validvalue = WatcherKey.reset();
				if (!validvalue) {
					break;
				}
			}
		} catch (IOException x) {
			logger.error(x);
		}
	}
}