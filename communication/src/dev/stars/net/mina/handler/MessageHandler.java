package dev.stars.net.mina.handler;

import org.apache.mina.core.session.IoSession;

import dev.stars.net.message.SocketMessage;

public interface MessageHandler {
	public void handleMessage(SocketMessage msg,IoSession session);
}
