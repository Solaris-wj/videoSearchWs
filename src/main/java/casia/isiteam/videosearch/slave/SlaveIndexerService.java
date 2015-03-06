package casia.isiteam.videosearch.slave;

import javax.jws.WebService;


@WebService
public interface SlaveIndexerService {
	public int addVideo(String filePath);
	public String searchVideo(String filePath);
	public int deleteVideo(String filePath);
}
