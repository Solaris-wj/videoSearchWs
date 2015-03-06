package casia.isiteam.videosearch.master;

import java.net.MalformedURLException;



public class MasterIndexer {

	String host="0.0.0.0";
	int port=9100;
	SlaveManager slaveManager;
	
	
	public MasterIndexer() throws MalformedURLException{
		//准备接受 slave index 注册，报告信息等
		slaveManager = new SlaveManager(host,port);	
	}

	
	public static void main(String[] args){
		//MasterIndexer masterIndexer=new MasterIndexer();
	}
}
