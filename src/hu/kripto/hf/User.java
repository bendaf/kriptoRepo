package hu.kripto.hf;

import java.util.ArrayList;
import java.util.Collection;

public class User {
	private String name;
	private ArrayList<Record> records = new ArrayList<>();
	private String verifier;
	
	public User(String name, String verifier) {
		super();
		this.name = name;
		this.verifier = verifier;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVerifier() {
		return verifier;
	}
	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}
	public void addRecord(Record r){
		records.add(r);
	}
	public void addRecords(Collection<Record> r){
		records.addAll(r);
	}
	public ArrayList<Record> getRecords(){
		return records;
	}
}
