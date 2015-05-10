package hu.kripto.hf;

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
	private String usersXml = "aaa.xml";

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
		User myUser = new User("ASDF","adfkvjaovj");
		myUser.addRecord(new Record("valami.hu","DKDKDKLDLK","DKJHVUBDU"));
		myUser.addRecord(new Record("a", "sdadf", "agffd"));
//		byte[] array = UsertoBytes.getBytes(myUser);
//		System.out.println(Integer.toString(array.length));
		try {
			clientSocket = serverSocket.accept(); 

			clientInput = new DataInputStream(clientSocket.getInputStream());
			clientOutput = new DataOutputStream(clientSocket.getOutputStream());

			keyExchange();
			
			try{
				User currentUser = authUser();
				if(checkUser(currentUser)){
					sendRecords(currentUser);
				}else {
					XmlHelper.addUserToFile(currentUser,usersXml);
				}
				getRecords(currentUser);
			}catch(UserAuthFaildException e){
				e.printStackTrace();
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
					Document d = XmlHelper.obtenerDocumentDeByte(b);
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
	//							System.out.println(Integer.toString(modulusSize));
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
					Document d = XmlHelper.obtenerDocumentDeByte(b2);
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
	//							System.out.println(Integer.toString(step));
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
				clientSocket.close();
			
			
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
		while(!Thread.currentThread().isInterrupted()){
			String recordXml = Network.getXml(clientInput,dh.getValue(DifHelm.DH_KEY).toByteArray());
			XmlHelper.addRecordToFile(currentUser,XmlHelper.getRecordFromXml(recordXml));
		}
		
	}

	/**
	 * @param idUser
	 * @return
	 * @throws UserAuthFaildException
	 */
	private boolean checkUser(User idUser) throws UserAuthFaildException{
		ArrayList<User> users = XmlHelper.getUsersFromFlie(usersXml);
		for(User u : users){
			if(u.getName().equals(idUser.getName())){
				if(u.getVerifier().equals(idUser.getVerifier())){
					idUser = u;
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
	
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	
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
