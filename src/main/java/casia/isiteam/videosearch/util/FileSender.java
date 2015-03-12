package casia.isiteam.videosearch.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;

public class FileSender {
	
	public static String sendFile(final File file,String host, int fileTransferPort) throws Exception{
		
		EventLoopGroup group=new NioEventLoopGroup();
		
		final FileSendHandler fileSendHandler=new FileSendHandler(file);
		try {
			Bootstrap bootstrap=new Bootstrap();
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.group(group);
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p=ch.pipeline();
					
					p.addLast(new LineBasedFrameDecoder(8192));
					p.addLast(new StringDecoder());
					
					p.addLast(new ChunkedWriteHandler());
					p.addLast(new StringEncoder());
					
					p.addLast(fileSendHandler);
				}
			});
			
			ChannelFuture f = bootstrap.connect(host, fileTransferPort).sync();			
			
			
			f.channel().closeFuture().sync();		

			return fileSendHandler.getFileName();
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}finally{
			group.shutdownGracefully();
		}		
	}
}

class FileSendHandler extends SimpleChannelInboundHandler<String>{

	File file;
	String fileNameOnServer=null;
	public FileSendHandler(File file){
		this.file=file;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.write(file.getName()+"\n");
		ctx.writeAndFlush(String.valueOf(file.length())+"\n");
		
		ctx.writeAndFlush(new ChunkedFile(file));
	};
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg)
			throws Exception {
		
		fileNameOnServer=msg;		
		ctx.close();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
		
		Util.printContextInfo(null);
		
		cause.printStackTrace();		
		ctx.close();
	}
	
	String getFileName(){
		return fileNameOnServer;
	}
}
