package org.lima.vertx.mod_was.handler;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.eventbus.ReplyException;

public abstract class EBReplyHandler<T> implements Handler<Message<T>>{
	@Override
	public void handle(Message<T> msg) {
		if(msg.body() instanceof ReplyException) {
			handleFailure((ReplyException)msg.body());
		}
		else {
			handleSuccess(msg.body());
		}
	}
	public abstract void handleSuccess(T data);
	public abstract void handleFailure(ReplyException ex);
}
