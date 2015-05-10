package org.lima.vertx.mod_was.session;

import java.util.UUID;

import org.vertx.java.core.json.JsonObject;

public class Session {
	private String id = null;
	private long createTime = 0;
	private long updateTime = 0;
	private long expireTime = 0;
	private JsonObject data = null;
	
	public Session(int remainTime) {
		long now = System.currentTimeMillis();
		this.id = UUID.randomUUID().toString();
		this.createTime = now;
		this.updateTime = now;
		if(remainTime > 0) {
			this.expireTime = now + remainTime;
		}
		this.data = new JsonObject();
	}

	public void update(int remainTime) {
		long now = System.currentTimeMillis();
		this.updateTime = now;
		if(remainTime > 0) {
			this.expireTime = now + remainTime;
		}
	}

	public String getId() {
		return id;
	}
	public long getCreateTime() {
		return createTime;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public long getExpireTime() {
		return expireTime;
	}
	public JsonObject getData() {
		return data;
	}
}
