package casia.isiteam.videosearch.slave;

import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class SlaveIndexerServiceImpl implements SlaveIndexerService {

	IndexJNI indexJni=null; 
	String dataDir=null;	
	String tempFileDir=null;
	ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); // 写优先锁

	public SlaveIndexerServiceImpl(String dataDir, String tempFileDir, String logDir, String algoConfPath) throws IOException {
		super();		
		indexJni=new IndexJNI(dataDir, logDir, algoConfPath);
		this.dataDir=dataDir;
		this.tempFileDir=tempFileDir;
	}
	
	@Override
	public int addVideo(String fileName){
		
		//String filePath = getFilePath(fileId);
		
		readWriteLock.writeLock().lock();
		int ret = indexJni.addVideo(fileName);
		readWriteLock.writeLock().unlock();

		return ret;
	}

	@Override
	public String searchVideo(String filePath){
		
		readWriteLock.readLock().lock();
		String ret="hello";
		//String ret = indexJni.searchVideo(tempFileDir+"/"+filePath);
		readWriteLock.readLock().unlock();
		
		return ret;
	}

	@Override
	public int deleteVideo(String fileName){
		// TODO Auto-generated method stub
		
		//String filePath = getFilePath(fileId);
		
		readWriteLock.writeLock().lock();
		int ret = indexJni.deleteVideo(fileName);
		readWriteLock.writeLock().unlock();
		return ret;
	}

}
