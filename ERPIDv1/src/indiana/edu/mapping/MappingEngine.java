package indiana.edu.mapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import indiana.edu.erpid.DataTypeRegistry;
import indiana.edu.property.Property;
import net.handle.hdllib.HandleValue;

public class MappingEngine {
	
	public Properties property = new Properties();
	public String handlePrefix;
	public String dtrPrefix;
	public String profileDTRSuffix;
	
	public MappingEngine() throws IOException {
		this.property = (new Property()).property;
		this.handlePrefix = this.property.getProperty("handle.prefix");
		this.dtrPrefix = this.property.getProperty("dtr.prefix");
		this.profileDTRSuffix = this.property.getProperty("main.pid.ki.profile.suffix");
		
	}
	
	public HashMap<JSONObject, String> mappingHandleObject(HandleValue values[]) throws JSONException, Exception {
		DataTypeRegistry dtr = new DataTypeRegistry();
		HashMap<JSONObject, String> newPID = new HashMap<JSONObject, String>();
		
		for (int i = 0; i < values.length; i++) {
			String[] list = values[i].toString().split(" ");
			String type = list[2].toString().split("=")[1];		
			String[] valueField = values[i].toString().split("\"");
			String value = "";
			if (valueField.length > 1) {
				value = valueField[1];
			}
			/*
			JSONObject temp = new JSONObject();
			if (type.contains(this.handlePrefix)) {
				JSONObject resolvedType = dtr.isProfile(dtr.httpQuery(type));
				newPID.put(resolvedType, value);
			}else {
				JSONObject resolvedType = new JSONObject();
				resolvedType.put("name", type);
				newPID.put(resolvedType, value);
			}
			*/
			if (type.contains("20.5000.347")) {
				JSONObject resolvedType = dtr.isProfile(dtr.httpQuery(type.replace("20.5000.347", "11723")));
				newPID.put(resolvedType, value);
			}else {
				JSONObject resolvedType = new JSONObject();
				resolvedType.put("name", type);
				newPID.put(resolvedType, value);
			}
					
		}
		
		return newPID;
		
	}
	
	
	
	public HashMap<String, String> checkProfile(JSONObject object) throws JSONException, Exception{
		DataTypeRegistry dtr = new DataTypeRegistry();
		HashMap<String, String> output = new HashMap<String, String>();
		
		if (object.has(this.dtrPrefix+"/"+this.profileDTRSuffix)) {
			String[] profileLink = object.getString(this.dtrPrefix+"/"+this.profileDTRSuffix).toString().split("/");
			String profileID = profileLink[profileLink.length-2]+"/"+profileLink[profileLink.length-1];
			JSONArray profileProperties = (JSONArray) dtr.httpQuery(profileID).get("properties");
			
			for (Object item : profileProperties) {
				String property = ((JSONObject) item).getString("identifier");
				if (object.has(property)) {
					output.put(property, "True");
					object.remove(property);
				}else {
					output.put(property, "False");
				}
			}
			
			if (object.length() > 0) {
				output.put("Not Matched", object.keySet().toString());
			}
			
		} else {
			output.put("No Profile", object.keySet().toString());
		}
		
		return output;
	}
	
	
	public static void main(String[] args) {
		Set<String> test = new HashSet<String>();
		test.add("1");
		test.add("2");
		String p = test.toString();
		System.out.println(p);
	}

}
