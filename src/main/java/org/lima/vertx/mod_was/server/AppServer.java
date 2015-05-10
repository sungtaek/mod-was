package org.lima.vertx.mod_was.server;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.ServerCookieEncoder;

import java.lang.reflect.Method;
import java.util.Set;

import org.lima.vertx.mod_was.handler.EBReplyHandler;
import org.lima.vertx.mod_was.model.HttpRequest;
import org.lima.vertx.mod_was.model.HttpResponse;
import org.lima.vertx.mod_was.model.UrlMapper;
import org.lima.vertx.mod_was.session.Session;
import org.lima.vertx.mod_was.session.SessionManager;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.ReplyException;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

public class AppServer {
	private Logger logger = LoggerFactory.getLogger(AppServer.class);
	private Vertx vertx = null;

	private HttpServer server = null;
	private String host = null;
	private int port = 0;
	private SessionManager sessionManager = null;
	private RouteMatcher rm = null;
	
	public AppServer (Vertx vertx, String host, int port) {
		this.vertx = vertx;
		this.server = vertx.createHttpServer();
		this.host = host;
		this.port = port;
		this.sessionManager = new SessionManager(0);
		this.rm = new RouteMatcher();
	}
	
	public void addMapper(final UrlMapper urlMapper) throws Exception {
		String method = urlMapper.getMethod().toLowerCase();
		Method handler = null;
		
		handler = RouteMatcher.class.getMethod(method);
		if(handler != null) {
			handler.invoke(rm, urlMapper.getUrl(), new Handler<HttpServerRequest>() {
				@Override
				public void handle(final HttpServerRequest svrReq) {
					final Session session = getSession(svrReq);
					svrReq.bodyHandler(new Handler<Buffer>() {
						@Override
						public void handle(Buffer buf) {
							processRequest(urlMapper.getChannel(), svrReq, buf.getBytes(), session);
						}
					});
				}
			});
		}
	}
	
	public int init() {
		server.requestHandler(rm);

		return 0;
	}
	
	public int start() {
		if(host != null) {
			server.listen(port, host);
			logger.info("AppServer[" + host + ":" + port + "] started!!");
		}
		else {
			server.listen(port);
			logger.info("AppServer[*:" + port + "] started!!");
		}
		return 0;
	}

	public int stop() {
		server.close();
		logger.info("AppServer stopped");
		return 0;
	}

	private Session getSession(HttpServerRequest req) {
		Session session = null;
		String id = getCookie(req, "sessionId");

		if(id != null) {
			session = sessionManager.getSession(id);
		}

		if(session == null) {
			session = sessionManager.createSession();
			id = session.getId();
		}

		putCookie(req.response(), "sessionId", id);
		return session;
	}
	
	private String getCookie(HttpServerRequest req, String key) {
		String cookieVal = req.headers().get("Cookie");
		if(cookieVal != null) {
			Set<Cookie> cookies = CookieDecoder.decode(cookieVal);
			for(Cookie cookie: cookies) {
				if(cookie.getName().equals(key)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	
	private void putCookie(HttpServerResponse res, String key, String value) {
		res.putHeader("Set-Cookie", ServerCookieEncoder.encode(key, value));
	}

	private void processRequest(String channel, final HttpServerRequest svrReq, byte[] body, final Session session) {
		JsonObject msg = new JsonObject();
		HttpRequest req = HttpRequest.create(svrReq, body);
		
		msg.putObject("request", req.toJson());
		msg.putObject("session", session.getData());

		vertx.eventBus().send(channel, msg, new EBReplyHandler<JsonObject>() {
			@Override
			public void handleSuccess(JsonObject msg) {
				// TODO Auto-generated method stub
				processResponse(svrReq, session, msg);
			}
			@Override
			public void handleFailure(ReplyException ex) {
				ex.printStackTrace();
			}
		});
	}

	private void processResponse(HttpServerRequest svrReq, Session session, JsonObject msg) {
		HttpServerResponse svrRes = svrReq.response();
		HttpResponse response = HttpResponse.create(msg.getObject("response"));
		JsonObject sessionData = msg.getObject("session");

		if(sessionData != null) {
			// TODO
		}
		
		response.applyServerResponse(svrRes);
		if(!svrRes.headers().contains("Content-Length")) {
			svrRes.headers().add("Content-Length", String.valueOf(response.getBody().length));
		}
		
		svrRes.end();
	}
}