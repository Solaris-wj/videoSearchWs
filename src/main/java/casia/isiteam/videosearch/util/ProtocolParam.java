package casia.isiteam.videosearch.util;

import java.util.regex.Pattern;

public interface ProtocolParam {
	public final static String idleHandlerName="idleHander";
	public final static int idleReadTimeInSeconds=60;
	public final static int idleWriteTimeInSeconds=30;
	public final static Pattern seperator=Pattern.compile("\n");
}
