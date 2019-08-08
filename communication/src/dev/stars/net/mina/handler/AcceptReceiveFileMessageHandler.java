package dev.stars.net.mina.handler;

import java.io.File;
import java.io.IOException;

import org.apache.mina.core.session.IoSession;

import dev.stars.net.message.AcceptReceiveFileMessage;
import dev.stars.net.message.SocketMessage;
import dev.stars.util.GzipUtil;
/**
 * 
 * @author Administrator
 *
 */
public class AcceptReceiveFileMessageHandler extends FileMessageHandler {

	@Override
	public void handleMessage(SocketMessage msg, IoSession session) {
		AcceptReceiveFileMessage requestSendFileMessage = new AcceptReceiveFileMessage(
				msg);
		long id = requestSendFileMessage.getId();
		FileTask fileTask = mFileTasks.get(id);
		if (fileTask == null) {
			System.err.println("wrong file task id " + id);
			session.closeOnFlush();
		} else {
			//将文档拆分，多线程发送文档
			sendFilePart(fileTask, session);
		}
	}


}
