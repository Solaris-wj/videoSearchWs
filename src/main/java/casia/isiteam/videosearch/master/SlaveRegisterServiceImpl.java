package casia.isiteam.videosearch.master;

import java.net.MalformedURLException;

import casia.isiteam.videosearch.slave.client.SlaveIndexerClient;

/**
 * slave indexer 接口实现
 * 
 * @author dell
 *
 */
class SlaveRegisterServiceImpl implements SlaveRegisterService {

	private MasterIndexer manager = null;

	public SlaveRegisterServiceImpl(MasterIndexer manager_) {
		this.manager = manager_;
	}

	@Override
	public int registerSlave(String groupName, String host, int servicePort,
			int fileTransferPort) throws MalformedURLException {
		// TODO Auto-generated method stub

		SlaveIndexerClient slaveIndexerClient = new SlaveIndexerClient( groupName, host, servicePort, fileTransferPort);

		manager.getSlaveIndexer().add(slaveIndexerClient);
		return 0;
	}


}
