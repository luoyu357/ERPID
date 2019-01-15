package indiana.edu.erpid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import indiana.edu.property.Property;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;

public class HandleSystem {
	
	public Properties property = new Properties();
	public String handle;
	public String prefix;
	
	public HandleSystem() throws IOException{
		this.property = (new Property()).property;
		this.handle = this.property.getProperty("handle.restful.api.url");
		this.prefix = this.property.getProperty("prefix");
		
		
	}

	public JSONObject httpQuery(String pidID) throws Exception {
		
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
	
	
	public JSONArray handleQuery(String pidID) throws HandleException, Exception {
		HandleValue values[] = new HandleResolver().resolveHandle(pidID, null, null);
		JSONArray newPID = new JSONArray();
		
		DataTypeRegistry dtr = new DataTypeRegistry();
		
		for (int i = 0; i < values.length; i++) {
			String[] list = values[i].toString().split(" ");
			String type = list[2].toString().split("=")[1];		
			String[] valueField = values[i].toString().split("\"");
			String value = "";
			if (valueField.length > 1) {
				value = valueField[1];
			}
			
			JSONObject temp = new JSONObject();
			if (type.contains(this.prefix)) {
				JSONObject resolvedType = dtr.isProfile(dtr.httpQuery(type));
				temp.put("value", value);
				temp.put("Type Info", resolvedType);
			}else {
				temp.put("value", value);
				temp.put("Type Info", type);
			}
			/*
			JSONObject temp = new JSONObject();
			if (type.contains("20.5000.347")) {
				JSONObject resolvedType = dtr.isProfile(dtr.httpQuery(type.replace("20.5000.347", "11723")));
				temp.put("value", value);
				temp.put("Type Info", resolvedType);
			}else {
				temp.put("value", value);
				temp.put("Type Info", type);
			}
			*/
			newPID.put(temp);
			
			
		}
		
		return newPID;
	}
	
	
	public static void main(String[] args) throws Exception {
		HandleSystem test = new HandleSystem();
		System.out.println(test.handleQuery("11723/FCD56E9D-5956-48CF-9304-6CFDD5EFDE47").toString(4));
	}
	
}
