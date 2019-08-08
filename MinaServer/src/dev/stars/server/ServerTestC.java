package dev.stars.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import dev.stars.net.message.SocketMessage;
import dev.stars.net.mina.BaseCodecFactory;
import dev.stars.net.mina.handler.FileMessageHandler;
import dev.stars.net.mina.handler.SocketMessageDispatcher;
import dev.stars.util.LogUtils;

public class ServerTestC {


	public static void main(String[] args) {
		//Tcp监听端口
		NioSocketAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		//设置读空闲和写空闲
		acceptor.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, Constants.READ_IDLE_TIMEOUT);
		// 设置会话写空闲时间，当会话写空闲发送心跳包给服务器
		acceptor.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, Constants.WRITE_IDLE_TIMEOUT);
		//Sets write timeout in seconds
		acceptor.getSessionConfig().setWriteTimeout(2000);
		//因为传输文档，实时性不高，对文档准确性高，所以开启时延，开启Nagle算法，但是时延高（该算法要求一个TCP连接上最多只能有一个未被确认的小分组，在该小分组的确认到来之前，不能发送其他小分组。）
		acceptor.getSessionConfig().setTcpNoDelay(true);
		
		acceptor.getFilterChain().addLast("BaseFilter", new ProtocolCodecFilter(new BaseCodecFactory()));
		// 设置连接超时检查时间
		//设置可重用线程池
		acceptor.getFilterChain().addLast("threadpool",
				new ExecutorFilter(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1)));
		final SocketMessageDispatcher socketMessageDispatcher = new SocketMessageDispatcher();
		//业务处理
		acceptor.setHandler(new IoHandlerAdapter() {
			@Override
			public void sessionClosed(IoSession session) throws Exception {
				super.sessionClosed(session);
				LogUtils.DT("session:" + session.getId() + " 已关闭");
			}

			@Override
			public void sessionOpened(IoSession session) throws Exception {
				// TODO Auto-generated method stub
				super.sessionOpened(session);
				LogUtils.DT("session:" + session.getId() + " 已建立");
			}

			@Override
			public void messageSent(IoSession session, Object message) throws Exception {
				super.messageSent(session, message);
				SocketMessage msg = (SocketMessage) message;
			}

			@Override
			public void messageReceived(final IoSession session, final Object message) throws Exception {
				super.messageReceived(session, message);
				socketMessageDispatcher.handleMessage(session, (SocketMessage) message);
			}

			@Override
			public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
				super.exceptionCaught(session, cause);
				cause.printStackTrace();
				session.closeNow();
			}
		});
		//设置下载目录
		FileMessageHandler.setDownloadPath("G:\\server_download");
		try {
			acceptor.bind(new InetSocketAddress(Constants.PORT));
			System.out.println("已绑定 "+Constants.PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

}
