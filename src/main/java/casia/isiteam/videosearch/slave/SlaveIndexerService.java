package casia.isiteam.videosearch.slave;

import javax.jws.WebService;

@WebService
public interface SlaveIndexerService {
	public int addVideo(String fileName);
	public String searchVideo(String fileName);
	public int deleteVideo(String fileName);
}
