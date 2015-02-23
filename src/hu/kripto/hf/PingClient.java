package hu.kripto.hf;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException; // Ha a kommunikacioban valami balul sul el
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException; // Ha rossz cimre probalunk csatlakozni

public class PingClient implements Runnable {
	public PingClient() throws UnknownHostException, IOException {
		clientSocket = new Socket(InetAddress.getLocalHost(), Server.PORT_NUMBER);
	}

	public void close() throws IOException {
		clientSocket.close();
	}
	
	public void run() {
		try {
			DataInputStream serverInput = new DataInputStream(clientSocket.getInputStream());
			PrintWriter serverOutput = new PrintWriter(clientSocket.getOutputStream());

			for (int i = 0; i < 1; ++i) {
				long startTime = System.currentTimeMillis();
				serverOutput.print("PING\r\n");
				serverOutput.flush(); // Buffer uritese
//				System.out.println(String.valueOf(serverInput.readInt()));
				byte[] b = new byte[serverInput.readInt()];
				serverInput.read(b);
				System.out.println(b);
				
//				if (serverInput.readLine().equals("PONG")) {
//					long endTime = System.currentTimeMillis();
//					System.out.println("Az uzenet visszaert " + new Long(endTime - startTime).toString() + " ms alatt");
//					try {
//						Thread.sleep(400);
//					} catch (InterruptedException e) {
//						System.out.println("Interrupted while sleeping, shutting down...");
//						clientSocket.close(); // Minden lehetseges ponton le kell zarni a socket-okat
//						return;
//					}
//				}
			}

			serverOutput.print("QUIT\r\n");
			serverOutput.flush();

			// Itt se felejtsetek lezarni a kapcsolatot!
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			new PingClient().run();
		} catch (IOException e) { // Az UnknownHostException az IOException leszarmazottja. Mivel most egyikre se tudnank ertelmes hibakezelest csinalni, egyszerre lekezeljuk mindkettot.
			e.printStackTrace();
		}
	}
	
	// Csatlakozunk a sajat gepunkon futo szerverhez. A sajat gepunk hostneve localhost, ip cime 127.0.0.1.
	protected Socket clientSocket;
	
}
