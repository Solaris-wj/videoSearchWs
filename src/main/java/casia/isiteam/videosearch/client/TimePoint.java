package casia.isiteam.videosearch.client;

import java.io.Serializable;

public class TimePoint implements Serializable{
	public int getDet_beg() {
		return det_beg;
	}
	public void setDet_beg(int det_beg) {
		this.det_beg = det_beg;
	}
	public int getDet_end() {
		return det_end;
	}
	public void setDet_end(int det_end) {
		this.det_end = det_end;
	}
	public int getTar_beg() {
		return tar_beg;
	}
	public void setTar_beg(int tar_beg) {
		this.tar_beg = tar_beg;
	}
	public int getTar_end() {
		return tar_end;
	}
	public void setTar_end(int tar_end) {
		this.tar_end = tar_end;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3816155474693227417L;
	int det_beg;
	int det_end;
	int tar_beg;
	int tar_end;
	
	
}
