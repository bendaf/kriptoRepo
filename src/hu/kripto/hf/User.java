package hu.kripto.hf;

public class User {
	private String name;
	private String url;
	private String usernameHash;
	private String passwordHash;
	private String recordSalt;
	private String verifier;
	
	public User(String name, String verifier) {
		super();
		this.name = name;
		this.verifier = verifier;
	}
	public User(String name, String url, String usernameHash,
			String passwordHash, String recordSalt) {
		super();
		this.name = name;
		this.url = url;
		this.usernameHash = usernameHash;
		this.passwordHash = passwordHash;
		this.recordSalt = recordSalt;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public void setRecordSalt(String recordSalt) {
		this.recordSalt = recordSalt;
	}
	public String getVerifier() {
		return verifier;
	}
	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}
	
}
