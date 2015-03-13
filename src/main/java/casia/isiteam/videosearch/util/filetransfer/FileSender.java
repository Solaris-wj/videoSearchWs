package casia.isiteam.videosearch.util.filetransfer;

import java.io.Closeable;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import casia.isiteam.videosearch.util.Util;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;

public class FileSender implements Callable<Void>, Closeable {
	long TimeOutPeriod=30;
	String host;
	int fileTransferPort;
	Channel ch = null;
	ExecutorService selfExecutor = Executors.newSingleThreadExecutor();

	EventExecutor executor = new DefaultEventExecutor();
	AtomicBoolean isShutDown = new AtomicBoolean(false);

	public EventExecutor getExecutor() {
		return executor;
	}

	ConcurrentLinkedQueue<Promise<String>> promiseQueue = new ConcurrentLinkedQueue<Promise<String>>();

	Timer timer=new HashedWheelTimer();
	public FileSender(String host, int fileTransferPort) {
		this.host = host;
		this.fileTransferPort = fileTransferPort;
		
		timer.newTimeout(new CheckPromise(), TimeOutPeriod, TimeUnit.SECONDS);
	}

	/**
	 * 由于请求都是顺序发送，有一个没收到回复，后面的都认为是失败了
	 * @author dell
	 *
	 */
	private class CheckPromise implements TimerTask{
		int lastPromiseSize=promiseQueue.size();
		@Override
		public void run(Timeout timeout) throws Exception {
			if(lastPromiseSize==promiseQueue.size()){
				setAllPromiseFailed(new TimeoutException("operation time out!"));
				FileSender.this.forceClose();			
				
				return;
			}else{
				lastPromiseSize=promiseQueue.size();
				timer.newTimeout(this, TimeOutPeriod, TimeUnit.SECONDS);
			}		
		}
		
	}
	
	synchronized private void setAllPromiseFailed(Throwable cause){
		Promise [] promises = new Promise[promiseQueue.size()];
		promises = promiseQueue.toArray(promises);
		
		promiseQueue.clear();
		for(Promise<String> promise:promises){
			promise.setFailure(cause);
		}
	}
	
	
	/**
	 * 关闭连接和线程组，保证任务都完成，调用后由timer负责等待所有请求都接收到回复，然后关闭
	 */
	@Override
	public void close() {
		isShutDown.set(true);
		// 等待所有队列中的promise收到回复后，由handler调用forceclose关闭
	}

	public void forceClose() {
		isShutDown.set(true);
		ch.close();
		selfExecutor.shutdownNow();
	}

	public Future<String> sendFile(final File file) throws Exception {
		// 如果关闭了就不能再发送
		if (isShutDown.get()) {
			throw new Exception("file send has been shut down");
		}
		executor.submit(new Callable<String>() {
			public String call() throws Exception {
				byte[] b = file.getName().getBytes();
				ByteBuf buf = ch.alloc().buffer();
				buf.writeInt(b.length);
				buf.writeBytes(b);
				buf.writeLong(file.length());
				ch.writeAndFlush(buf);
				ch.writeAndFlush(new ChunkedFile(file));
				return null;
			}
		});

		// 写完之后 ，应该收到回复才确认真的完成了。
		Promise<String> ret = executor.newPromise();
		promiseQueue.offer(ret);
		return ret;
	}

	public ConcurrentLinkedQueue<Promise<String>> getPromiseQueue() {
		return promiseQueue;
	}

	/**
	 * 直接建立连接发送文件，完成后关闭。阻塞等待！
	 * 
	 * @param file
	 * @param host
	 * @param fileTransferPort
	 * @return 成功0 失败-1
	 * @throws Exception
	 * @throws
	 */
	public static String sendFile(final File file, String host,
			int fileTransferPort) throws Exception {
		FileSender fileSender = null;
		try {
			fileSender = new FileSender(host, fileTransferPort);
			fileSender.start();
			synchronized (fileSender) {
				fileSender.wait();
			}
			Future<String> ret = fileSender.sendFile(file);

			return ret.get();
		} finally {
			fileSender.close();
		}
	}

	@Override
	public Void call() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();

		final FileSender fileSender = this;
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.group(group);
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();

					// decoder
					p.addLast(new LoggingHandler(LogLevel.INFO));
					p.addLast(new LengthFieldBasedFrameDecoder(
							Integer.MAX_VALUE, 0, Integer.BYTES, 0,
							Integer.BYTES));
					p.addLast(new StringDecoder());

					// encoder
					p.addLast(new LengthFieldPrepender(Integer.BYTES));
					p.addLast(new ChunkedWriteHandler());

					// business logic
					p.addLast(new FileSenderHandler(fileSender));
				}
			});

			ChannelFuture f = bootstrap.connect(host, fileTransferPort).sync();

			synchronized (this) {
				this.notify();
			}

			this.ch = f.channel();
			f.channel().closeFuture().sync();
			
			//对端关闭链接，自己关不关？
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Util.printContextInfo(null);
			throw e;

		} finally {
			group.shutdownGracefully();
			executor.shutdownGracefully();
		}
		return null;
	}

	/**
	 * 在一个新线程启动自身
	 */
	public void start() {
		selfExecutor.submit(this);
	}

	public static void main(String[] args) throws Exception {

//		String ret0 = FileSender.sendFile(new File("C:/t.txt"), "127.0.0.1",
//				9001);
		//System.out.println(ret0);

		FileSender fileSender = new FileSender("127.0.0.1", 9001);

		fileSender.start();
		
		synchronized (fileSender) {
			fileSender.wait();
		}

		final FileSender finalFileSender = fileSender;
		final File file = new File("C:/t.txt");

		Future<String> ret = finalFileSender.sendFile(file);
		
		ret.addListener(new FutureListener<String>() {

			@Override
			public void operationComplete(Future<String> future)
					throws Exception {
				System.out.println(finalFileSender.ch.isOpen());
			}
		});
		
		
		Future<String> ret2 = finalFileSender.sendFile(file);

		
		fileSender.close();
		ret.await().sync();
		System.out.println("end of main");
		// int num = 1;
		// for (int i = 0; i != num; ++i) {
		//
		// new Thread() {
		// @Override
		// public void run() {
		//
		// try {
		// finalFileSender.sendFile(new FileInfo(file));
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// }.start();
		// }

	}

}
