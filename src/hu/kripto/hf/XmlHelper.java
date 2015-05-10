package hu.kripto.hf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlHelper {
	
	private final static String USERS = "users";
	private final static String USER = "user";
	private final static String NAME = "name";
	private final static String VERIFIER = "verifier";
	private final static String RECORD = "record";
	private final static String URL = "url";
	private final static String USERNAME = "username";
	private final static String PASSWORD = "password";
	private final static String SALT = "recolrdsalt";
	
	public static String createAuthXml(String username, String verifier) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(USERS);
			doc.appendChild(rootElement);
			Element userElement = doc.createElement(USER);
			rootElement.appendChild(userElement);
			userElement.setAttribute(NAME, username);
			userElement.setAttribute(VERIFIER, verifier);
			
			
			try {
				return print(doc2String(doc));
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static User getAuthFromXml(String userXml){
		System.out.println(userXml);
		try {
			Document d = string2Doc(userXml);
			d.getDocumentElement().normalize();
			NodeList nList = d.getElementsByTagName(USER);// TODO Nem kell ide egy users? 
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				 
				Node nNode = nList.item(temp);
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element eElement = (Element) nNode;
					String username = eElement.getAttribute(NAME);
					String verifier = eElement.getAttribute(VERIFIER);
					return new User(username, verifier);
				}
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addUserToFile(User idUser, String fileName) {
		ArrayList<User> users = getUsersFromFlie(fileName);
		users.add(idUser);
		createUsersFile(users,fileName);
	}

	private static void createUsersFile(ArrayList<User> users,String fileName) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(USERS);
			doc.appendChild(rootElement);
			for(User myUser : users){
				Element userElement = doc.createElement(USER);
				rootElement.appendChild(rootElement);
				userElement.setAttribute(NAME, myUser.getName());
				userElement.setAttribute(VERIFIER, myUser.getVerifier());
				
				for( Record r : myUser.getRecords()){
					Element record = doc.createElement(RECORD);
					rootElement.appendChild(record);
		 
					// set attribute to staff element
					record.setAttribute(URL, r.getUrl());
					record.setAttribute(USERNAME, r.getUsernameHash());
					record.setAttribute(PASSWORD, r.getPasswordHash());
					record.setAttribute(SALT, r.getRecordSalt());
				}
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileName));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}

		
	}

	public static ArrayList<User> getUsersFromFlie(String fileName) { // Recordokkal együtt
		ArrayList<User> users = new ArrayList<User>();
		
		try {
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName(USER);
		 
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node userNode = nList.item(temp);
				if (userNode.getNodeType() == Node.ELEMENT_NODE) {
					User myUser;
					Element eElement = (Element) userNode;
					String username = eElement.getAttribute(NAME);
					String verifier = eElement.getAttribute(VERIFIER);
					myUser = new User(username, verifier);
					for(int i = 0; i< userNode.getChildNodes().getLength();i++){
						Node recordNode = userNode.getChildNodes().item(i);
						if(recordNode.getNodeType() == Node.ELEMENT_NODE){
							Element record = (Element) recordNode;
							myUser.addRecord(new Record(record.getAttribute(URL),
									record.getAttribute(USERNAME), record.getAttribute(PASSWORD),
									record.getAttribute(SALT)));
						}
					}
					users.add(myUser);
				}
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return users;
	}

	public static String createUserXml(User currentUser) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(USER);
			doc.appendChild(rootElement);
			rootElement.setAttribute(NAME, currentUser.getName());
			
			for( Record r : currentUser.getRecords()){
				Element record = doc.createElement(RECORD);
				rootElement.appendChild(record);
	 
				// set attribute to staff element
				record.setAttribute(URL, r.getUrl());
				record.setAttribute(USERNAME, r.getUsernameHash());
				record.setAttribute(PASSWORD, r.getPasswordHash());
				record.setAttribute(SALT, r.getRecordSalt());
			}
			return print(doc2String(doc));
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String print(String string) {
		System.out.println(string);
		return string;
	}

	public static ArrayList<Record> getRecordsFromXml(String recordsXml) { //User xml
		System.out.println(recordsXml);
		ArrayList<Record> records = new ArrayList<>();
		
		Document doc;
		try {
			doc = string2Doc(recordsXml);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName(RECORD); // TODO ???
			for (int temp = 0; temp < nList.getLength(); temp++) {
		 
				Node nNode = nList.item(temp);
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element eElement = (Element) nNode;
					records.add(new Record(eElement.getAttribute(URL), eElement.getAttribute(USERNAME),
							eElement.getAttribute(PASSWORD),eElement.getAttribute(SALT)));
				}
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return records;
	}

	public static String createRecordXml(String username, Record r) {
		User u = new User(username,"id");
		return createUserXml(u);
	}

	public static Record getRecordFromXml(String recordXml){
		System.out.println("RecordXml: ");
		return getRecordsFromXml(recordXml).get(0);
	}

	public static void addRecordToFile(User user, Record record, String fileName) {
		ArrayList<User> users = getUsersFromFlie(fileName);
		for(User u : users){
			if(u.getName().equals(user.getName())){
				u.addRecord(record);
			}
		}
		createUsersFile(users, fileName);
		
	}
	
	public static Document bytes2Doc(byte[] documentoXml) throws ParserConfigurationException, SAXException, IOException  {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    return builder.parse(new ByteArrayInputStream(documentoXml));
	}
	
	public static byte[] doc2Bytes(Document d) throws TransformerException{
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		StreamResult result=new StreamResult(bos);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		DOMSource source = new DOMSource(d);
		transformer = transformerFactory.newTransformer();
		transformer.transform(source, result);
		return bos.toByteArray();
	}
	
	public static String doc2String(Document d) throws TransformerException{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(d), new StreamResult(writer));
		return writer.getBuffer().toString().replaceAll("\n|\r", "");
	}
	
	public static Document string2Doc(String xmlStr) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        builder = factory.newDocumentBuilder();  
        Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
        return doc;
    }
}
