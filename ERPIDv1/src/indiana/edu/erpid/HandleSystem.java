package indiana.edu.erpid;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import indiana.edu.mapping.MappingEngine;
import indiana.edu.property.Property;
import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.AuthenticationInfo;
import net.handle.hdllib.CreateHandleRequest;
import net.handle.hdllib.ErrorResponse;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.Util;

public class HandleSystem {
	
	public Properties property = new Properties();
	public String handle;
	public String handlePrefix;
	public String admin_privKey_file;
	public String handle_admin_identifier;
	
	public HandleSystem() throws IOException{
		this.property = (new Property()).property;
		this.handle = this.property.getProperty("handle.restful.api.url");
		this.handlePrefix = this.property.getProperty("handle.prefix");
		this.admin_privKey_file = this.property.getProperty("private.key.file");
		this.handle_admin_identifier = this.property.getProperty("handle.admin.identifier");
	}

	public JSONObject httpResolve(String pidID) throws Exception {
		
		String handleURL = this.handle + pidID;
		URL object = new URL(handleURL);
		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setRequestMethod("GET");
		
		StringBuilder content;

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {

            String line;
            content = new StringBuilder();

            while ((line = in.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
        }

        String jsonString = content.toString();
        
        JSONObject output = new JSONObject(jsonString);
        
		return output;
		
	}
	
	public JSONArray handleResolve(String pidID) throws HandleException, Exception {
		HandleValue values[] = new HandleResolver().resolveHandle(pidID, null, null);
		HashMap<JSONObject, String> resolved = (new MappingEngine()).mappingHandleObject(values);
		
		JSONArray newPID = new JSONArray();
		for (Map.Entry item : resolved.entrySet()) {
			JSONObject temp = new JSONObject();
			temp.put("Type", (JSONObject) item.getKey());
			temp.put("Value", item.getValue().toString());
			newPID.put(temp);
		}
		return newPID;
	}
	
	
	public String createHandle(JSONObject object) throws Exception {

		HandleResolver resolver = new HandleResolver();
		
		File privKeyFile = new File(this.admin_privKey_file);
		PrivateKey hdl_adm_priv = net.handle.hdllib.Util.getPrivateKeyFromFileWithPassphrase(privKeyFile, "luoyu");
		byte adm_handle[] = Util.encodeString(this.handle_admin_identifier);
		AuthenticationInfo auth = new net.handle.hdllib.PublicKeyAuthenticationInfo(adm_handle, 300, hdl_adm_priv);
		
		String uuid = UUID.randomUUID().toString();
		String handle_identifier = this.handlePrefix + "/" + uuid;
		object.put("11723/dde57e820d65c87a0777", handle_identifier);
		
		HandleValue[] new_values = new HandleValue[object.keySet().size()];
		
		int count = 0;
		for (Object attribute : object.keySet()){
			HandleValue new_value = new HandleValue(count+1, Util.encodeString(attribute.toString()), Util.encodeString(object.get(attribute.toString()).toString()));
			new_values[count] = new_value;
			count++;
		}
		
		CreateHandleRequest assign_request = new CreateHandleRequest(Util.encodeString(handle_identifier), new_values,
				auth);

		
		AbstractResponse response_assign = resolver.processRequestGlobally(assign_request);
		
		if (response_assign.responseCode == AbstractMessage.RC_SUCCESS) {
			return handle_identifier;

		} else {
			byte values[] = ((ErrorResponse) response_assign).message;
			for (int i = 0; i < values.length; i++) {
				System.out.print(String.valueOf(values[i]));
			}
			return "Failed";
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		HandleSystem test = new HandleSystem();
		System.out.println(test.handleResolve("11723/FCD56E9D-5956-48CF-9304-6CFDD5EFDE47").toString(4));
	}
	
}
