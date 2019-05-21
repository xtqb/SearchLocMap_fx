package com.lhzw.uploadmms;


//Declare any non-default types here with import statements

import java.util.List;

import com.lhzw.uploadmms.UploadInfoBean;

interface BDUploadEvent {
	void doTask(in List<UploadInfoBean> list);
	void setUploadState(in int state);
	void stopTask();
	void continueTask();
	void reuestData(in double lat, in double lon);
	void setNum(in int tx_type, in String num);
}
