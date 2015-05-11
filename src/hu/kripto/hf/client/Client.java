package hu.kripto.hf.client;


import hu.kripto.hf.UserAuthFaildException;
import hu.kripto.hf.database.CONSTANTS;
import hu.kripto.hf.database.Record;
import hu.kripto.hf.database.XmlHelper;
import hu.kripto.hf.functions.Coder;
import hu.kripto.hf.functions.DifHelm;
import hu.kripto.hf.functions.Network;
import hu.kripto.hf.server.Server;

import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Client extends Thread {
	protected Socket clientSocket;
	private BigInteger serverPK;
	private DifHelm dh;
	private DataOutputStream serverOutput;
	private DataInputStream serverInput;
	private byte[] masterKey;
	private String username;
	private PasswordSafeGUI psfgui;
	
	public Client() throws UnknownHostException, IOException {
		clientSocket = new Socket("192.168.255.226", Server.PORT_NUMBER);
		serverInput = new DataInputStream(clientSocket.getInputStream());
		serverOutput = new DataOutputStream(clientSocket.getOutputStream());
		keyExchange();
	}

	public Client(PasswordSafeGUI passwordSafeGUI) throws UnknownHostException, IOException {
		this();
		psfgui = passwordSafeGUI;
	}

	public void close() throws IOException {
		clientSocket.close();
	}
	
	public static void main(String[] args) {
		try {
			new Client();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void keyExchange() throws IOException{
	
		byte[] firstStep = firstStep();
		serverOutput.writeInt(firstStep.length);
		serverOutput.write(firstStep);
		serverOutput.flush();
		System.out.println("kiírok " + firstStep.length + " hosszú bájtot");
		
		byte[] b = new byte[serverInput.readInt()];
		serverInput.read(b);
		System.out.println("beolvasok " + b.length + " hoszú bájtot");
		
		try {
			Document d = XmlHelper.bytes2Doc(b);
			System.out.println(XmlHelper.doc2String(d));
			d.getDocumentElement().normalize();
			
			NodeList nList = d.getElementsByTagName("dh");
			Node nNode = nList.item(0);
			NodeList ghChildren = nNode.getChildNodes();
			int step = -1;
			for (int temp = 0; temp < ghChildren.getLength(); temp++) {
				 
				Node node = ghChildren.item(temp);
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					node.getChildNodes();
					if(node.getNodeName().equals("step")){
						step = Integer.parseInt(node.getTextContent());
					}else if (node.getNodeName().equals("myresult")) {
						serverPK = new BigInteger(node.getTextContent(),16);
					}
				}
			}
			if(step == 2){
				try {
					byte[] thirdStep = thirdStep(dh.createInterKey().toString(16));
					
					serverOutput.writeInt(thirdStep.length);
					serverOutput.write(thirdStep);
					serverOutput.flush();
					System.out.println("kiírok " + thirdStep.length + " hosszú bájtot");
					dh.createEncryptionKey(serverPK);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	public void sendAuth(String username, String password) throws UserAuthFaildException{
		masterKey = sha1(password);
		this.username = username;
		byte[] verifier = sha1(masterKey);
		try {
			sendUserData(username,new String(verifier,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try{
		getRecords();
		}catch(NullPointerException e){
			throw new UserAuthFaildException();
		}
	}

	private void getRecords() {
		String recordsXml = Network.getXml(serverInput, dh.getValue(DifHelm.DH_KEY).toByteArray());
		ArrayList<Record> records =  XmlHelper.getRecordsFromXml(recordsXml);
		for(Record r : records){
			byte[] recordKey = pbkdf2(masterKey, r.getRecordSalt().getBytes(Charset.forName("UTF-8")));
			byte[] usernameKey = pbkdf2(recordKey, new String("USER__ID").getBytes(Charset.forName("UTF-8")));
			byte[] passwordKey = pbkdf2(recordKey, new String("PASSWORD").getBytes(Charset.forName("UTF-8")));
			r.deCipher(usernameKey,passwordKey);
//			User u = new User("AAA","d");
//			u.addRecord(r);
//			System.out.println();
//			XmlHelper.createUserXml(u);
//			System.out.println();
		}
		final ArrayList<Record> rs = records;
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				for(Record r : rs){
					psfgui.listmodel.addElement(r.getUrl() + "   " + r.getUsernameHash() + "   " + r.getPasswordHash());
				}
				
			}
		});
		
	}
	
	public void addRecord(Record r) {
		try {
			r.setRecordSalt(new String(Coder.generateIV(),"UTF-8"));
			byte[] recordKey = pbkdf2(masterKey, r.getRecordSalt().getBytes(Charset.forName("UTF-8")));
			byte[] usernameKey = pbkdf2(recordKey, new String("USER__ID").getBytes(Charset.forName("UTF-8")));
			byte[] passwordKey = pbkdf2(recordKey, new String("PASSWORD").getBytes(Charset.forName("UTF-8")));
			r.cipher(usernameKey,passwordKey);
			sendRecrodData(r);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void sendRecrodData(Record r) {
		Network.send(serverOutput,XmlHelper.createRecordXml(Coder.base64Encode(username),r),
					dh.getValue(DifHelm.DH_KEY).toByteArray());
		
	}

	private byte[] pbkdf2(byte[] masterKey2, byte[] salt) {
//		System.out.println(bytes2Char(masterKey2) + " AAA " + bytes2Char(salt));
		try {
			SecretKeyFactory kf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec ks = new PBEKeySpec(bytes2Char(masterKey2),salt,42,20);
			return kf.generateSecret(ks).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private char[] bytes2Char(byte[] byteArray) {
		char[] charArray = new char[byteArray.length];
		for (int i = 0; i < byteArray.length; i++) {
			charArray[i] = (char)(byteArray[i] & 0xFF);
		}
		return charArray;
	}

	private byte[] sha1(byte[] byteArray) {
		MessageDigest md = null;
	    try {
	        md = MessageDigest.getInstance("SHA-1");
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } 
	    return md.digest(byteArray);
	}

	private byte[] sha1(String masterKey) { // 160 bites kimenet!
		return sha1(Arrays.copyOf(masterKey.getBytes(Charset.forName("UTF-8")), 20));
	}

	private void sendUserData(String username, String verifier) {
		Network.send(serverOutput,XmlHelper.createAuthXml(Coder.base64Encode(username),
							Coder.base64Encode(verifier)),dh.getValue(DifHelm.DH_KEY).toByteArray());
	}
	
	// Csatlakozunk a sajat gepunkon futo szerverhez. A sajat gepunk hostneve localhost, ip cime 127.0.0.1.
	private byte[] firstStep(){
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("dh");
			doc.appendChild(rootElement);
			
			Element record = doc.createElement("step");
			record.setTextContent("1");
			rootElement.appendChild(record);
			
			Element modulus = doc.createElement("modulus");
			modulus.setTextContent(chooseModulus().toString());
			rootElement.appendChild(modulus);
			
			// write the content into xml file
			return XmlHelper.doc2Bytes(doc);
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	
		byte[] fileData = null;		
		return fileData;
	}

	private byte[] thirdStep(String hexString) {
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("dh");
			doc.appendChild(rootElement);
			
			Element record = doc.createElement("step");
			record.setTextContent("3");
			rootElement.appendChild(record);
			
			Element modulus = doc.createElement("myresult");
			modulus.setTextContent(hexString);
			rootElement.appendChild(modulus);
			
			return XmlHelper.doc2Bytes(doc);
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Integer chooseModulus() {
		Random r = new Random();
		Integer modulusSize = CONSTANTS.MOD_NUMS[r.nextInt(CONSTANTS.MOD_NUMS.length)];
		dh = new DifHelm(new BigInteger("2"),modulusSize); 
		return modulusSize;
	}
}
