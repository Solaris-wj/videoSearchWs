package casia.isiteam.videosearch.slave;

import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class SlaveIndexerServiceImpl implements SlaveIndexerService {

	IndexJNIimpl indexJni = new IndexJNIimpl();
	String dataDir=null;	
	ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); // 写优先锁

	public SlaveIndexerServiceImpl(String dataDir, String logDir, String algoConfPath) throws IOException {
		super();		
		
		this.dataDir=dataDir;
		if (indexJni.initIndex(dataDir, logDir, algoConfPath) < 0) {
			throw new IOException("init index failed");
		}		
	}
	
	public int addVideo(String fileId){
		// TODO Auto-generated method stub		
		
		String filePath = getFilePath(fileId);
		
		readWriteLock.writeLock().lock();
		int ret = indexJni.addVideo(filePath);
		readWriteLock.writeLock().unlock();

		return ret;
	}

	public String searchVideo(String filePath){
		// TODO Auto-generated method stub
		
		readWriteLock.readLock().lock();
		String ret = indexJni.searchVideo(filePath);
		readWriteLock.readLock().unlock();
		
		return ret;
	}

	public int deleteVideo(String fileId){
		// TODO Auto-generated method stub
		
		String filePath = getFilePath(fileId);
		
		readWriteLock.writeLock().lock();
		int ret = indexJni.deleteVideo(filePath);
		readWriteLock.writeLock().unlock();
		return ret;
	}
	
	private String getFilePath (String fileId) {
		
		int ind=fileId.indexOf('/',fileId.indexOf('/')+1);
	
		return fileId.substring(ind+1);
	}

}
