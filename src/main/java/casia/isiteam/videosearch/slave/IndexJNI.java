package casia.isiteam.videosearch.slave;

import java.io.IOException;


public class IndexJNI {

	public IndexJNI(String dataDir, String logDir, String algoConfPath)
			throws IOException {
		try {
			if (this.initIndex(dataDir, logDir, algoConfPath) < 0) {
				throw new IOException("init index failed");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	public native int initIndex(String dataDir, String logDir,
			String algoConfPath);

	public native int addVideo(String filePath);

	public native String searchVideo(String filePath);

	public native int deleteVideo(String filePath);
}
