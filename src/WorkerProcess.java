import java.net.Socket;


public class WorkerProcess
implements Runnable {
	private ClientConnector fc;
	
	public WorkerProcess(Socket clientSocket, EncryptionManager em, PacketHandler handler, UserTracker tracker) {
		this.fc = new ClientConnector(clientSocket, em, handler);
	}
	
	@Override
	public void run() {
		// Start the connector
		fc.run();
	}
	
}
