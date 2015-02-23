package hu.kripto.hf;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
	public static final int PORT_NUMBER = 42424;

	public Server() throws IOException {
		serverSocket = new ServerSocket(PORT_NUMBER);
	}

	public void close() throws IOException {
		serverSocket.close();
	}

	public void run() {
		User myUser = new User("ASDF","valami.hu",
				"DKDKDKLDLK","DKJHVUBDU","adfkvjaovj");
		byte[] array = UsertoBytes.getBytes(myUser);
		System.out.println(Integer.toString(array.length));
		try {
			Socket clientSocket = serverSocket.accept(); 

			// A szukseges IO cuccok
			BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());

			String line = "";
			while (! (line = clientInput.readLine()).equals("QUIT")) {
				if (line.equals("PING")) {
//					System.out.println(array.length);

//					clientOutput.print("PONG\r\n");
					clientOutput.writeInt(array.length);
					clientOutput.write(array);
					clientOutput.flush();
			
//					clientOutput.flush();
				}
			}


			// CLEANUP
			// Sose felejtsetek el lezarni! Magatoknak okoztok vele fejfajast.
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace(); // Eleg sok helyen elszallhat a kapcsolat, erdemes a java doksit egyszer vegigolvasni, hogy mennyi problema lephet fel halozati kommunikacio soran.
		}

		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			new Server().run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected ServerSocket serverSocket;
}
