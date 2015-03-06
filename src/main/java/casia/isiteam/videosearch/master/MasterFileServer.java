package casia.isiteam.videosearch.master;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MasterFileServer implements Runnable {

	int port;
	String tempFileDir=null;
	ExecutorService executor;

	AtomicInteger fileNum=new AtomicInteger(0);
	
	public MasterFileServer(int port, String tempFileDir) {
		this.port = port;
		this.tempFileDir=tempFileDir;
		executor = Executors.newCachedThreadPool();
	}

	@Override
	public void run() {

		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(port);
			// socket.bind(new InetSocketAddress("0.0.0.0", port));

			Socket socket = serverSocket.accept();

			final Socket subSocket = socket;
			executor.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {

					OutputStream ofs = null;
					BufferedOutputStream obfs = null;
					try {
						int chunkSize = 1024 * 1024;
						InputStream in = subSocket.getInputStream();
						DataInputStream dataInputStream = new DataInputStream(
								in);

						//接收文件名		
						int len=dataInputStream.readInt();	
						
						byte[] dst=new byte[len];						
						int readed=0;
						while(readed<len){
							in.read(dst, readed, len);
						}			
						
						String fileNameOnClient=new String(dst);
						
						long fileSize = dataInputStream.readLong();

						String fileName = getNextFileName(fileNameOnClient);
						File file = new File(tempFileDir, fileName);
						ofs = new FileOutputStream(file);
						obfs = new BufferedOutputStream(ofs);

						byte[] buf = new byte[chunkSize];
						// int cnt = 0;
						// while ((cnt = in.read(buf)) > 0) {
						// obfs.write(buf, 0, cnt);
						// }
						int cnt = 0;

						while (cnt < fileSize) {
							int len1 = in.read(buf);
							obfs.write(buf, 0, len1);

							cnt += len1;
						}

						OutputStream socketOutputStream = subSocket
								.getOutputStream();

						DataOutputStream socketDataOutputStream = new DataOutputStream(
								socketOutputStream);

						socketDataOutputStream.writeInt(fileName.length());
						socketOutputStream.write(fileName.getBytes());
					} finally {
						obfs.close();
						ofs.close();
						subSocket.close();

					}
					return null;
				}

			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getNextFileName(String fileNameOnClient) {
		
		int ind=fileNameOnClient.lastIndexOf('.');
		
		String extString=fileNameOnClient.substring(ind, fileNameOnClient.length());
		
		return fileNum.getAndIncrement()+extString;
	}
}
