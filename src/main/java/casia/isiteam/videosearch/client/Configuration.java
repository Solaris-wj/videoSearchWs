package casia.isiteam.videosearch.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	
	String host=null;
	int servicePort;
	int fileTransferPort;
		
	
	private Properties properties=new Properties();
	public Configuration(String configFilePath) throws IOException {
		InputStream in = new FileInputStream(configFilePath);
		properties.load(in);


		host = properties.getProperty("host");
		if (host == null || host.length() == 0) {
			host = "0.0.0.0";
			System.err.println("default host is \"0.0.0.0\"");
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
	}
}
