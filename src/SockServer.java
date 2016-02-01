package src;
import java.net.*;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;

class SockServer 
{
    static ConcurrentHashMap<Integer, Integer> clients = new ConcurrentHashMap<Integer, Integer>();   
    static int sleepTime = 0;
    
    public static void main (String args[]) throws Exception 
    {
        ServerSocket serv = null;
        Socket sock = null;
        
        if (args[0] != null)
        {
        	sleepTime = Integer.parseInt(args[0]);
        }
        
        try 
        {
            serv = new ServerSocket(8888);
        } 
        catch(Exception e) 
        {
        	e.printStackTrace();
        }
        
        while (serv.isBound() && !serv.isClosed()) 
        {
            System.out.println("\nReady...");
            try 
            {
                sock = serv.accept();
                String fileName = "";
                if (args.length == 2)
                {
                	System.out.println(args[1]);
                	fileName = args[1];
                }
                Calculator calculator = new Calculator(sock, clients, sleepTime, fileName);
                
                calculator.run();
                calculator.close();
                calculator = null;
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            } 
            finally 
            {
                if (sock != null) sock.close();
            }
        }
    }
    
}

class Calculator implements Runnable
{
    ConcurrentHashMap<Integer, Integer> clients;

	InputStream in = null;
    OutputStream out = null;
    
    BufferedReader reader;
    PrintWriter pw;
    Socket sock = null;
    int clientID = 0;
    int addValue = 0;
    int sleepTime = 0;
	String fileName = "";
    
    Calculator(Socket socket, ConcurrentHashMap<Integer, Integer> clients, int sleepTime, String fileName)
    {
    	this.sock = socket;
    	this.clients = clients;
    	this.sleepTime = sleepTime;
    	this.fileName = fileName;
    }
    
    public boolean close() throws IOException
    {
    	out.close();
    	in.close();
    	return true;
    }
    
    String[] parseInput(InputStream inputStream) throws Exception
    {
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String[] arguments = reader.readLine().split(" ");
        if (arguments.length == 2)
        {
        	clientID = Integer.parseInt(arguments[0]);
        	addInput(arguments[1]);
        }
        else if (arguments.length == 1)
        {
        	addInput(arguments[0]);
        }

        return arguments;
    }
    
    synchronized boolean addInput(String input) throws Exception
    {
    	int addValue = 0;
    	boolean result = false;
    	
    	if (clients.get(clientID) == null)
        {
        	clients.put(clientID, 0);
        }

    	try
    	{
    		addValue = Integer.parseInt(input);
        	int newValue = clients.get(clientID) + addValue;
            clients.put(clientID, newValue);
            result = true;
    	}
    	catch (NumberFormatException e)
    	{
    		addValue = 0;
    		if (input.equals("reset"))
			{
    			clients.put(clientID, 0);
    			System.out.println("Total is reset.");
    			result = true;
			}
    		else
    		{
    			System.out.println("'" + input + "' is not an accepted input.");
    		}
    	}
    	
    	writeToXML();
    	return result;
    }
    
    synchronized boolean readFromXML(String fileName) throws ParserConfigurationException, SAXException, IOException, TransformerException
    {
    	boolean result = false;
    	
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    	Document doc = docBuilder.parse(fileName);
    	
    	Node data = doc.getFirstChild();
    	NodeList nodes = data.getChildNodes();
    	for (int i = 0; i < nodes.getLength(); i++)
    	{
    		Node client = nodes.item(i);
    		int clientID = Integer.parseInt(client.getAttributes().getNamedItem("ClientID").getNodeValue());
    		int clientValue = Integer.parseInt(client.getAttributes().getNamedItem("ClientValue").getNodeValue());
    		clients.put(clientID, clientValue);
    	}
    	
    	return result;
    }
    
    synchronized boolean writeToXML() throws SAXException, IOException, ParserConfigurationException, TransformerException
    {
    	boolean result = false;
    	
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    	Document doc = docBuilder.parse("clients.xml");
    	
    	Node data = doc.getFirstChild();

    	doc.removeChild(data);
    	data = doc.createElement("clients");
    	doc.appendChild(data);
    	
    	for (Entry<Integer, Integer> client : clients.entrySet())
    	{
    		org.w3c.dom.Element root = doc.createElement("client");
    		root.setAttribute("ClientID", String.valueOf(client.getKey()));
    		root.setAttribute("ClientValue", String.valueOf(client.getValue()));
    		data.appendChild(root);
    	}
    	
    	TransformerFactory transformerFactory = TransformerFactory.newInstance();
    	Transformer transformer = transformerFactory.newTransformer();
    	DOMSource source = new DOMSource(doc);
    	StreamResult sResult = new StreamResult(new File("clients.xml"));
    	transformer.transform(source, sResult);
    	
    	return result;
    }
    
	public void run()
	{
		try {
			in = sock.getInputStream();
            out = sock.getOutputStream();
            
            if (fileName != "")
            {
            	readFromXML(fileName);
            }
            
            String[] arguments = parseInput(in);
            
            System.out.println("Server received " + Arrays.toString(arguments));
            
            if (sleepTime > 0)
            {
            	System.out.println("Sleep for " + sleepTime + " milliseconds...");
            	Thread.sleep(sleepTime);
            }
            
        	System.out.println("Total is: " + clients);

            pw = new PrintWriter(out, true);
            pw.println(clients.get(clientID));
            out.flush();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
