package casia.isiteam.videosearch.master;

import javax.jws.WebService;


@WebService
public interface IndexService{

	public int addVideo(String fileId);

	public String searchVideo(String fileId);

	public int deleteVideo(String fileId);
}
