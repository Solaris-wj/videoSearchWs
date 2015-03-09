package casia.isiteam.fastdfs.client;

import java.io.IOException;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class FastDFSClient {

	TrackerClient trackerClient;
	TrackerServer trackerServer;
	public FastDFSClient(String conf_filename) {
		try {
			ClientGlobal.init(conf_filename);


			trackerClient = new TrackerClient();
			trackerServer=trackerClient.getConnection();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String upLoadFile(String localFileName){
		try {
			return getUploadStorage().upload_file1(localFileName,null,null);
		} catch (IOException | MyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	* download file from storage server
	* @param file_id the file id(including group name and filename)
	* @param local_filename  the filename on local
	* @return 0 success, return none zero errno if fail
	*/
	public int download_file1(String fileID, String local_filename) throws IOException, MyException
	{
		return getDownloadStorage(fileID).download_file1(fileID, local_filename);
	}
	
	
	/**
	* delete file from storage server
	* @param file_id the file id(including group name and filename)
	* @return 0 for success, none zero for fail (error code)
	*/
	public int delete_file1(String fileID) throws IOException, MyException
	{		
		return getDownloadStorage(fileID).delete_file1(fileID);
	}
	
	private StorageClient1 getDownloadStorage(String fileId) throws IOException{
	
		try {			
			StorageServer storageServer=trackerClient.getFetchStorage1(trackerServer, fileId);
			StorageClient1 client = new StorageClient1(trackerServer, storageServer);
			
			return client;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
		
	}
	private StorageClient1 getUploadStorage() throws IOException{
		try {		
			StorageClient1 client = new StorageClient1(trackerServer,
					null);
			return client;
		}finally{
			//trackerServer.close();
		}

	}
}
