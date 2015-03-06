package casia.isiteam.videosearch.slave;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
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

public class SlaveFileServer implements Runnable {

	int port;
	String tempFileDir = null;
	ExecutorService executor;

	public SlaveFileServer(int port, String tempFileDir) {
		this.port = port;
		this.tempFileDir = tempFileDir;

		executor = Executors.newCachedThreadPool();
	}

	@Override
	public void run() {

		try {
			
			ServerSocket serverSocket = new ServerSocket(port);

			Socket socket = serverSocket.accept();

			final Socket subSocket = socket;
			executor.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {

					int chunkSize = 1024 * 1024;

					InputStream in = null;
					DataInputStream dataInputStream = null;

					OutputStream out = null;
					BufferedOutputStream bufferedOut = null;

					try {
						in = subSocket.getInputStream();
						dataInputStream = new DataInputStream(in);
						int len = dataInputStream.readInt();

						byte[] dst = new byte[len];

						int readed = 0;
						while (readed < len) {
							in.read(dst, readed, len);
						}
						String fileName = new String(dst);

						File file = new File(tempFileDir, fileName);

						out = new FileOutputStream(file);
						bufferedOut = new BufferedOutputStream(out);

						int cnt = 0;
						byte[] buf = new byte[chunkSize];

						while ((cnt = in.read(buf)) > 0) {
							bufferedOut.write(buf, 0, cnt);
						}
					}catch(Exception e){
						throw e;
					}
					finally {
						bufferedOut.close();
						out.close();
						dataInputStream.close();
						in.close();
						subSocket.close();
					}

					return null;
				}

			});

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
