package casia.isiteam.videosearch.slave;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class Configuration {
	
	String groupName=null;
	
	String masterHost=null;
	int masterPort;
	
	String host=null;
	int servicePort;
	int fileTransferPort;
	
	String tempFileDir=null;
	String dataDir=null;
	String logDir=null;

	String algoConfFilePath=null;
	

	
	
	private Properties properties;
	public Configuration(String configFilePath) throws IOException {
		InputStream in = new FileInputStream(configFilePath);
		properties.load(in);

		groupName=properties.getProperty("groupName");
		if(groupName==null || groupName.length()==0){
			throw new IOException("groupName can not be null");
		}
		
		masterHost = properties.getProperty("masterHost");
		if (masterHost == null || masterHost.length() == 0) {
			throw new IOException("masterHost can not be null");
		}

		try {
			masterPort = Integer.parseInt(properties
					.getProperty("registerPort"));
		} catch (NumberFormatException e) {
			throw new IOException("registerPort can not be null");
			//masterPort = 800100;
		}

		host = properties.getProperty("localhost");
		if (host == null || host.length() == 0) {
			host = "0.0.0.0";
			System.err.println("default localhost is \"0.0.0.0\"");
		}

		
		try {
			servicePort = Integer.parseInt(properties
					.getProperty("servicePort"));
		} catch (NumberFormatException e) {
			throw new IOException("servicePort can not be null");
		}

		
		try {
			fileTransferPort = Integer.parseInt(properties
					.getProperty("fileTransferPort"));
		} catch (NumberFormatException e) {
			throw new IOException("fileTransferPort can not be null");
		}
		
		
		tempFileDir = properties.getProperty("tempFileDir");
		if (tempFileDir == null || tempFileDir.length() == 0) {
			throw new IOException("tempFileDir can not be null!");
		}
		if (new File(tempFileDir).exists() == false) {
			throw new IOException("temporary file directory doesn't exists");
		}
		
		
		dataDir = properties.getProperty("dataDir");
		if (dataDir == null || dataDir.length() == 0) {
			throw new IOException("dataDir can not be null!");
		}
		if (new File(dataDir).exists() == false) {
			throw new IOException("dataDir file directory doesn't exists!");
		}
		
		
		logDir = properties.getProperty("logDir");
		if (logDir == null || logDir.length() == 0) {
			throw new IOException("logDir can not be null!");
		}
		if (new File(logDir).exists() == false) {
			throw new IOException("logDir file directory doesn't exists!");
		}
		
		
		algoConfFilePath=properties.getProperty("algoConfFilePath");
		if(algoConfFilePath==null || algoConfFilePath.length()==0){
			throw new IOException("algoConfFilePath can not be empty!");
		}
		if(new File(algoConfFilePath).exists()==false){
			throw new IOException("algoConfFilePath file directory doesn't exists");
		}		
		



	}

}
