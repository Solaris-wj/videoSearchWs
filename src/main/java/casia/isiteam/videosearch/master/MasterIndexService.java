package casia.isiteam.videosearch.master;

import javax.jws.WebService;


@WebService
public interface MasterIndexService{

	public int addVideo(String fileID);

	public String searchVideo(String fileName);

	public int deleteVideo(String fileID);
}
