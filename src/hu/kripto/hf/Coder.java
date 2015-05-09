package hu.kripto.hf;

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
		// TODO Auto-generated method stub
		return null;
	}

}
