package hu.kripto.hf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Network {

	public static void send(DataOutputStream output, String message, byte[] key) {
		byte[] iv = Coder.generateIV();
		byte[] messageBytes = Coder.encode(message,key,iv);
		byte[] c = new byte[iv.length + messageBytes.length];
		System.arraycopy(iv, 0, c, 0, iv.length);
		System.arraycopy(message, 0, c, iv.length, messageBytes.length);
		
		try {
			output.writeInt(c.length);
			output.write(c);
			System.out.println(c.length + " hosszú üzenet elküldve");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static String getXml(DataInputStream input, byte[] key) {
		try {
			byte[] b = new byte[input.readInt()-128];
			byte[] iv = new byte[128];
			input.read(iv);
			input.read(b);
			System.out.println((iv.length+b.length) + " hosszú üzenet fogadva");
			
			return Coder.decode(b, key, iv);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
