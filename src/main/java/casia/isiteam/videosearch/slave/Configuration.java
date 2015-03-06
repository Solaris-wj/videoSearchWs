package casia.isiteam.videosearch.slave;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class Configuration {
	private Properties properties;
	public Configuration(String configFilePath, SlaveIndexer indexer) throws IOException {
		InputStream in = new FileInputStream(configFilePath);
		properties.load(in);

		indexer.masterHost = properties.getProperty("masterHost");
		if (indexer.masterHost == null || indexer.masterHost.length() == 0) {
			throw new IOException("masterHost can not be null");
		}

		try {
			indexer.masterPort = Integer.parseInt(properties
					.getProperty("registerPort"));
		} catch (NumberFormatException e) {
			// throw new IOException("registerPort can not be null");
			indexer.masterPort = 800100;
		}

		indexer.localhost = properties.getProperty("localhost");
		if (indexer.localhost == null || indexer.localhost.length() == 0) {
			indexer.localhost = "0.0.0.0";
		}

		try {
			indexer.localPort = Integer.parseInt(properties
					.getProperty("servicePort"));
		} catch (NumberFormatException e) {
			// throw new IOException("servicePort can not be null");
			indexer.localPort = 900100;
		}

		indexer.tempFileDir = properties.getProperty("tempFileDir");
		if (indexer.tempFileDir == null || indexer.tempFileDir.length() == 0) {
			throw new IOException("tempFileDir can not be empty!");
		}
		if (new File(indexer.tempFileDir).exists() == false) {
			throw new IOException("temporary file directory doesn't exists");
		}

		indexer.dataDir = properties.getProperty("dataDir");
		if (indexer.dataDir == null || indexer.dataDir.length() == 0) {
			throw new IOException("dataDir can not be empty!");
		}
		if (new File(indexer.dataDir).exists() == false) {
			throw new IOException("dataDir file directory doesn't exists");
		}

		indexer.logDir = properties.getProperty("logDir");
		if (indexer.logDir == null || indexer.logDir.length() == 0) {
			throw new IOException("logDir can not be empty!");
		}
		if (new File(indexer.logDir).exists() == false) {
			throw new IOException("logDir file directory doesn't exists");
		}

	}

}
