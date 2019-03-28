package RESTAPI;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import indiana.edu.erpid.DataTypeRegistry;
import indiana.edu.erpid.HandleSystem;
import indiana.edu.mapping.MappingEngine;


@Path("/query")
public class GetOperation {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/handle/{prefix}/{suffix}")
	public Response handleQuery(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws Exception{
		HandleSystem handle = new HandleSystem();
		JSONObject output = handle.httpResolve(prefix+"/"+suffix);
		//System.out.println(output.toString(4));
		return Response.status(200).entity(output.toString()).build();
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/dtr/{prefix}/{suffix}")
	public Response dtrQuery(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws Exception{
		DataTypeRegistry dtr = new DataTypeRegistry();
		JSONObject output = dtr.httpQuery(prefix+"/"+suffix);
		//System.out.println(output.toString(4));
		return Response.status(200).entity(output.toString()).build();
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/fulldtr/{prefix}/{suffix}")
	public Response dtrFullQuery(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws Exception{
		DataTypeRegistry dtr = new DataTypeRegistry();
		JSONObject output = dtr.isProfile(dtr.httpQuery(prefix+"/"+suffix));
		//System.out.println(output.toString(4));
		return Response.status(200).entity(output.toString()).build();
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/pid/{prefix}/{suffix}")
	public Response pidQuery(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws Exception{
		HandleSystem handle = new HandleSystem();
		JSONArray output = handle.handleResolve(prefix+"/"+suffix);
		//System.out.println(output.toString(4));
		return Response.status(200).entity(output.toString()).build();
	}
	
	
}
