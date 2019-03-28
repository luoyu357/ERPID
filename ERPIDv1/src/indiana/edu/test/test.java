package indiana.edu.test;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class test {
	public static void main(String[] args) {
		try {

			Client client = Client.create();

			WebResource webResource = client
			   .resource("http://localhost:8080/ERPIDv1/query/DTR/11723/2bddff949f83f771ba83");

			ClientResponse response = webResource.accept("application/json")
	                   .get(ClientResponse.class);

			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
			}

			JSONObject output = new JSONObject(response.getEntity(String.class));

			System.out.println("Output from Server .... \n");
			System.out.println(output.toString());

		  } catch (Exception e) {

			e.printStackTrace();

		  }

		}
}
