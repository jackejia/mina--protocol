package dev.stars.net.mina.handler;

import org.apache.mina.core.session.IoSession;

import dev.stars.net.message.SocketMessage;
/**
 * 转发器：根据消息类型转到不同的handler处理
 * @author Administrator
 *
 */
public class SocketMessageDispatcher {

	public void handleMessage(IoSession session, SocketMessage msg) {
		int type = msg.getType();
		MessageHandlerFactory.getHandler(type).handleMessage(msg, session);
	}
}
