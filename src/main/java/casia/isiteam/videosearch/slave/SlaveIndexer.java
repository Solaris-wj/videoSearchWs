package casia.isiteam.videosearch.slave;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.ws.Endpoint;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import casia.isiteam.videosearch.master.SlaveRegisterService;


public class SlaveIndexer {
	private IndexImpl indexImpl=null;

	String tempFileDir=null;
	String dataDir=null;
	String logDir=null;
	String configFilePath=null;
	String algoConfFilePath=null;
	
	String masterHost=null;
	int masterPort;
	
	String localhost=null;
	int localPort;
	Configuration configuration=null;
	public SlaveIndexer(String confFilePath, String algoConfFilePath) throws IOException, URISyntaxException {
		this.configFilePath=confFilePath;	
		this.algoConfFilePath=algoConfFilePath;
		
		configuration = new Configuration(configFilePath,this);			
		indexImpl=new IndexImpl(dataDir,logDir,configFilePath);		
		SlaveIndexerService indexerService=new SlaveIndexerServiceImpl(dataDir, logDir, algoConfFilePath);
		
		URL url=new URL("http", "0.0.0.0", localPort, SlaveRegisterService.class.getSimpleName());
		Endpoint.publish(url.toString(), indexerService);		
		
		registerToMaster(url.toString());
		
	}
	
	private boolean registerToMaster(String indexServiceURL) {
		

		URL url;
		try {
			url = new URL("http",masterHost,masterPort,SlaveRegisterService.class.getName());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
		
		JaxWsProxyFactoryBean factoryBean=new JaxWsProxyFactoryBean();
		
		factoryBean.setServiceClass(SlaveRegisterService.class);
		factoryBean.setAddress(url.toString());
		SlaveRegisterService registerService=(SlaveRegisterService) factoryBean.create();
		
		
		registerService.registerSlave(indexServiceURL);
		
		return true;
	}
	
	public IndexImpl getInderJNI() {
		return indexImpl;
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException{
		if(args.length < 1){
			System.out.println("args <1");
			System.out.println("must provide configure file path");
		}
		
		String configFilePathString=args[0];
		@SuppressWarnings("unused")
		SlaveIndexer slaveIndexer=new SlaveIndexer(configFilePathString, null);			
	}
}
