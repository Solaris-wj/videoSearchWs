package casia.isiteam.videosearch.util;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import casia.isiteam.videosearch.util.Util;

public class FileServer implements Runnable {

	String host;
	int fileTransferPort;
	String tempFileDir = null;
	boolean useFileNameOnClient=true; 
	ExecutorService executor;

	public FileServer(String host, int port, String tempFileDir, boolean useFileNameOnClient) {
		this.host=host;
		this.fileTransferPort = port;
		this.tempFileDir = tempFileDir;
		this.useFileNameOnClient=useFileNameOnClient;
		executor = Executors.newCachedThreadPool();
	}

	@Override
	public void run() {

		EventLoopGroup group = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap sBootstrap = new ServerBootstrap();
			sBootstrap.group(group, workGroup);
			sBootstrap.channel(NioServerSocketChannel.class);
			sBootstrap.option(ChannelOption.TCP_NODELAY, true);

			sBootstrap
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {

							ChannelPipeline cp = ch.pipeline();
							cp.addLast(new LineBasedFrameDecoder(8192));
							cp.addLast(new StringDecoder());

							cp.addLast(new MasterFileHandler(tempFileDir,useFileNameOnClient));
						}
					});

			ChannelFuture f;

			f = sBootstrap.bind(host, fileTransferPort).sync();

			//等待服务器关闭，会阻塞线程
			f.channel().closeFuture().sync();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Util.printContextInfo(null);
		}
		finally{
			group.shutdownGracefully();
			workGroup.shutdownGracefully();
		}

	}

}

class MasterFileHandler extends SimpleChannelInboundHandler<String> {

	AtomicInteger fileNum = new AtomicInteger(0);

	String tempFileDir = null;
	String fileNameOnClient = null;
	long fileSize = 0;
	
	boolean useFileNameOnClient;
	public MasterFileHandler(String tempFileDir, boolean useFileNameOnClient) {
		this.tempFileDir = tempFileDir;
		this.useFileNameOnClient=useFileNameOnClient;
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg)
			throws Exception {
		if (fileNameOnClient == null) {
			fileNameOnClient = msg;
		} else if (fileSize == 0) {
			fileSize = Long.parseLong(msg);
		} else {
			ctx.pipeline().addFirst(
					new FileReadHandler(tempFileDir,
							useFileNameOnClient?fileNameOnClient:getNextFileName(fileNameOnClient), fileSize));
		}

	}

	public String getNextFileName(String fileNameOnClient) {

		int ind = fileNameOnClient.lastIndexOf('.');

		String extString = fileNameOnClient.substring(ind,
				fileNameOnClient.length());

		return fileNum.getAndIncrement() + extString;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
		
		cause.printStackTrace();
		Util.printContextInfo(null);
		
		ctx.close();
	}
}

class FileReadHandler extends ChannelHandlerAdapter {

	String fileName = null;
	long fileSize = 0;

	File file;
	FileOutputStream ofs;
	long readedSize = 0;

	public FileReadHandler(String tempFileDir, String fileName, long fileSize)
			throws FileNotFoundException {
		this.fileName = fileName;
		this.fileSize = fileSize;

		file = new File(tempFileDir, fileName);
		ofs = new FileOutputStream(file);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		ByteBuf buf = (ByteBuf) msg;

		readedSize += buf.readableBytes();
		if (buf.isReadable()) {
			byte[] bytes = new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			ofs.write(bytes);
		}

		if (readedSize >= fileSize) {
			readedSize = 0;
			fileSize = 0;
			ofs.close();

			ctx.writeAndFlush(Unpooled.copiedBuffer((fileName+"\n").getBytes()));
			ctx.pipeline().remove(this);
		}
		buf.release();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
		
		cause.printStackTrace();
		Util.printContextInfo(null);		
		ctx.close();
	}
}
