package src;
import java.net.*;
import java.io.*;

class SockClient 
{
	static BufferedReader reader;
	static PrintWriter pw;
	
     public static void main (String args[]) throws Exception 
     {
        Socket          sock = null;
        OutputStream    out = null;
        InputStream     in = null;
		
        try 
        {
            sock = new Socket("localhost", 8888);
            out = sock.getOutputStream();
            in = sock.getInputStream();

            pw = new PrintWriter(out, true);
            String output_text = "";
            if (args.length == 1)
            {
                output_text = args[0];
            }
            else
            {
            	for (int i = 0; i < args.length; i++)
            	{
            		output_text += args[i] + " ";
            	}
            }
            
            pw.println(output_text);
            
            reader = new BufferedReader(new InputStreamReader(in));
            String result = reader.readLine();
            System.out.println("Result is " + result);
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
