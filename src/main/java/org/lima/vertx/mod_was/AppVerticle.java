package org.lima.vertx.mod_was;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import java.util.HashMap;
import java.util.Map;

import org.lima.vertx.mod_was.handler.EBDataHandler;
import org.lima.vertx.mod_was.model.AppInfo;
import org.lima.vertx.mod_was.model.UrlMapper;
import org.lima.vertx.mod_was.server.AppServer;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

public class AppVerticle extends BusModBase {
	private Logger logger = LoggerFactory.getLogger(AppVerticle.class);
	private Map<String, AppInfo> appInfoMap = new HashMap<String, AppInfo>();
	private JsonObject serverConfig = null;
	private AppServer server = null;

	public void start() {
		super.start();

		String host = null;
		int port = 0;

		logger.info("starting ServerVerticle...");
		
		serverConfig = config.getObject("server");
		if(serverConfig == null) {
			logger.error("not found server config");
			vertx.stop();
			return;
		}
	
		eb.registerHandler(serverConfig.getString("channel"), new EBDataHandler<JsonObject>() {
			@Override
			public void handleData(JsonObject data) {
				if("register".equalsIgnoreCase(data.getString("method"))) {
					AppInfo appInfo = AppInfo.create(data.getObject("appInfo"));
					registerApplication(appInfo);
					
					JsonObject reply = new JsonObject();
					reply.putString("method", "register");
					reply.putString("status", "ok");
					
					sendReply(reply);
				}
			}
		});

		
		host = serverConfig.getString("host");
		port = serverConfig.getInteger("port");

		server = new AppServer(vertx, host, port);
		server.init();
		server.start();
	}

	private void registerApplication(AppInfo appInfo) {
		if(appInfoMap.get(appInfo.getName()) == null) {
			logger.info("register Application[" + appInfo.getName() + "]");
			appInfoMap.put(appInfo.getName(), appInfo);

			if(appInfo.getUrlMappers().size() > 0) {
				server.stop();
				for(UrlMapper urlMapper: appInfo.getUrlMappers()) {
					try {
						server.addMapper(urlMapper);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				server.init();
				server.start();
			}
		}
		else {
			logger.debug("already registered Application[" + appInfo.getName() + "]");
		}
	}
}
