package de.unibonn.iai.eis.linda.helper;

import java.util.regex.Pattern;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.WebContent;

import com.google.common.io.Resources;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;


/**
 * @author gsingharoy
 * 
 *         This class handles SPARQL queries
 **/
public class SPARQLHandler {

	// This method executes a SPARQL query in DBPedia
	public static ResultSet executeDBPediaQuery(String queryString) {
		return executeQuery("http://dbpedia.org/sparql?timeout=100000",queryString);
	}

	public static ResultSet executeQuery(String uri, String queryString, boolean summary) {
//		if (uri.toLowerCase().contains("dbpedia") && !summary){
//			return executeDBpediaLocalQuery(queryString);
//		}
//		else {
			Query query = QueryFactory.create(queryString);
			QueryEngineHTTP qexec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(uri, query);
			qexec.setTimeout(1000000);
			
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//			}
			
			qexec.setSelectContentType(WebContent.contentTypeResultsJSON);
			ResultSet results = qexec.execSelect();
			return results;
//		}
	}
	
	
	// This method executes a SPARQL query in a RDF triple store
	public static ResultSet executeQuery(String uri, String queryString) {
		return executeQuery(uri, queryString, false);
	}
	
	static Model local = ModelFactory.createOntologyModel();
	static{
		local.read(SPARQLHandler.class.getResourceAsStream("/dbpedia_2014.owl"),null);
	}
	
	private static ResultSet executeDBpediaLocalQuery(String query){
		com.hp.hpl.jena.query.Query q = QueryFactory.create(query);
	    QueryExecution qe = QueryExecutionFactory.create(q, local);
		
		return qe.execSelect();
	}

	public static String getLiteralValue(RDFNode literal){
		String result = literal.toString();
		if(result.contains("^^"))
			result = result.split("\\^\\^")[0];
		return result;
	}
	
	public static String getPrefixes() {
		String prefixes = "";
		prefixes += "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
		prefixes += "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> ";
		prefixes += "PREFIX owl:<http://www.w3.org/2002/07/owl#> ";
		return prefixes;
	}

	public static String getLabelName(RDFNode label) {
		Integer languageIdentifierPoint = label.toString().length() - 3;
		return label.toString().substring(0, languageIdentifierPoint);
	}

	public static String getLabelFromNode(String dataset, RDFNode node,
			String language) {
		return getLabelFromNode(dataset, node.toString(), language);
	}

	public static String getLabelFromNode(String dataset, String node,
			String language) {
		String label = "";
		String query = getPrefixes();
		query += "select distinct ?label where {<" + node
				+ "> rdfs:label ?label. FILTER(langMatches(lang(?label), \""
				+ language + "\"))} ";
		ResultSet labelResultSet = executeQuery(dataset, query);
		if (labelResultSet.hasNext()) {
			label = getLabelName(labelResultSet.next().get("label"));
		}
		return label;
	}

	public static String getXMLSchemaURI() {
		return "http://www.w3.org/2001/XMLSchema";
	}

	public static Boolean isDataTypeUri(String uri) {
		if (Pattern.compile(Pattern.quote(SPARQLHandler.getXMLSchemaURI()), Pattern.CASE_INSENSITIVE).matcher(uri).find())
			return true;
		else {
			if (SPARQLHandler.isLangLiteralUri(uri)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static Boolean isLangLiteralUri(String uri) {
		return uri.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString");
	}


	public static String getLabelLanguage(RDFNode label) {
		return (label.asLiteral().getLanguage());
	}
	
	
	public static String getLabelLanguage(String label) {
		Integer languageIdentifierPoint = label.length() - 2;
		if (SPARQLHandler.isLanguageLiteral(label))
			return label.substring(languageIdentifierPoint,
					label.length());
		else
			return "";
	}

	public static String getLabelText(RDFNode label) {
		return label.asLiteral().getString();
//		return SPARQLHandler.getLabelText(label.toString());
	}

	public static String getLabelText(String label) {
		if (SPARQLHandler.isLanguageLiteral(label)) {
			return label.substring(0, label.toString().length() - 3);
		} else {
			return label;
		}
	}

	public static Boolean isLanguageLiteral(RDFNode label) {
		return SPARQLHandler.isLanguageLiteral(label.toString());
	}

	public static Boolean isLanguageLiteral(String label) {
		if (label.charAt(label.length() - 3) == '@')
			return true;
		else
			return false;

	}

	public static String getBaseUrl(RDFNode node) {
		return SPARQLHandler.getBaseUrl(node.toString());
	}

	public static String getBaseUrl(String nodeUrl) {
		String baseUrl = "";
		Integer baseEnd = 0;
		for (Integer i = nodeUrl.length() - 1; i >= 0; i--) {
			if (nodeUrl.charAt(i) == '/' || nodeUrl.charAt(i) == '#') {
				baseEnd = i;
				break;
			}
		}
		if (baseEnd > 0)
			baseUrl = nodeUrl.substring(0, baseEnd);
		return baseUrl;
	}

	public static String getLiteralDataType(RDFNode literal) {
		String result = literal.toString();
		if(result.contains("^^"))
			result = result.split("\\^\\^")[1];
		else
			result = "";
		return result;
	}
}