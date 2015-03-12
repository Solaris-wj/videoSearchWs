package casia.isiteam.videosearch.master;

import javax.jws.WebService;


@WebService
public interface MasterIndexService{

	public int addVideo(String fileID) throws Exception;

	public String searchVideo(String fileName) throws Exception;

	public int deleteVideo(String fileID) throws Exception;
}
