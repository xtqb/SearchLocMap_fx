package com.lhzw.searchlocmap.constants;

import android.os.Environment;

public class Constants {
	public static final String DB_NAME = "DB_NAME.db";
	public static final int FAILED = -1;
	public static final int SUCCEED = 0;
	public static final String TAG_ADDRESS = "address";
	// 保存正在下载的城市的状态值
	public static boolean isOk;// 是否退出程序
	public static String SP_NAME = "SP_NAME";

	// 北京经纬度

	// public static float CENTRE_LON = 120.19f;// 北京市区 经度
	// public static float CENTRE_LAT = 30.26f; // 北京市区 纬度
	public static float CENTRE_LON = 116.37334376485391f;// 北京市区 经度
	public static float CENTRE_LAT = 39.98860655012614f; // 北京市区 纬度
//	public static float CENTRE_LON = 127.2628f;// 黑河 经度
//	public static float CENTRE_LAT = 50.1535f; // 黑河 纬度
//	 public static float CENTRE_LON = 89.19f;// 北京市区 经度
//	 public static float CENTRE_LAT = 42.91f; // 北京市区 纬度
	public static int RADIUS = 800; // 默认安全半径

	public static String SIGNAL_WATCH = "com.lhzw.searchlocmap.receiver.WatchSignalReceiver";

	public static String PERSON_UNDETERMINED = "-1"; // 待定
	public static String PERSON_OFFLINE = "0";// 离线
	public static String PERSON_COMMON = "1"; // 普通
	public static String PERSON_SOS = "2"; // sos
	public static String PIC_GROUP_SOS = "PIC_GROUP_SOS";
	public static String PIC_GROUP_COMMON = "PIC_GROUP_COMMON";
	public static String CIRCLE_GROUP = "CIRCLE_GROUP";
	public static String LINE_GROUP = "LINE_GROUP";
	public static String PIC_CENTER = "PIC_CENTER";
	public static String CIRCLE_CENTER = "CIRCLE_CENTER";
	public static final String BD_Mms_ACTION = "android.intent.action.SEARCH_MMS_RECEIVER";
	public static final String BD_SIGNAL_LIST = "android.intent.action.BD_SIGNAL_LIST";
	public static final String BD_HISTREE_LIST = "android.intent.action.BD_HISTREE_LIST";
	public static final String BD_SIG_ACTION = "android.intent.action.dipper_signal";
	public static final String ACTION_FEEDBACK = "com.lhzw.action.feedback";
	public static final String SIG_RET = "strSignalInfoChanged";
	public static final String SIG_RET_INFO_0 = "SignalInfo0";
	public static final String SIG_RET_INFO_1 = "SignalInfo1";
	public static final String SIG_RET_INFO_2 = "SignalInfo2";
	public static final String SIG_RET_INFO_3 = "SignalInfo3";
	public static final String SIG_RET_INFO_4 = "SignalInfo4";
	public static final String SIG_RET_INFO_5 = "SignalInfo5";
	public static final String SIG_RET_INFO_6 = "SignalInfo6";
	public static final String SIG_RET_INFO_7 = "SignalInfo7";
	public static final String SIG_RET_INFO_8 = "SignalInfo8";
	public static final String SIG_RET_INFO_9 = "SignalInfo9";

	public static final String BD_NUM_DEF = "0337009";

	public static final String RevBDAddress = "_RevBDAddress";// 北斗接收短信时 对方地址
	public static final String RevBDInfo = "_RevBDInfo";// 北斗接收短信保存在intent中的信息 关键字
	public static final String RevBDType = "_RevBDType";// 北斗接收短信的类型  代码0 汉字 1、混合2

	public static final int TX_P = 0;
	public static final int TX_F = 1;

	//数据类型
	public static final int TX_FIREPOIT = 0;
	public static final int TX_COMMON = 1;
	public static final int TX_SOS = 2;
	public static final int TX_MMS = 3;
	public static final int TX_FIRELINE = 4;
	public static final int TX_COMMAND = 5;
	public static final int TX_SYNCFL = 6;
	//传输类型
	public static final int TX_QZH = 1; //前指
	public static final int TX_JZH = 5;  //基指
	public static final int TX_BJ = 10;   //普通人员

	public static final int UOLOAD_STATE_0 = 0;  //单一基址
	public static final int UOLOAD_STATE_1 = 1;  //单一基址 + 前址

	public static final String UPLOAD_JZH_NUM = "UPLOAD_JZH_NUM";
	public static final String UPLOAD_QZH_NUM = "UPLOAD_QZH_NUM";
	public static final String UPLOAD__NUM_DEF = "337009";

	public static final String ACTION_MESSAGE = "com.lhzw.action.mms";
	public static final String ACTION_SYNC_FIRELINE = "com.lhzw.action.sync_fire_line";

	//短信状态
	public static final int MESSAGE_UNREAD = 0;
	public static final int MESSAGE_READ = 1;

	public static final int UPLOAD_STATE_ON = 0;
	public static final int UPLOAD_STATE_OFF = 1;

	public static final String HTTP_TOOKEN = "x-access-token";
	public static final String USER_PATH = "/security/user";
	public static final String APK_PATH = "/apks/latest";
	public static final String APK_OLD = "old";
	public static final String APK_NEW = "new";
	public static final String APK_BD = "bd";
	public static final String APK_map = "map";
	public static final int CHANNEL_DEF = 0;//默认信道
	//public static final String IP_ADD = "http://47.104.109.138:8080";
	public static final String IP_ADD = "http://192.168.1.119:8070/";
	public static final int SEND_ID_DEF = 1;

//对手表的指令接收状态

	public static final int COMMAND_CONFIRMED = 1;

	//EventBusBean 发送信息的Code值
	public static final int EVENT_CODE_REFRESH_MSG_LIST = 0X888;//刷新短消息页面
	public static final int EVENT_CODE_REFRESH_MSG_NUM = 0X887;//刷新安全监测页的未读消息
	public static final int EVENT_CODE_REFRESH_COMMAND_STATE = 0X886;//刷新指令接收页的状态
	public static final String SAVEPATH = Environment.getExternalStorageDirectory().getPath()+"/apk/";

	//三种通信类型
	public static final int COM_MODE_BD = 0; //北斗
	public static final int COM_MODE_NET = 1;//网络
	public static final int COM_MODE_AUTO = 3;//自动

    public static final int BD_OUTLINK_OFF = 0; // 关闭
    public static final int BD_OUTLINK_ON = 1;  // 打开

	//短消息的cmd
	public static final String CMD_SMS="receivesmsindication";
	public static final String CMD_COMMON = "locationindication";
	public static final String CMD_FIRELINE = "receivefirelineindication";
	public static final String CMD_SOS = "soslocationindication";
	public static final String CMD_FIRE_POINT = "receivefirepointindication";


}
