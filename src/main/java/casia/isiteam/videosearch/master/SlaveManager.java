package casia.isiteam.videosearch.master;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArraySet;


import javax.xml.ws.Endpoint;

import casia.isiteam.videosearch.slave.SlaveIndexerService;

/**
 * 
 * @author dell
 * 
 */
public class SlaveManager {

	//private ReadWriteLock slaveIndexerMapLock;
	//private Set<SlaveIndexerService> slaveIndexer;
	
	private CopyOnWriteArraySet<SlaveIndexerService> slaveIndexer;
	
	private SlaveRegisterService slaveRegisterService;
	private IndexService indexServiceImpl=null;
	private String host;
	private int registerPort;

	public SlaveManager(String host, int port) throws MalformedURLException {
		this.host=host;
		this.registerPort=port;
		
		//slaveIndexerMapLock = new ReentrantReadWriteLock();
		slaveIndexer = new CopyOnWriteArraySet<SlaveIndexerService>();
		slaveRegisterService = new SlaveRegisterServiceImpl(this);
		
		URL url=new URL("http", host, registerPort, SlaveRegisterService.class.getSimpleName());
		Endpoint.publish(url.toString(), slaveRegisterService);
		
		
		//对外发布检索服务
		URL urlService=new URL("http",host,port,IndexService.class.getSimpleName());
		indexServiceImpl = new IndexServiceImpl(this);		
		Endpoint.publish(urlService.toString(), indexServiceImpl);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getRegisterPort() {
		return registerPort;
	}

	public void setRegisterPort(int registerPort) {
		this.registerPort = registerPort;
	}
	
	//final ReadWriteLock getSlaveIndexerLock() {
	//	return slaveIndexerMapLock;
	//}

	final CopyOnWriteArraySet<SlaveIndexerService> getSlaveIndexer() {
		return slaveIndexer;
	}
}
