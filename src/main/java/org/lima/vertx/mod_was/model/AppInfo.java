package org.lima.vertx.mod_was.model;

import java.util.ArrayList;
import java.util.List;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

public class AppInfo {
	private String name;
	private List<UrlMapper> urlMappers;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<UrlMapper> getUrlMappers() {
		return urlMappers;
	}
	public void setUrlMappers(List<UrlMapper> urlMappers) {
		this.urlMappers = urlMappers;
	}
	
	static public AppInfo create(JsonObject json) {
		AppInfo appInfo = new AppInfo();
		
		appInfo.setName(json.getString("name"));
		appInfo.setUrlMappers(new ArrayList<UrlMapper>());
		JsonArray jUrlMappers = json.getArray("urlMappers");
		if(jUrlMappers != null && jUrlMappers.size() > 0) {
			for(Object e: jUrlMappers) {
				appInfo.getUrlMappers().add(UrlMapper.create((JsonObject)e));
			}
		}
		
		return appInfo;
	}
}
