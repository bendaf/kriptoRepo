package hu.kripto.hf.database;

import hu.kripto.hf.functions.Coder;

public class Record {
	private String url;
	private String usernameHash;
	private String passwordHash;
	private String recordSalt;
	
	public Record(String url, String usernameHash, String passwordHash) {
		super();
		this.url = url;
		this.usernameHash = usernameHash;
		this.passwordHash = passwordHash;
	}
	public Record(String url, String username, String password, String salt) {
		this(url,username,password);
		this.recordSalt = salt;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsernameHash() {
		return usernameHash;
	}
	public void setUsernameHash(String usernameHash) {
		this.usernameHash = usernameHash;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public String getRecordSalt() {
		return recordSalt;
	}
	public void setRecordSalt(String bs) {
		this.recordSalt = bs;
	}
	public void cipher(byte[] usernameKey, byte[] passwordKey) {
//		System.out.println(recordSalt);
		passwordHash = Coder.base64Encode(passwordHash);
		usernameHash = Coder.base64Encode(usernameHash);
//		passwordHash = Coder.base64Encode(new String(
//					Coder.encode(passwordHash, passwordKey, recordSalt.getBytes())));
//		usernameHash = Coder.base64Encode(new String( 
//					Coder.encode(usernameHash,passwordKey,recordSalt.getBytes())));
		recordSalt = Coder.base64Encode(recordSalt);
		url = Coder.base64Encode(url);
		
	}
	public void deCipher(byte[] usernameKey, byte[] passwordKey) {
		recordSalt = Coder.base64Decode(recordSalt);
		passwordHash = Coder.base64Decode(passwordHash);
		usernameHash = Coder.base64Decode(usernameHash);
//		passwordHash = Coder.decode(
//				Coder.base64Decode(passwordHash).getBytes(),passwordKey,recordSalt.getBytes());
//		usernameHash = Coder.decode(
//				Coder.base64Decode(usernameHash).getBytes(),usernameKey,recordSalt.getBytes());
		url = Coder.base64Decode(url);
	}
	
}
