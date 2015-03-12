package casia.isiteam.videosearch.test;

import java.io.IOException;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import casia.isiteam.fastdfs.client.FastDFSClient;
import casia.isiteam.videosearch.client.MasterIndexerClient;

public class Test {
	
	public static void main(String[] args) throws Exception{
		
		if(args.length<2){
			System.out.println("args[0]=fdfs_client.conf, args[1]=indexer_client.conf");
			return;
		}
		
		FastDFSClient fastDFSClient=new FastDFSClient(args[0]);
		MasterIndexerClient masterIndexerClient;
		try {
			masterIndexerClient=new MasterIndexerClient(args[1]);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		String localFileName="C:/Users/dell/Downloads/ChunkedWriteHandlerTest.java";
		
		String fileID=fastDFSClient.upLoadFile(localFileName);
		
		System.out.println(masterIndexerClient.addVideo(fileID));
		System.out.println(masterIndexerClient.delete(fileID));
		System.out.println(masterIndexerClient.searchVideo(localFileName));
	}
}
