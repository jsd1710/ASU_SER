package src;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import java.io.*;

class SockServer 
{
    static ConcurrentHashMap<Integer, Integer> total = new ConcurrentHashMap<Integer, Integer>();   
    
    public static void main (String args[]) throws Exception 
    {
        ServerSocket serv = null;
        Socket sock = null;
        
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
                Calculator calculator = new Calculator(sock, total);
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
    static ConcurrentHashMap<Integer, Integer> clients;

	InputStream in = null;
    OutputStream out = null;
    
    BufferedReader reader;
    PrintWriter pw;
    Socket sock = null;
    int clientID = 0;
    int addValue = 0;
    
    Calculator(Socket socket, ConcurrentHashMap<Integer, Integer> total)
    {
    	sock = socket;
    	clients = total;
    }
    
    public boolean close() throws IOException
    {
    	out.close();
    	in.close();
    	return true;
    }
    
    String[] parseInput(InputStream inputStream) throws IOException
    {
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String[] arguments = reader.readLine().split(" ");
        if (arguments.length == 2)
        {
        	clientID = Integer.parseInt(arguments[0]);
        	addValue = addInput(arguments[1]);
        }
        else if (arguments.length == 1)
        {
        	addValue = addInput(arguments[0]);
        }
        return arguments;
    }
    
    int addInput(String input)
    {
    	int result = -1;
    	try
    	{
        	result = Integer.parseInt(input);
    	}
    	catch (NumberFormatException e)
    	{
    		result = 0;
    		if (input.equals("reset"))
			{
    			clients.put(clientID, 0);
    			System.out.println("Total is reset.");
			}
    		else
    		{
    			System.out.println("'" + input + "' is not an accepted input.");
    		}
    	}
    	
    	return result;
    }
    
	public void run()
	{
		try {
			in = sock.getInputStream();
            out = sock.getOutputStream();
            
            String[] arguments = parseInput(in);
            
            System.out.println("Server received " + Arrays.toString(arguments));
            	
            if (clients.get(clientID) == null)
            {
            	clients.put(clientID, 0);
            }

            int newValue = clients.get(clientID) + addValue;
            clients.put(clientID, newValue);
        	System.out.println("Total is: " + clients);

            pw = new PrintWriter(out, true);
            pw.println(clients.get(clientID));
            out.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
