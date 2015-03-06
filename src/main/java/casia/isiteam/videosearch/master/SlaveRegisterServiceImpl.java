package casia.isiteam.videosearch.master;


import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import casia.isiteam.videosearch.slave.SlaveIndexerService;

/**
 * slave indexer 接口实现
 * @author dell
 *
 */
class SlaveRegisterServiceImpl implements SlaveRegisterService {
	
	private SlaveManager manager=null;

	public SlaveRegisterServiceImpl(SlaveManager manager_){
		this.manager = manager_;
	}

	public int registerSlave(String url) {
				
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		// factory.
		factory.setServiceClass(SlaveIndexerService.class);
		factory.setAddress(url);
		SlaveIndexerService slave = (SlaveIndexerService) factory.create();
		
		//manager.getSlaveIndexerLock().writeLock().lock();
		manager.getSlaveIndexer().add(slave);
		//manager.getSlaveIndexerLock().writeLock().unlock();
		return 0;
	}

}
