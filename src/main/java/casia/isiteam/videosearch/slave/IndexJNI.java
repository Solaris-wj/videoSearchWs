package casia.isiteam.videosearch.slave;

import java.io.IOException;


public class IndexJNI {

	public IndexJNI(String dataDir, String logDir, String algoConfPath)
			throws IOException {
		if (this.initIndex(dataDir, logDir, algoConfPath) < 0) {
			throw new IOException("init index failed");
		}
	}
	public native int initIndex(String dataDir, String logDir,
			String algoConfPath);

	public native int addVideo(String filePath);

	public native String searchVideo(String fileID);

	public native int deleteVideo(String fileID);
}
