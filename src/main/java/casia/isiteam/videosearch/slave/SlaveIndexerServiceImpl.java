package casia.isiteam.videosearch.slave;

import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class SlaveIndexerServiceImpl implements SlaveIndexerService {

	IndexJNI indexJni=null; 
	String dataDir=null;	
	String tempFileDir=null;
	
	String groupName=null;
	String vDisk=null;
	
	ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); // 写优先锁

	public SlaveIndexerServiceImpl(String vDisk,String dataDir, String tempFileDir, String logDir, String algoConfPath) throws IOException {
		//super();		
		//indexJni=new IndexJNI(dataDir, logDir, algoConfPath);
		this.vDisk=vDisk;
		this.dataDir=dataDir;
		this.tempFileDir=tempFileDir;
	}
	
	/**
	 * 将带fastDFS虚拟磁盘路径的文件名 替换为实际路径文件名
	 * M00/00/00/1.txt --> vDisk/00/00/1.txt
	 * @param filePath 
	 * @return
	 */
	private String resolveFileName(String filePath){
		int ind=filePath.indexOf('/');
		String retString=vDisk+filePath.substring(ind, filePath.length());
		
		return retString;
	}
	@Override
	public int addVideo(String fileName){
		
		String filePath = resolveFileName(fileName);
		
		readWriteLock.writeLock().lock();
		//int ret = indexJni.addVideo(fileName);
		int ret=0;
		readWriteLock.writeLock().unlock();

		return ret;
	}

	@Override
	public String searchVideo(String filePath){
		
		readWriteLock.readLock().lock();
		String ret=null;
		//String ret = indexJni.searchVideo(tempFileDir+"/"+filePath);
		
		readWriteLock.readLock().unlock();
		
		return ret;
	}

	@Override
	public int deleteVideo(String fileName){
		// TODO Auto-generated method stub
		
		String filePath = resolveFileName(fileName);
		
		readWriteLock.writeLock().lock();
		//int ret = indexJni.deleteVideo(filePath);
		int ret=0;
		readWriteLock.writeLock().unlock();
		return ret;
	}

}
