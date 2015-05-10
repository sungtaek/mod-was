package org.lima.vertx.mod_was.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
	private int remainUsec = 0;
	private Map<String, Session> sessionPool = new HashMap<String, Session>();
	
	public SessionManager(int remainUsec) {
		this.remainUsec = remainUsec;
	}
	
	public Session createSession() {
		Session session = new Session(remainUsec);
		return sessionPool.put(session.getId(), session);
	}

	public Session getSession(String id) {
		long curTime = System.currentTimeMillis();
		Session session = sessionPool.get(id);
		if(session != null) {
			if(session.getExpireTime() > 0
				&& session.getExpireTime() < curTime) {
				removeSession(id);
				session = null;
			}
			else {
				session.update(remainUsec);
			}
		}
		return session;
	}

	public int removeSession(String id) {
		sessionPool.remove(id);
		return sessionPool.size();
	}

	public int removeSession(Session session) {
		return removeSession(session.getId());
	}
}
