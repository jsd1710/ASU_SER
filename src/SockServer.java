package src;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.io.*;

class SockServer 
{
    static AtomicIntegerArray total = new AtomicIntegerArray(10);   
    static BufferedReader reader;
    static PrintWriter pw;
    
    static int clientID = 0;
    static int addValue = 0;
    
    static String[] parseInput(InputStream inputStream) throws IOException
    {
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String[] arguments = reader.readLine().split(" ");
        System.out.println(Arrays.toString(arguments));
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
    
    static int addInput(String input)
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
    			total.set(clientID, 0);
    			System.out.println("Total is reset.");
			}
    		else
    		{
    			System.out.println("'" + input + "' is not an accepted input.");
    		}
    	}
    	
    	return result;
    }
    
    public static void main (String args[]) throws Exception 
    {
        ServerSocket    serv = null;
        InputStream in = null;
        OutputStream out = null;
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
            System.out.println("Ready...");
            try 
            {
                sock = serv.accept();
                in = sock.getInputStream();
                out = sock.getOutputStream();
                String[] arguments = parseInput(in);
                
                System.out.println("Server received " + Arrays.toString(arguments));
                	
                //Thread.sleep(1000);
                total.addAndGet(clientID, addValue);
            	System.out.println("Total is: " + total);

                pw = new PrintWriter(out, true);
                pw.println(total.get(clientID));
                out.flush();
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            } 
            finally 
            {
                if (out != null)  out.close();
                if (in != null)   in.close();
                if (sock != null) sock.close();
            }
        }
    }
}

