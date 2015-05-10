package hu.kripto.hf.server;

import hu.kripto.hf.UserAuthFaildException;
import hu.kripto.hf.database.User;
import hu.kripto.hf.database.XmlHelper;
import hu.kripto.hf.functions.DifHelm;
import hu.kripto.hf.functions.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Server implements Runnable {
	public static final int PORT_NUMBER = 42424;
	private Socket clientSocket;
	private ServerSocket serverSocket;
	DataInputStream clientInput;
	DataOutputStream clientOutput;
	private DifHelm dh;
	private BigInteger clientPK;
	private String usersXml = "example1.xml";

	public Server() throws IOException {
		serverSocket = new ServerSocket(PORT_NUMBER);
	}

	public static void main(String[] args) {
		try {
			new Server().run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted()){
				clientSocket = serverSocket.accept(); 
				(new Thread(new Runnable(){
					
					@Override
					public void run() {
	
						try {
							clientInput = new DataInputStream(clientSocket.getInputStream());
							clientOutput = new DataOutputStream(clientSocket.getOutputStream());
							keyExchange();
							try{
								User currentUser = authUser();
								if(checkUser(currentUser)){
									System.out.println("User azonosítva" );
								}else {
									System.out.println("User hozzáadva");
									XmlHelper.addUserToFile(currentUser,usersXml);
								}
								sendRecords(currentUser);
								getRecords(currentUser);
							}catch(UserAuthFaildException e){
								e.printStackTrace();
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				})).start();
			}
			serverSocket.close();
		} catch (EOFException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}

	public void close() throws IOException {
		serverSocket.close();
	}

	private void keyExchange() throws IOException{
			
				byte[] b = new byte[clientInput.readInt()];
				clientInput.read(b);
				System.out.println("beolvasok " + b.length + " hoszú bájtot");
				
				int modulusSize=0;
				try {
					Document d = XmlHelper.bytes2Doc(b);
					d.getDocumentElement().normalize();
					
					NodeList nList = d.getElementsByTagName("dh");
					Node nNode = nList.item(0);
					NodeList ghChildren = nNode.getChildNodes();
					int step = -1;
					for (int temp = 0; temp < ghChildren.getLength(); temp++) {
						 
						Node node = ghChildren.item(temp);
				 
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							node.getChildNodes();
							Element eElement = (Element) node;
							if(eElement.getNodeName().equals("step")){
								step = Integer.parseInt(eElement.getTextContent());
							}else if (eElement.getNodeName().equals("modulus")) {
								modulusSize = Integer.parseInt(eElement.getTextContent());
							}
						}
					}
					if(step == 1){
						try {
							dh = new DifHelm(new BigInteger("2"), modulusSize);
							byte[] secondStep = secondStep(dh.createInterKey().toString(16));
							clientOutput.writeInt(secondStep.length);
							clientOutput.write(secondStep);
							clientOutput.flush();
							System.out.println("kiírok " + secondStep.length + " hosszú bájtot");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				} catch (ParserConfigurationException | SAXException e) {
					e.printStackTrace();
				}
	
				byte[] b2 = new byte[clientInput.readInt()];
				clientInput.read(b2);
				System.out.println("beolvasok " + b.length + " hoszú bájtot");
				try {
					Document d = XmlHelper.bytes2Doc(b2);
					d.getDocumentElement().normalize();
					
					NodeList nList = d.getElementsByTagName("dh");
					Node nNode = nList.item(0);
					NodeList ghChildren = nNode.getChildNodes();
					int step = -1;
					for (int temp = 0; temp < ghChildren.getLength(); temp++) {
						 
						Node node = ghChildren.item(temp);
				 
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							node.getChildNodes();
							Element eElement = (Element) node;
							if(eElement.getNodeName().equals("step")){
								step = Integer.parseInt(eElement.getTextContent());
							}else if (eElement.getNodeName().equals("myresult") || eElement.getNodeName().equals("myresult")) {
								clientPK = new BigInteger(eElement.getTextContent(),16);
							}
						}
					}
					if(step == 3){
						dh.createEncryptionKey(clientPK);
					}
					
				} catch (ParserConfigurationException | SAXException e) {
					e.printStackTrace();
				}
			
		}

	private User authUser() {
		
		String authXml = Network.getXml(clientInput,dh.getValue(DifHelm.DH_KEY).toByteArray());
		return XmlHelper.getAuthFromXml(authXml);
		
	}

	private void sendRecords(User currentUser) {
		Network.send(clientOutput,XmlHelper.createUserXml(currentUser),
					dh.getValue(DifHelm.DH_KEY).toByteArray());
	}

	private void getRecords(User currentUser) {
		boolean isAlive = true;
		while(isAlive){
//			System.out.println("VEGTELEN");
			String recordXml = Network.getXml(clientInput,dh.getValue(DifHelm.DH_KEY).toByteArray());
			if(recordXml == null)isAlive = false;
			else XmlHelper.addRecordToFile(currentUser,XmlHelper.getRecordFromXml(recordXml),usersXml);
		}
		
	}

	/**
	 * @param currentUser
	 * @return
	 * @throws UserAuthFaildException
	 */
	private boolean checkUser(User currentUser) throws UserAuthFaildException{
		ArrayList<User> users = XmlHelper.getUsersFromFlie(usersXml);
		for(User u : users){
			if(u.getName().equals(currentUser.getName())){
				if(u.getVerifier().equals(currentUser.getVerifier())){
					currentUser.addRecords(u.getRecords());
					return true;
				}else{
					throw new UserAuthFaildException();
				}
			}
		}
		return false;
	}

	private byte[] secondStep(String hexString) {

		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("dh");
			doc.appendChild(rootElement);
			
			Element record = doc.createElement("step");
			record.setTextContent("2");
			rootElement.appendChild(record);
			
			Element modulus = doc.createElement("myresult");
			modulus.setTextContent(hexString);
			rootElement.appendChild(modulus);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("outexample.xml"));
	
	
			transformer.transform(source, result);
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
		try {
			File file = new File("outexample.xml");
			fileData = new byte[(int) file.length()];
			FileInputStream in = new FileInputStream(file);
			in.read(fileData);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileData;
	}
}
