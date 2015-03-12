package casia.isiteam.videosearch.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import casia.isiteam.videosearch.master.MasterIndexService;
import casia.isiteam.videosearch.util.filetransfer.FileSender;

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
	
	Configuration configuration;
	
	public MasterIndexerClient(String confFileName) throws IOException{
		configuration=new Configuration(confFileName);
		init(configuration.host, configuration.servicePort, configuration.fileTransferPort);
	}
	public MasterIndexerClient(String host,int servicePort, int fileTransferPort) throws MalformedURLException{
		init(host, servicePort, fileTransferPort);
	}
	private void init(String host, int servicePort, int fileTransferPort) throws MalformedURLException{
		this.host=host;
		this.servicePort=servicePort;
		this.fileTransferPort=fileTransferPort;
		
		URL url=new URL("http", host, servicePort, "/"+MasterIndexService.class.getSimpleName());
		
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
	 * @throws Exception 
	 * @throws IOException
	 */
	private String upLoadFile(String fileName) throws Exception{
		
		File file=new File(fileName);
		
		return FileSender.sendFile(file, host, fileTransferPort);
		
	}
	
	/**
	 * 向视频索引中添加视频
	 * @param fileID  文件的ID，指fastDFS返回的ID，文件必须先上传到FastDFS中
	 * @return 成功返回0，失败-1
	 * @throws Exception 
	 * @throws IOException
	 */
	
	public int addVideo(String fileID) throws Exception{
		return masterIndexService.addVideo(fileID);
	}
	

	/**
	 * 删除索引中的视频
	 * @param fileID 文件ID指fastDFS返回的文件ID，此方法不会删除fastdfs中的文件
	 * @return 成功返回0，失败-1
	 * @throws Exception 
	 */
	public int delete(String fileID) throws Exception {
		return masterIndexService.deleteVideo(fileID);		
	}
	
	/**
	 * 检索视频 
	 * @param fileName 文件名，指本地的文件
	 * @return 成功返回检索结果，失败返回 “ERR”
	 * @throws Exception 
	 */
	public String searchVideo(String fileName) throws Exception{
		String nameOnServer;
		nameOnServer = upLoadFile(fileName);		
		return masterIndexService.searchVideo(nameOnServer);
	}
}
