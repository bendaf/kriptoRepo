package hu.kripto.hf;

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
	public void cifher(byte[] usernameKey, byte[] passwordKey) {
		// TODO Auto-generated method stub
		
	}
	
}
