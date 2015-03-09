package casia.isiteam.videosearch.master.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import casia.isiteam.videosearch.master.MasterIndexService;
import casia.isiteam.videosearch.util.FileSender;

/**
 * master的客户端代理，此类所有方法均在客户端处运行
 * @author dell
 *
 */
public class MasterIndexerClient {
	String host;
	int servicePort;
	int fileTransferPort;
	MasterIndexService masterIndexService;
	
	public MasterIndexerClient(String host,int servicePort, int fileTransferPort) throws MalformedURLException{
		this.host=host;
		this.servicePort=servicePort;
		this.fileTransferPort=fileTransferPort;
		
		URL url=new URL("http", host, servicePort, MasterIndexService.class.getSimpleName());
		
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		// factory.
		factory.setServiceClass(MasterIndexService.class);
		factory.setAddress(url.toString());
		this.masterIndexService = (MasterIndexService) factory
				.create();
		
	}
	/**
	 * 上传文件到master处，文件名指本地文件名，服务器端的文件名由服务器产生并且返回
	 * @param fileName
	 * @return 返回服务器处的文件名
	 * @throws IOException
	 */
	private String upLoadFile(String fileName){
		
		File file=new File(fileName);
		
		return FileSender.sendFile(file, host, fileTransferPort);
		
	}
	public int addVideo(String fileID){
		return masterIndexService.addVideo(fileID);
	}
	public int delete(String fileID) {
		return masterIndexService.deleteVideo(fileID);		
	}
	
	public String searchVideo(String fileName){
		String nameOnServerString;
		nameOnServerString = upLoadFile(fileName);
		
		return masterIndexService.searchVideo(nameOnServerString);
	}
}
