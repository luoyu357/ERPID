package indiana.edu.property;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Property {
	
	public Properties property = new Properties();
		
	public Property() throws IOException {
		InputStream input = new FileInputStream("/Users/yuluo/eclipse-workspace/ERPIDv1/src/indiana/edu/property/config.properties");
		property.load(input);
	}

}
