package hu.kripto.hf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlHelper {
	public static String createRecordXml(Record r) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Record getRecordFromXml(String recordXml){
		// TODO
		return null;
	}
	
	public static ArrayList<Record> getRecordsFromXml(String recordsXml) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String createUserXml(User currentUser) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static User getAuthFromXml(String userXml){
		// TODO 
		return null;
	}

	public static String createAuthXml(String username, String verifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public static ArrayList<User> getUsersFromFlie(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void addUserToFile(User idUser, String usersXml) {
		// TODO Auto-generated method stub
		
	}

	public static void addRecordToFile(User user, Record recordFromXml) {
		// TODO Auto-generated method stub
		
	}
	
	public static Document obtenerDocumentDeByte(byte[] documentoXml) throws ParserConfigurationException, SAXException, IOException  {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    return builder.parse(new ByteArrayInputStream(documentoXml));
	}
}
