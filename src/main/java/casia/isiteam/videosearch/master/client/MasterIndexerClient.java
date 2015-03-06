package casia.isiteam.videosearch.master.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import casia.isiteam.videosearch.master.MasterIndexService;

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
	private String upLoadFile(String fileName) throws IOException{
		
		int trunkSize=1024*1024;
		Socket socket=null;
		FileInputStream ifs=null;
		OutputStream ofs=null;
		DataOutputStream dataOutputStream=null;
		
		InputStream in=null;
		DataInputStream dataInputStream=null;
		try {
			socket = new Socket();
			InetSocketAddress address=new InetSocketAddress(host, fileTransferPort);
			
			socket.connect(address);
			
			File file=new File(fileName);
			ifs=new FileInputStream(file);
			ofs=socket.getOutputStream();	
			
			dataOutputStream=new DataOutputStream(ofs);
			//发送文件名，服务器端用来去后缀
			dataOutputStream.writeInt(fileName.length());
			dataOutputStream.write(fileName.getBytes());			
			
			dataOutputStream.writeLong(file.length());

			int cnt=0;
			while(cnt < file.length()){
				
				byte[] buf=new byte[trunkSize];
				int ret=ifs.read(buf);				
				dataOutputStream.write(buf);				
				cnt+=ret;				
			}
			
			//接收文件名
			in=socket.getInputStream();			
			dataInputStream=new DataInputStream(in);
			int len=dataInputStream.readInt();	
			
			byte[] dst=new byte[len];
			
			int readed=0;
			while(readed<len){
				in.read(dst, readed, len);
			}			
			String nameOnServerString=new String(dst);
			
			
			return nameOnServerString;
			
			//socket.close();
		} catch (IOException e) {

			e.printStackTrace();

			return null;
		}finally{
			
			ifs.close();
			socket.close();
		}	
	}
	public int addVideo(String fileID){
		return masterIndexService.addVideo(fileID);
	}
	public int delete(String fileID) {
		return masterIndexService.deleteVideo(fileID);		
	}
	
	public String searchVideo(String fileName){
		String nameOnServerString;
		try {
			nameOnServerString = upLoadFile(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		}
		
		return masterIndexService.searchVideo(nameOnServerString);
	}
}
