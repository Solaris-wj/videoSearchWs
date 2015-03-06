package casia.isiteam.videosearch.master;


import javax.jws.WebService;

@WebService
public interface SlaveRegisterService {
	public int registerSlave(String url);
}
