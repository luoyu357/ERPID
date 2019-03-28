package indiana.edu.doip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.json.JSONObject;

import indiana.edu.property.Property;

public class DOIPServer {
	public Properties property = new Properties();
	public ServerSocket serverSocket;
	
	public DOIPServer() throws IOException {
		this.property = (new Property()).property;
		int port = Integer.parseInt(this.property.getProperty("doip.port"));
		serverSocket = new ServerSocket(port);
		int clientID = 0;
		while(true) {
			ClientSocketThread client = new ClientSocketThread(serverSocket, clientID++);
			client.start();
		}	
	}
	
	public void stop() throws IOException {
		this.serverSocket.close();
	}
	
	public static void main(String args[]) throws IOException {
		DOIPServer test = new DOIPServer();
	}
}

class ClientSocketThread extends Thread {
	Socket clientSocket;
	boolean running = true;
	
	public ClientSocketThread(ServerSocket ss, int id) throws IOException{
		this.clientSocket = ss.accept();
		System.out.println("running client "+id);
		
	}
	
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
	        PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream());
	        
	        String line = in.readLine();
	        
	        if (line.contains("HTTP")){
	        	out.print("HTTP/1.1 200 \r\n"); 
		        out.print("Content-Type: text/plain\r\n"); 
		        out.print("Connection: close\r\n"); 
		        out.print("\r\n"); 
		        int postDataIndex = 0;
		        while ((line = in.readLine()) != null) {
			        if (line.contains("content-length")) {
		        	    postDataIndex = new Integer(
		        	        line.substring(
		        	            line.indexOf("content-length:") + 16,
		        	            line.length())).intValue();
			        }
			        if (line.length() == 0) {
			          	break;
			        }
		        }
		        
		        String postData = "";
		        
		        if (postDataIndex > 0) {
		        	char[] charArray = new char[postDataIndex];
		        	in.read(charArray, 0, postDataIndex);
		        	postData = new String(charArray);
		    	}
		        
		        JSONObject input = new JSONObject(postData);
		        
		        //add operations here
		        
		        String output = "result";
		        
		        out.println(input+"\r\n");
	        	
	        } else {
	        	JSONObject input = new JSONObject(line);
	        	
	        	//add operations here
	        	
	        	String output = "result";
	        	out.print(output);
	        }
	        
	        out.close();
	        in.close(); 
	        this.clientSocket.close(); 
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
