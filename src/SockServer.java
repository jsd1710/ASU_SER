package src;
import java.net.*;
import java.util.Arrays;
import java.io.*;

class SockServer 
{
    static int total = 0;   
    static BufferedReader reader;
    
    static String[] parseInput(InputStream inputStream) throws IOException
    {
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String[] arguments = reader.readLine().split(" ");
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
    		e.printStackTrace();
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
                Thread.sleep(1000);
                total += addInput(arguments[0]);

                out.write(total);
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

