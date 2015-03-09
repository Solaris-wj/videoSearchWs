/**
 * 
 */
package casia.isiteam.videoSearch;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.csource.common.MyException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

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
	

	@Test
	public void testUpLoadFile() throws IOException, MyException {
		

		localFileName=new File("").getCanonicalPath()+"/src/test/java/" + this.getClass().getCanonicalName().replace('.', '/')+".java";
		remoteFileName=fastDFSclient.upLoadFile(localFileName);
		
		String localFileName1=localFileName+"down";
		fastDFSclient.download_file1(remoteFileName,localFileName1);
		
		System.out.println(remoteFileName);
	}


	//@Ignore
	@Test
	public void testDelete_file1() {
		System.out.println(this.remoteFileName);
		//fail("Not yet implemented");
	}

}
