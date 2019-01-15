package indiana.edu.erpid;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;


@Path("/query")
public class QueryOperation {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/HANDLE/{prefix}/{suffix}")
	public String handleQuery(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws Exception{
		HandleSystem handle = new HandleSystem();
		JSONObject output = handle.httpQuery(prefix+"/"+suffix);
		//System.out.println(output.toString(4));
		return output.toString(4);
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/DTR/{prefix}/{suffix}")
	public String dtrQuery(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws Exception{
		DataTypeRegistry dtr = new DataTypeRegistry();
		JSONObject output = dtr.httpQuery(prefix+"/"+suffix);
		//System.out.println(output.toString(4));
		return output.toString(4);
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/fullDTR/{prefix}/{suffix}")
	public String dtrFullQuery(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws Exception{
		DataTypeRegistry dtr = new DataTypeRegistry();
		JSONObject output = dtr.isProfile(dtr.httpQuery(prefix+"/"+suffix));
		//System.out.println(output.toString(4));
		return output.toString(4);
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/PID/{prefix}/{suffix}")
	public String pidQuery(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws Exception{
		HandleSystem handle = new HandleSystem();
		JSONArray output = handle.handleQuery(prefix+"/"+suffix);
		//System.out.println(output.toString(4));
		return output.toString(4);
	}
	
}
