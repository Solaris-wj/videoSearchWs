package casia.isiteam.videosearch.util.filetransfer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import casia.isiteam.videosearch.util.Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class FileReceiverHandler extends ChannelHandlerAdapter {

	FileInfo fileInfo = null;
	FileReceiver fileReceiver = null;
	OutputStream out = null;
	BufferedOutputStream bout = null;
	long readedSize = 0;

	public FileReceiverHandler(FileReceiver fileReceiver) {
		this.fileReceiver = fileReceiver;

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		ByteBuf buf = (ByteBuf) msg;

		
		if (fileInfo == null) {
			int fileNameLength = buf.readInt();

			byte[] dst = new byte[fileNameLength];

			buf.readBytes(dst);
			String nameOnClient = new String(dst);
			long fileLength = buf.readLong();
			

			String nameOnServer = fileReceiver.getNextFileName(nameOnClient);
			fileInfo=new FileInfo(new File(fileReceiver.getFileDir(),
					nameOnServer));
			out = new FileOutputStream(fileInfo.getFile());
			bout = new BufferedOutputStream(out);

			buf.resetReaderIndex();
			byte[] b = new byte[buf.readableBytes()];
			buf.readBytes(b);
		}

		readedSize += buf.readableBytes();

		byte[] b = new byte[buf.readableBytes()];
		buf.readBytes(b);
		bout.write(b);

		if (readedSize >= fileInfo.getFileLength()) {
			readedSize = 0;
			bout.close();
			out.close();
			fileInfo = null;

			ctx.writeAndFlush(Unpooled.copiedBuffer(fileInfo.getFileName().getBytes()));
			ctx.close();
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {

		// if (ctx.channel().isActive()) {
		// ctx.writeAndFlush(Unpooled.copiedBuffer(ResultCode.error.getBytes()));
		// }
		ctx.close();
		e.printStackTrace();

		Util.printContextInfo(null);
	}
}
