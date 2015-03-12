package casia.isiteam.videosearch.slave;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.ws.Endpoint;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import casia.isiteam.videosearch.master.SlaveRegisterService;
import casia.isiteam.videosearch.util.FileServer;


public class SlaveIndexer {
	
	String configFilePath=null;
	Configuration configuration=null;
	public SlaveIndexer(String confFilePath, String algoConfFilePath) throws IOException, URISyntaxException {
		this.configFilePath=confFilePath;	
		
		configuration = new Configuration(configFilePath);			
		
		//启动接收文件服务
		Thread thread=new Thread(new FileServer(configuration.host,configuration.fileTransferPort, configuration.tempFileDir,true));
		thread.start();
		
		//发布检索服务
		URL url=new URL("http", "0.0.0.0", configuration.servicePort, "/"+SlaveRegisterService.class.getSimpleName());
		SlaveIndexerService indexerService=new SlaveIndexerServiceImpl(configuration.vDisk, configuration.dataDir, configuration.tempFileDir, configuration.logDir, algoConfFilePath);
		Endpoint.publish(url.toString(), indexerService);		
		
		//向master注册
		registerToMaster(url.toString());
		
	}
	
	private boolean registerToMaster(String indexServiceURL) throws MalformedURLException {
		

		URL url;
		try {
			url = new URL("http",configuration.masterHost,configuration.masterPort,"/"+SlaveRegisterService.class.getSimpleName());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
		
		JaxWsProxyFactoryBean factoryBean=new JaxWsProxyFactoryBean();
		
		factoryBean.setServiceClass(SlaveRegisterService.class);
		factoryBean.setAddress(url.toString());
		
		SlaveRegisterService registerService=(SlaveRegisterService) factoryBean.create();
		
		
		registerService.registerSlave(configuration.groupName, configuration.host, configuration.servicePort, configuration.fileTransferPort);
				
		return true;
	}
	
	
	public static void main(String[] args) throws IOException, URISyntaxException{
		if(args.length < 2){
			System.out.println("args <2");
			System.out.println("must provide slave indexer and algorithm configuration file");
		}
		
		String configFilePath=args[0];
		String algoConfFilePath=args[1];
		
		@SuppressWarnings("unused")
		SlaveIndexer slaveIndexer=new SlaveIndexer(configFilePath,algoConfFilePath);			
	}
}
