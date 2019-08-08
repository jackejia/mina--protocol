package dev.stars.net.mina.handler;

import java.io.File;

import org.apache.mina.core.session.IoSession;

import dev.stars.net.message.ReceiveFileFinishMessage;
import dev.stars.net.message.ReceiveFileFinishMessage.ReceiveFileInfo;
import dev.stars.util.FileUtil;
import dev.stars.net.message.SocketMessage;

public class ReceiveFileFinishMessageHandler extends FileMessageHandler {

	@Override
	public void handleMessage(SocketMessage msg, IoSession session) {
		ReceiveFileFinishMessage receiveFileMessage = new ReceiveFileFinishMessage(
				msg);
		ReceiveFileInfo receiveFileInfo = receiveFileMessage
				.getReceiveFileInfo();

		FileTask fileTask = mFileTasks.get(receiveFileInfo.id);
		if (fileTask == null) {
			System.err.println("wrong file task id " + receiveFileInfo.id);
			session.closeNow();
		} else {
			if(receiveFileInfo.success){
				System.out.println("对方接收并校验成功，总计用时(压缩+发送文件+解压):"+(System.currentTimeMillis()-fileTask.startTime)/1000d+" s");
			}else{
				System.out.println("对方并未校验成功");
			}
			removeFileTask(receiveFileInfo.id);
			FileUtil.deleteFile(fileTask.zippedFilePath);
		}

	}

}
