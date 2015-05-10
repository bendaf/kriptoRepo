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
		System.arraycopy(messageBytes, 0, c, iv.length, messageBytes.length);
		
		try {
			System.out.println(iv.length);
			output.writeInt(c.length);
			output.write(c);
			output.flush();
			System.out.println(c.length + " hosszú üzenet elküldve");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static String getXml(DataInputStream input, byte[] key) {
		try {
			int a;
			System.out.println(a = input.readInt());
			byte[] b = new byte[a-16];
			byte[] iv = new byte[16];
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
