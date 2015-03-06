package casia.isiteam.videosearch.slave;

import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IndexImpl {
	IndexJNIimpl indexJni = new IndexJNIimpl();

	ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); // 写锁优先

	public IndexImpl(String dataDir, String logDir, String configFilePath)
			throws IOException {
		if (this.initIndex(dataDir, logDir, configFilePath) < 0) {
			throw new IOException("init index failed");
		}
	}

	// 初始化
	private int initIndex(String dataDir, String logDir, String algoConfPath) {
		int ret = indexJni.initIndex(dataDir, logDir, algoConfPath);
		return ret;
	}

	public int addVideo(String fileID) {

		readWriteLock.writeLock().lock();
		int ret = indexJni.addVideo(fileID);
		readWriteLock.writeLock().unlock();

		return ret;
	}

	public String searchVideo(String filePath) {
		readWriteLock.readLock().lock();

		String ret = indexJni.searchVideo(filePath);
		readWriteLock.readLock().unlock();

		return ret;

	}

	public int deleteVideo(String fileID) {

		readWriteLock.writeLock().lock();

		int ret = indexJni.deleteVideo(fileID);

		readWriteLock.writeLock().unlock();

		return ret;
	}

}

class IndexJNIimpl {

	public native int initIndex(String dataDir, String logDir,
			String algoConfPath);

	public native int addVideo(String filePath);

	public native String searchVideo(String fileID);

	public native int deleteVideo(String fileID);
}
