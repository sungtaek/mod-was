package org.lima.vertx.mod_was.model;

import org.vertx.java.core.json.JsonObject;

public class UrlMapper {
	private String channel;
	private String method;
	private String url;

	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	static public UrlMapper create(JsonObject json) {
		UrlMapper urlMapper = new UrlMapper();
		
		urlMapper.setChannel(json.getString("channel"));
		urlMapper.setMethod(json.getString("method"));
		urlMapper.setUrl(json.getString("url"));

		return urlMapper;
	}
}
