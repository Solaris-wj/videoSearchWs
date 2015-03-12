package casia.isiteam.fastdfs.client;

import java.io.File;
import java.io.IOException;

import org.csource.common.MyException;
import casia.isiteam.fastdfs.client.FastDFSClient;

/**
 * @author dell
 *
 */
public class FastDFSClientTest {

	FastDFSClient fastDFSclient;
	String localFileName=null;
	String remoteFileName=null;
	
	public FastDFSClientTest() throws IOException {
		// TODO Auto-generated constructor stub
		fastDFSclient=new FastDFSClient(new File("").getCanonicalPath()+"/src/main/resources/fdfs_client.conf");

	}
	
	public void testUpLoadFile() throws IOException, MyException {
		

		localFileName=new File("").getCanonicalPath()+"/src/main/java/" + this.getClass().getCanonicalName().replace('.', '/')+".java";
		remoteFileName=fastDFSclient.upLoadFile(localFileName);
		
		String localFileName1=localFileName+"down";
		fastDFSclient.download_file1(remoteFileName,localFileName1);
		
		System.out.println(remoteFileName);
		
	}	
	
	public void testDelete_file1() throws IOException, MyException {
		System.out.println(this.remoteFileName);
		fastDFSclient.delete_file1("group1/M00/00/00/n-K2tlT9i_aANk56AAAFQXE-Uyw03.java");
	}

	public static void main(String [] args) throws IOException, MyException{
		FastDFSClientTest fastDFSClientTest=new FastDFSClientTest();
		fastDFSClientTest.testUpLoadFile();
		fastDFSClientTest.testDelete_file1();
	}
}

