package casia.isiteam.videosearch.client;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.rmi.Naming;

import casia.isiteam.videosearch.master.SlaveRegisterService;

public class SearchClient {
	String fdfs_client_config_path = "";
	
	
	public SearchClient(String indexHost, int port){
//		try {
//			ClientGlobal.init(fdfs_client_config_path);
//		} catch (IOException | MyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		URI uri= new URI("rmi",null,indexHost,port,"/SlaveRegisterService",null,null);
		
		SlaveRegisterService service=(SlaveRegisterService)Naming.lookup(uri.toString());
		

	}
	
	public String uploadToFdfs(String filePath){

		String fileId=null;
		try {
			TrackerClient tracker = new TrackerClient();
			TrackerServer trackerServer = tracker.getConnection();
			StorageServer storageServer = null;
			StorageClient1 client = new StorageClient1(trackerServer,
					storageServer);
			fileId = client.upload_file1(filePath, null, null);
		} catch (IOException  | MyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return fileId;
	}
	
	public int addVideo(String fileId) throws IOException{
		
		//String fileId = this.uploadToFdfs(filePath);
		
		ServerSocket svrSocket=new ServerSocket();
		InetAddress address = svrSocket.getInetAddress();
		
		addVideo
		
		return 0;
	}

	public String searchVideo(String fileId){
		//String 
	}

	public int deleteVideo(String fileId){
		
	}
}
