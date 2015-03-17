package hu.kripto.hf;



import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException; // Ha a kommunikacioban valami balul sul el
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException; // Ha rossz cimre probalunk csatlakozni
import java.util.Random;

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

public class Client implements Runnable {
	protected Socket clientSocket;
	private Integer myModulus;
	private long myResult;
	private DH dh;
	
	public Client() throws UnknownHostException, IOException {
		clientSocket = new Socket(InetAddress.getLocalHost(), Server.PORT_NUMBER);
	}

	public void close() throws IOException {
		clientSocket.close();
	}
	
	public void run() {

		try {
			
			DataInputStream serverInput = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
			
			
			byte[] firstStep = firstStep();
			serverOutput.writeInt(firstStep.length);
			serverOutput.write(firstStep);
			serverOutput.flush();
			
			byte[] b = new byte[serverInput.readInt()];
			serverInput.read(b);
			
			try {
				Document d = obtenerDocumentDeByte(b);
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
						}else if (eElement.getNodeName().equals("myresult")) {
							myResult = Long.parseLong(eElement.getTextContent());
//							System.out.println(Long.toString(myResult));
						}
					}
				}
				if(step == 2){
					try {
						dh = new DH(2, myModulus);
						byte[] thirdStep = thirdStep(Long.toHexString(dh.createInterKey()));
						
						serverOutput.writeInt(thirdStep.length);
						serverOutput.write(thirdStep);
						serverOutput.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (ParserConfigurationException | SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println(firstStep.length);
			
			// Itt se felejtsetek lezarni a kapcsolatot!
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public static void main(String[] args) {
		try {
			new Client().run();
		} catch (IOException e) { // Az UnknownHostException az IOException leszarmazottja. Mivel most egyikre se tudnank ertelmes hibakezelest csinalni, egyszerre lekezeljuk mindkettot.
			e.printStackTrace();
		}
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
			modulus.setTextContent(Integer.toString(chooseModulus()));
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

	private Document obtenerDocumentDeByte(byte[] documentoXml) throws ParserConfigurationException, SAXException, IOException  {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    return builder.parse(new ByteArrayInputStream(documentoXml));
	}
	
	private int chooseModulus() {
		Random r = new Random();
		myModulus = CONSTANTS.MOD_NUMS[r.nextInt(CONSTANTS.MOD_NUMS.length)]; 
		
		return myModulus;
	}
}
