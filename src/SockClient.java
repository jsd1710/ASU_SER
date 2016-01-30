package src;
import java.net.*;
import java.io.*;

class SockClient 
{
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

            PrintWriter pw = new PrintWriter(out, true);
            String output_text = args[0];
            pw.println(output_text);
            int result = in.read();
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
