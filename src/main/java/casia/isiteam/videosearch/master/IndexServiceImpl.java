package casia.isiteam.videosearch.master;


import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import casia.isiteam.videosearch.slave.SlaveIndexerService;


public class IndexServiceImpl implements IndexService{

	
	ExecutorService executor=null; 

	SlaveManager slaveManager=null;
	public IndexServiceImpl(SlaveManager slaveManager) {
		// TODO Auto-generated constructor stub
		this.slaveManager=slaveManager;
		executor=Executors.newCachedThreadPool();
		
		
	}
	
	public int addVideo(final String fileId) {
		// TODO Auto-generated method stub
		
		SlaveIndexerService[] indexers=(SlaveIndexerService[]) slaveManager.getSlaveIndexer().toArray();
		
		ArrayList<Future<Integer>> results=new ArrayList<Future<Integer>>();
		//final String l_arg=arg;
		for(final SlaveIndexerService index:indexers){			
		
			Future<Integer> future=executor.submit(new Callable<Integer>() {
				public Integer call() throws Exception {

					return index.addVideo(fileId);
				}
			});
			
			results.add(future);
		}
				
		for(Future<Integer> ret:results){
			try {
				if(ret.get()==0)
					return 0;
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}
		}
		
		return -1;
	}

	public String searchVideo(String fileId) {
		// TODO Auto-generated method stub
		
		
		return null;
	}
	
	public InetAddress getUpLoadSock(){
		ServerSocket socket;
		socket.
	}

	public int deleteVideo(final String fileId) {
		// TODO Auto-generated method stub
		
		SlaveIndexerService [] indexers=(SlaveIndexerService[]) slaveManager.getSlaveIndexer().toArray();
		
		ArrayList<Future<Integer>> results=new ArrayList<Future<Integer>>();
		//final String l_arg=arg;
		for(final SlaveIndexerService index:indexers){			
		
			Future<Integer> future=executor.submit(new Callable<Integer>() {
				public Integer call() throws Exception {

					return index.addVideo(fileId);
				}
			});
			
			results.add(future);
		}
		
		for(Future<Integer> ret:results){
			try {
				if(ret.get()==0)
					return 0;
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}
		}
		
		return -1;
	}
	
	
	
	
	/*
	 * private <T> ArrayList<Future<T>> doTheJob( final Method method, final String arg){

		SlaveIndexerService[] indexers=(SlaveIndexerService[]) slaveManager.getSlaveIndexer().toArray();
		
		ArrayList<Future<T>> results=new ArrayList<Future<T>>();
		//final String l_arg=arg;
		for(final SlaveIndexerService index:indexers){			
		
			Future<T> future=executor.submit(new Callable<T>() {
				public T call() throws Exception {

					return (T) method.invoke(index, arg);
				}
			});
			
			results.add(future);
		}
		return results;
	}*/
	
	
}

