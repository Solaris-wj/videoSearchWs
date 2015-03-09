
package casia.isiteam.videosearch.master;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import casia.isiteam.videosearch.client.TimePoint;
import casia.isiteam.videosearch.slave.client.SlaveIndexerClient;
import casia.isiteam.videosearch.util.Util;


public class MasterIndexServiceImpl implements MasterIndexService{	
	

	MasterIndexer masterIndexer=null;
	public MasterIndexServiceImpl(MasterIndexer slaveManager) {
		this.masterIndexer=slaveManager;
				
		
	}
	public static String[] getFileGroupAndName(String fileID){
		int ind=fileID.indexOf('/');
		
		String []ret=new String[2];
		ret[0]=fileID.substring(0,ind);
		ret[1]=fileID.substring(ind+1,fileID.length());
		
		return ret;
	}
	
	@Override
	public int addVideo(final String fileID) {
		
		String [] retStrings=getFileGroupAndName(fileID);

		for(SlaveIndexerClient slaveIndexerClient:masterIndexer.getSlaveIndexer()){
			if(retStrings[0].equals(slaveIndexerClient.getGroupName())){
				return slaveIndexerClient.addVideo(retStrings[1]);
			}
		}
		
		return -1;
	}

	@Override
	public String searchVideo(String fileName) {
		
		final String l_fileNameString=fileName;
		ExecutorService executor= Executors.newCachedThreadPool();
		
		ArrayList<Future<String>> retsArrayList=new ArrayList<Future<String>>();
		for(final SlaveIndexerClient slaveIndexerClient:masterIndexer.getSlaveIndexer()){
			
			Future<String> retFuture=executor.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					
					slaveIndexerClient.searchVideo(l_fileNameString);				
					
					return null;
				}
				
			});
			retsArrayList.add(retFuture);
		}
		
		Map<String, List<TimePoint>> results=new HashMap<String, List<TimePoint>>();
		//合并检索结果
		for(Future<String> retFuture:retsArrayList){
			String text;
			try {
				text = retFuture.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return Util.errCodeString;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return Util.errCodeString;
			}
			if(text==null){
				continue;
			}
			Map<String, List<TimePoint>>  retMap;
			retMap=JSON.parseObject(text,new TypeReference<Map<String, List<TimePoint>>>(){});
			
			results.putAll(retMap);
			
		}
		
		if(results.size()==0){
			return null;
		}

		String jsonString=JSON.toJSONString(results,true);
		
		return jsonString;
	}
	


	@Override
	public int deleteVideo(final String fileID) {
		
		String [] retStrings=getFileGroupAndName(fileID);

		for(SlaveIndexerClient slaveIndexerClient:masterIndexer.getSlaveIndexer()){
			if(retStrings[0].equals(slaveIndexerClient.getGroupName())){
				return slaveIndexerClient.deleteVideo(retStrings[1]);
			}
		}		
		return -1;
	}	
}

