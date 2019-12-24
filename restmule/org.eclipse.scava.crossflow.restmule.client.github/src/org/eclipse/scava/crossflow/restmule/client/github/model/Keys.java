package org.eclipse.scava.crossflow.restmule.client.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Keys {

	public Keys(){}

	@JsonProperty("id") 
	private Integer id;
	
	@JsonProperty("title") 
	private String title;
	
	@JsonProperty("key") 
	private String key;
	
	@JsonProperty("url") 
	private String url;
	
	public Integer getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	@Override
	public String toString() {
		return "Keys [ "
			+ "id = " + this.id + ", "
			+ "title = " + this.title + ", "
			+ "key = " + this.key + ", "
			+ "url = " + this.url + ", "
			+ "]"; 
	}	
}	
