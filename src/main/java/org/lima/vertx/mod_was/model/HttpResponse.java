package org.lima.vertx.mod_was.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;

public class HttpResponse {
	private Integer statusCode;
	private String statusMessage;
	private Map<String, String> headers = new HashMap<String, String>();
	private byte[] body = null;

	public Integer getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		
		json.putNumber("statusCode", statusCode);
		json.putString("statusMessage", statusMessage);

		JsonObject jHeaders = new JsonObject();
		for(Entry<String, String> entry: headers.entrySet()) {
			jHeaders.putString(entry.getKey(), entry.getValue());
		}
		json.putObject("headers", jHeaders);
		
		json.putBinary("body", body);
		
		return json;
	}
	
	public HttpServerResponse applyServerResponse(HttpServerResponse res) {
		// TODO
		return res;
	}

	static public HttpResponse create(JsonObject json) {
		HttpResponse res = new HttpResponse();
		
		res.setStatusCode(json.getInteger("statusCode"));
		res.setStatusMessage(json.getString("statusMessage"));

		JsonObject jHeaders = json.getObject("headers");
		if(jHeaders != null) {
			for(String key: jHeaders.getFieldNames()) {
				res.getHeaders().put(key, jHeaders.getString(key));
			}
		}

		res.setBody(json.getBinary("body"));
		
		return res;
	}

	static public HttpResponse create(HttpClientResponse cliRes, byte[] body) {
		HttpResponse res = new HttpResponse();
		
		res.setStatusCode(cliRes.statusCode());
		res.setStatusMessage(cliRes.statusMessage());
		for(Entry<String, String> entry: cliRes.headers().entries()) {
			res.getHeaders().put(entry.getKey(), entry.getValue());
		}
		res.setBody(body);

		return res;
	}
}
