package org.lima.vertx.mod_was.handler;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

public abstract class EBDataHandler<T> implements Handler<Message<T>>{
	private Message<T> msg;
	@Override
	public void handle(Message<T> msg) {
		this.msg = msg;
		handleData(msg.body());
	}
	public void sendReply(T data) {
		msg.reply(data);
	}
	public void sendFailure(int failureCode, String message) {
		msg.fail(failureCode, message);
	}
	public abstract void handleData(T data);
}
