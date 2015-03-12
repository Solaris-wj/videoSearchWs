package casia.isiteam.videosearch.util.filetransfer;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Promise;

public class FileSenderHandler extends ChannelHandlerAdapter {

	FileSender fileSender;
	public FileSenderHandler(FileSender fileSender){
		this.fileSender=fileSender;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		
		Promise<String> dp=fileSender.getPromiseQueue().poll();
		String ret=(String)msg;		
		dp.setSuccess(ret);
		
		if(fileSender.isShutDown.get() && fileSender.getPromiseQueue().size()==0){
			fileSender.forceClose();
		}
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
		
		cause.printStackTrace();
		throw new Exception(cause.getMessage());
	}
	
}
