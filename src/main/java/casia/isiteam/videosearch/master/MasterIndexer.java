package casia.isiteam.videosearch.master;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.xml.ws.Endpoint;

import casia.isiteam.videosearch.util.FileServer;

/**
 * 
 * @author dell
 * 
 */
public class MasterIndexer {

	private CopyOnWriteArraySet<SlaveIndexerClient> slaveIndexer=null;
	
	private SlaveRegisterService slaveRegisterService=null;
	private MasterIndexService masterIndexService=null;

	
	Configuration configuration;
	public MasterIndexer(String configFilePath) throws IOException {
		
		configuration=new Configuration(configFilePath);
		
		slaveIndexer = new CopyOnWriteArraySet<SlaveIndexerClient>();
		
		Thread thread=new Thread(new FileServer(configuration.host, configuration.fileTransferPort,configuration.tempFileDir,false));
		thread.start();
		
		//发布注册服务
		URL url=new URL("http", configuration.host, configuration.servicePort, "/"+SlaveRegisterService.class.getSimpleName());
		slaveRegisterService = new SlaveRegisterServiceImpl(this);
		Endpoint.publish(url.toString(), slaveRegisterService);
		
		
		//对外发布检索服务
		URL urlService=new URL("http",configuration.host,configuration.servicePort,"/"+MasterIndexService.class.getSimpleName());
		masterIndexService = new MasterIndexServiceImpl(this);		
		Endpoint.publish(urlService.toString(), masterIndexService);
	}

	final CopyOnWriteArraySet<SlaveIndexerClient> getSlaveIndexer() {
		return slaveIndexer;
	}
	
	
	public static void main(String [] args) throws IOException{
		if(args.length < 1){
			System.out.println("args <1");
			System.out.println("must provide configure file");
		}
		
		String configFilePath=args[0];
		
		@SuppressWarnings("unused")
		MasterIndexer masterIndexer=new MasterIndexer(configFilePath);
	}
}
