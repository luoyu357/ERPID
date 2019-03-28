package RESTAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import indiana.edu.deposit.FedoraClient;
import indiana.edu.erpid.HandleSystem;
import indiana.edu.mapping.MappingEngine;
import indiana.edu.property.Property;

@Path("/create")
public class PostOperation {
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/checkpid")
	public Response checkProfileOFPID(String object) throws JSONException, Exception {
		JSONObject input = new JSONObject(object);	
		MappingEngine mapping = new MappingEngine();
		JSONObject result = new JSONObject(mapping.checkProfile(input));
		return Response.status(200).entity(result.toString()).build();	
	}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("pid")
	public Response createHandle(String object) throws Exception {
		JSONObject input = new JSONObject(object);
		HandleSystem handle = new HandleSystem();
		String output = handle.createHandle(input);
		if (!output.equalsIgnoreCase("Failed")) {
			return Response.status(200).entity(output).build();
		} else {
			return Response.status(400).entity(output).build();
		}
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("server_upload")
	//upload file into Fedora, use uuid for container name
	public Response createAndUploadFromServer(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition dcd,
            @FormDataParam("pid") String object) throws Exception {
		
		Properties property = (new Property()).property;
		String uploadedFileLocation = property.getProperty("temp.file.location");
		writeToFile(fileInputStream, uploadedFileLocation);
		
		FedoraClient fedora = new FedoraClient();
		String uuid = UUID.randomUUID().toString();
		fedora.createContainer(uuid);
		fedora.uploadFile(uuid, dcd.getFileName().split(".")[0], uploadedFileLocation+dcd.getFileName());
		String fedoraAddress = fedora.getOutputAddress();
		
		JSONObject pid = new JSONObject(object);
		pid.put("11723/b8457812905b83046284", fedoraAddress);
		//pid.put("URL", fedoraAddress);
		HandleSystem handle = new HandleSystem();
		String result = handle.createHandle(pid);
		
		if (!result.equalsIgnoreCase("Failed")) {
			return Response.status(200).entity(result).build();
		} else {
			return Response.status(400).entity(result).build();
		}		
	}
		
	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
			try {
				
				
				OutputStream out = new FileOutputStream(new File(
						uploadedFileLocation));
				int read = 0;
				byte[] bytes = new byte[1024];

				out = new FileOutputStream(new File(uploadedFileLocation));
				while ((read = uploadedInputStream.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.flush();
				out.close();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("client_upload")
	public Response createAndUploadFromClient(String object) throws Exception {
		JSONObject input = new JSONObject(object);
		String fileLocation = input.getString("11723/b8457812905b83046284");
		String[] temp1 = fileLocation.split("/");
 		String[] temp2 = temp1[temp1.length-1].split(".");
 		
		FedoraClient fedora = new FedoraClient();
		String uuid = UUID.randomUUID().toString();
		fedora.createContainer(uuid);
		fedora.uploadFile(uuid, temp2[temp2.length-1], fileLocation);
		String fedoraAddress = fedora.getOutputAddress();
		
		JSONObject pid = new JSONObject(object);
		pid.put("11723/b8457812905b83046284", fedoraAddress);
		//pid.put("URL", fedoraAddress);
		HandleSystem handle = new HandleSystem();
		String result = handle.createHandle(pid);
		
		if (!result.equalsIgnoreCase("Failed")) {
			return Response.status(200).entity(result).build();
		} else {
			return Response.status(400).entity(result).build();
		}		
	}
	
	
}
