package com.servlet.routes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.unibonn.iai.eis.linda.converters.impl.JSONConverter;
import de.unibonn.iai.eis.linda.converters.impl.results.JSONOutput;
import de.unibonn.iai.eis.linda.helper.SPARQLHandler;
import de.unibonn.iai.eis.linda.querybuilder.ClassSearch;

@Path("/v1.0/builder/")
public class BuilderRoute {
	
	@GET
	@Path("classes")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONOutput getJSONConverter(@Context UriInfo uriInfo){
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
		String search = queryParams.getFirst("search");
		String dataset = queryParams.getFirst("dataset");
		System.out.println("START Searching for classes matching '"+search+"' in dataset '"+dataset+"'");
		Double startMilliseconds = (double) System.currentTimeMillis( );
		JSONConverter converter = new JSONConverter(SPARQLHandler.executeDBPediaQuery(new ClassSearch(dataset,search).getSPARQLQuery()));
		Double endMilliseconds = (double) System.currentTimeMillis( );
		converter.jsonOutput.setTimeTaken((endMilliseconds-startMilliseconds)/1000);
		System.out.println("FINISH searching for classes ... ");
		return converter.jsonOutput;
	}
}
