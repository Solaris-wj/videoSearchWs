package casia.isiteam.videosearch.client;


import java.io.IOException;
import java.net.MalformedURLException;
import casia.isiteam.videosearch.master.client.MasterIndexerClient;

public class SearchClient {
		
	String serverHost;
	int servicePort;
	int fileTransferPort;
	
	MasterIndexerClient masterIndexerClient=null;
	
	public SearchClient(String serverHost, int servicePort, int fileTransferPort) throws MalformedURLException{
		
		masterIndexerClient=new MasterIndexerClient(serverHost, servicePort, fileTransferPort);

	}
		
	public int addVideo(String fileID) throws IOException{
		return masterIndexerClient.addVideo(fileID);		
	}

	public String searchVideo(String fileName){
		return masterIndexerClient.searchVideo(fileName);
	}

	public int deleteVideo(String fileID){
		return masterIndexerClient.delete(fileID);
	}
}
