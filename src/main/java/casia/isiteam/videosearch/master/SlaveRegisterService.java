package casia.isiteam.videosearch.master;


import java.net.MalformedURLException;

import javax.jws.WebService;

@WebService
public interface SlaveRegisterService {
	public int registerSlave(String groupName , String host,int servicePort, int fileTransferPort) throws MalformedURLException;
}
