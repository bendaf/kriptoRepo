package hu.kripto.hf.functions;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Coder {

	public static byte[] encode(String plaintext, byte[] key, byte[] iv) {
		
		try {
			
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
			return cipher.doFinal(plaintext.getBytes("UTF-8"));
			
		} catch (UnsupportedEncodingException|NoSuchAlgorithmException|NoSuchProviderException|
				NoSuchPaddingException|InvalidKeyException|InvalidAlgorithmParameterException|
				IllegalBlockSizeException|BadPaddingException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

	public static String decode(byte[] encoded, byte[] key, byte[] iv) {
		
		try {
			
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			Cipher decipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
			decipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
			
			return new String(decipher.doFinal(encoded),"UTF-8");
			
		} catch (UnsupportedEncodingException|NoSuchAlgorithmException|NoSuchProviderException|
				NoSuchPaddingException|InvalidKeyException|InvalidAlgorithmParameterException|
				IllegalBlockSizeException|BadPaddingException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

	public static byte[] generateIV() {
		byte[] iv = new byte[16];
		Random rand = new Random();
		rand.nextBytes(iv);
		return iv;
	}

	public static String base64Encode(String token) {
		byte[] encodedBytes = Base64.encodeBase64(token.getBytes());
		return  new String(encodedBytes);
	}
	
	public static String base64Encode(byte[] token) {
		byte[] encodedBytes = Base64.encodeBase64(token);
		return  new String(encodedBytes);
	}
	
	public static String base64Decode(String token) {
		byte[] decodedBytes = Base64.decodeBase64(token.getBytes());
		return new String(decodedBytes);
	}
}
