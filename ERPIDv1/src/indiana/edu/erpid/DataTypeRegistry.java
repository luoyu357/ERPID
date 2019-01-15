package indiana.edu.erpid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import indiana.edu.property.Property;

public class DataTypeRegistry {
	public Properties property = new Properties();
	public String dtr;
	
	public DataTypeRegistry() throws IOException{
		this.property = (new Property()).property;
		this.dtr = this.property.getProperty("dtr.restful.api.url");
		
	}

	public JSONObject httpQuery(String dtrID) throws Exception {
		
		String dtrURL = this.dtr + dtrID;
		URL object = new URL(dtrURL);
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
	
	
	public JSONObject isProfile(JSONObject profile) throws JSONException, Exception {
		
		if (profile.has("properties")) {
			JSONArray properties = (JSONArray) profile.get("properties");
			JSONArray newProperties = new JSONArray();
			for (Object item : properties) {
				JSONObject property = (JSONObject) item;
				JSONObject resolvedProperty = isProfile(httpQuery(property.getString("identifier")));
				newProperties.put(resolvedProperty);						
			}
			profile.put("properties", newProperties);
			return profile;
		}else {
			return profile;
		}
	}
}
