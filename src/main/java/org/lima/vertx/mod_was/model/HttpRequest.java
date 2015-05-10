package org.lima.vertx.mod_was.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

public class HttpRequest {
	private String method;
	private String uri;
	private Map<String, String> params = new HashMap<String, String>();
	private Map<String, String> headers = new HashMap<String, String>();
	private byte[] body = null;

	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
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
		
		json.putString("method", method);
		json.putString("uri", uri);

		JsonObject jParams = new JsonObject();
		for(Entry<String, String> entry: params.entrySet()) {
			jParams.putString(entry.getKey(), entry.getValue());
		}
		json.putObject("params", jParams);

		JsonObject jHeaders = new JsonObject();
		for(Entry<String, String> entry: headers.entrySet()) {
			jHeaders.putString(entry.getKey(), entry.getValue());
		}
		json.putObject("headers", jHeaders);
		
		json.putBinary("body", body);
		
		return json;
	}
	
	public HttpClientRequest applyClientRequest(HttpClientRequest req) {
		// TODO
		return req;
	}

	static public HttpRequest create(JsonObject json) {
		HttpRequest req = new HttpRequest();
		
		req.setMethod(json.getString("method"));
		req.setUri(json.getString("uri"));

		JsonObject jParams = json.getObject("params");
		if(jParams != null) {
			for(String key: jParams.getFieldNames()) {
				req.getParams().put(key, jParams.getString(key));
			}
		}

		JsonObject jHeaders = json.getObject("headers");
		if(jHeaders != null) {
			for(String key: jHeaders.getFieldNames()) {
				req.getHeaders().put(key, jHeaders.getString(key));
			}
		}

		req.setBody(json.getBinary("body"));
		
		return req;
	}

	static public HttpRequest create(HttpServerRequest svrReq, byte[] body) {
		HttpRequest req = new HttpRequest();
		
		req.setMethod(svrReq.method());
		req.setUri(svrReq.uri());
		for(Entry<String, String> entry: svrReq.params().entries()) {
			req.getParams().put(entry.getKey(), entry.getValue());
		}
		for(Entry<String, String> entry: svrReq.headers().entries()) {
			req.getHeaders().put(entry.getKey(), entry.getValue());
		}
		req.setBody(body);

		return req;
	}
	
}