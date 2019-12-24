package org.eclipse.scava.crossflow.restmule.client.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Contributors {

	public Contributors(){}

	@JsonProperty("contributions") 
	private Integer contributions;
	
	@JsonProperty("avatar_url") 
	private String avatarUrl;
	
	@JsonProperty("id") 
	private Integer id;
	
	@JsonProperty("login") 
	private String login;
	
	@JsonProperty("gravatar_id") 
	private String gravatarId;
	
	@JsonProperty("url") 
	private String url;
	
	public Integer getContributions() {
		return this.contributions;
	}
	
	public String getAvatarUrl() {
		return this.avatarUrl;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public String getLogin() {
		return this.login;
	}
	
	public String getGravatarId() {
		return this.gravatarId;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	@Override
	public String toString() {
		return "Contributors [ "
			+ "contributions = " + this.contributions + ", "
			+ "avatarUrl = " + this.avatarUrl + ", "
			+ "id = " + this.id + ", "
			+ "login = " + this.login + ", "
			+ "gravatarId = " + this.gravatarId + ", "
			+ "url = " + this.url + ", "
			+ "]"; 
	}	
}	
