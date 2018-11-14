package newEvaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.AuthenticationInfo;
import net.handle.hdllib.CreateHandleRequest;
import net.handle.hdllib.ErrorResponse;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.SiteInfo;
import net.handle.hdllib.Util;

public class HandleSystemCall {
	
	private SiteInfo[] siteInfo = null;
	
	
	public HandleSystemCall(){
		
	}
	
	public void registerPID(JSONObject object) throws Exception{
		
		HandleResolver resolver = new HandleResolver();
		// Sample Handle Identifier Create/Assign Code Snippet
		//System.out.println("...Begin Assigning PID test...");

		// Read admpriv.bin for Handle instance authentication
		String handle_admin_identifier = "0.NA/11723";
		String admin_privKey_file = "the admpriv.bin file path";
		File privKeyFile = new File(admin_privKey_file);
		PrivateKey hdl_adm_priv = net.handle.hdllib.Util.getPrivateKeyFromFileWithPassphrase(privKeyFile, "the password to access handle server");
		byte adm_handle[] = Util.encodeString(handle_admin_identifier);
		AuthenticationInfo auth = new net.handle.hdllib.PublicKeyAuthenticationInfo(adm_handle, 300, hdl_adm_priv);

		//System.out.println("Check LHS Admin Authenticaton Status:" + new net.handle.hdllib.Resolver().checkAuthentication(auth));

		// Create one sample Handle identifier
		String handle_identifier = "20.500.12033/"+object.get("PID").toString();
		
		HandleValue[] new_values = new HandleValue[object.keySet().size()];
		
		int count = 0;
		for (Object attribute : object.keySet()){
			HandleValue new_value = new HandleValue(count+1, Util.encodeString(attribute.toString()), Util.encodeString(object.get(attribute.toString()).toString()));
			new_values[count] = new_value;
			count++;
		}
		
		CreateHandleRequest assign_request = new CreateHandleRequest(Util.encodeString(handle_identifier), new_values,
				auth);

		// Return PID create/assign response - one Handle identifier
		
		if (this.siteInfo == null) {
			this.siteInfo = resolver.findLocalSites(assign_request);
			System.out.println(this.siteInfo);
		}
		
		AbstractResponse response_assign = resolver.sendRequestToService(assign_request, siteInfo);
		
		if (response_assign.responseCode == AbstractMessage.RC_SUCCESS) {
			// The resolution was successful, so we'll cast the handle identifier
			System.out.println("Assigned Persistent Identifier:" + handle_identifier);

		} else if (response_assign.responseCode == AbstractMessage.RC_ERROR) {
			byte values[] = ((ErrorResponse) response_assign).message;
			for (int i = 0; i < values.length; i++) {
				System.out.print(String.valueOf(values[i]));
			}
		}
		
	}
	
	
	public String sendGet(String url_string) throws IOException{
		URL url = new URL(url_string);
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setUseCaches(false);
		httpCon.setDoOutput(true);	
		httpCon.setRequestMethod("GET");
		httpCon.setRequestProperty("Content-Type", "application/json");
		httpCon.setRequestProperty("Accept", "application/json");

		httpCon.connect();
	
		
		StringBuilder sb = new StringBuilder();
		//System.out.println("[Resolving the PID]: "+ url_string +" : "+httpCon.getResponseCode());
			
		BufferedReader br = new BufferedReader(new InputStreamReader(httpCon.getInputStream(),"utf-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
		    sb.append(line + "\n");
		}
		br.close();
		return sb.toString();	
	}
	
	public JSONObject parseAPIcall(String input) throws ParseException{
		
		JSONParser parse = new JSONParser();
		JSONObject json = (JSONObject) parse.parse(input);
		JSONArray values = (JSONArray) json.get("values");
		
		JSONObject output = new JSONObject();
		String[] handle = json.get("handle").toString().split("/");
		output.put("id", handle[handle.length-1]);
		
		for (int i = 0; i < values.size(); i++){
			output.put(((JSONObject) values.get(i)).get("type").toString(), ((JSONObject)((JSONObject) values.get(i)).get("data")).get("value").toString());
		}
		
		return output;
	}
	

}
