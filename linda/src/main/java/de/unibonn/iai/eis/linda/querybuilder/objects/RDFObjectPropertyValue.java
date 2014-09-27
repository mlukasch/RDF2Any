package de.unibonn.iai.eis.linda.querybuilder.objects;

import com.hp.hpl.jena.rdf.model.RDFNode;

import de.unibonn.iai.eis.linda.helper.SPARQLHandler;

public class RDFObjectPropertyValue {
	public String value;
	public String additionalValue;
	
	public RDFObjectPropertyValue(String value){
		this.value = value;
		this.additionalValue = "";
	}
	
	public RDFObjectPropertyValue(String value, String additionalValue){
		this.value = value;
		this.additionalValue = additionalValue;
	}
	
	public  RDFObjectPropertyValue(RDFObjectProperty predicate, RDFNode object){
		this.additionalValue = null;
		if(object.isLiteral()){		
			if(predicate.predicate.range.isLanguageLiteral()){
				this.value = SPARQLHandler.getLabelName(object);
				this.additionalValue = SPARQLHandler.getLabelLanguage(object);
			}
		}
		else{
			this.value = object.toString();
			this.additionalValue = "";
		}

	}
	
	public String toString(){
		String result = this.value;
		if(this.additionalValue != null)
			result += " ["+this.additionalValue+"]";
		return result;
	}
	
}