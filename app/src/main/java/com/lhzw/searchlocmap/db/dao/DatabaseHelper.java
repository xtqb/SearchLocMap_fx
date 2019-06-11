package com.lhzw.searchlocmap.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lhzw.searchlocmap.bean.DipperInfoBean;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.LocPersonalInfo;
import com.lhzw.searchlocmap.bean.LocTrackBean;
import com.lhzw.searchlocmap.bean.MessageInfoIBean;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.bean.PlotItemInfo;
import com.lhzw.searchlocmap.bean.SyncFireLine;
import com.lhzw.searchlocmap.bean.TreeStateBean;
import com.lhzw.searchlocmap.bean.UploadInfo;
import com.lhzw.searchlocmap.bean.WatchLastLocTime;
import com.lhzw.searchlocmap.constants.Constants;

import java.sql.SQLException;

public class DatabaseHelper<T> extends OrmLiteSqliteOpenHelper {

	private Dao<PersonalInfo, Integer> persondao;// 每张表对应一个Dao
	private Dao<LocPersonalInfo, Integer> LocPersondao;// 本地访问对象Dao
	private Dao<UploadInfo, Integer> uploadInfoDap;// 本地访问对象Dao
	private Dao<MessageInfoIBean, Integer> mesgInfoDao;// 短信对象Dao
	private Dao<PlotItemInfo, Integer> plotItemDao;
	private Dao<DipperInfoBean, Integer> dipDao;
	private Dao<SyncFireLine, Integer> syncDao;
	private Dao<LocTrackBean, Integer> locTrckDao;
	private Dao<HttpPersonInfo, Integer> httpPerDao;
	private Dao<TreeStateBean, Integer> treeDao;
	private Dao<WatchLastLocTime, Integer> lastLocDao;
	private static DatabaseHelper<?> instance;

	private DatabaseHelper(Context context) {
		super(context, Constants.DB_NAME, null, 2);// 数据库的名字
		// TODO Auto-generated constructor stub
	}

	public static synchronized DatabaseHelper<?> getHelper(Context context) {
		if (instance == null) {
			synchronized (DatabaseHelper.class) {
				if (instance == null)
					instance = new DatabaseHelper<Object>(context);
			}
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			// 创建表
			TableUtils.createTable(connectionSource, PersonalInfo.class);
			TableUtils.createTable(connectionSource, LocPersonalInfo.class);
			TableUtils.createTable(connectionSource, MessageInfoIBean.class);
			TableUtils.createTable(connectionSource, PlotItemInfo.class);
			TableUtils.createTable(connectionSource, DipperInfoBean.class);
			TableUtils.createTable(connectionSource, SyncFireLine.class);
			TableUtils.createTable(connectionSource, LocTrackBean.class);
			TableUtils.createTable(connectionSource, HttpPersonInfo.class);
			TableUtils.createTable(connectionSource, TreeStateBean.class);
			TableUtils.createTable(connectionSource, WatchLastLocTime.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 业务员收件表

	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionsource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, PersonalInfo.class, true);
			TableUtils.dropTable(connectionSource, LocPersonalInfo.class, true);
			TableUtils.dropTable(connectionSource, MessageInfoIBean.class, true);
			TableUtils.dropTable(connectionSource, PlotItemInfo.class, true);
			TableUtils.dropTable(connectionSource, DipperInfoBean.class, true);
			TableUtils.dropTable(connectionSource, SyncFireLine.class, true);
			TableUtils.dropTable(connectionSource, LocTrackBean.class, true);
			TableUtils.dropTable(connectionSource, HttpPersonInfo.class, true);
			TableUtils.dropTable(connectionSource, TreeStateBean.class, true);
			TableUtils.dropTable(connectionSource, WatchLastLocTime.class, true);
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 获得userDao
	public Dao<PersonalInfo, Integer> getPersonalInfoDao() {

		// 已上传

		if (persondao == null) {
			try {
				persondao = getDao(PersonalInfo.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return persondao;

	}

	// 获得userDao
	public Dao<LocPersonalInfo, Integer> getLocPersonDao() {

		// 已上传

		if (LocPersondao == null) {
			try {
				LocPersondao = getDao(LocPersonalInfo.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return LocPersondao;

	}

	// 上传数据表Dao
	public Dao<UploadInfo, Integer> getUploadInfoDao() {

		// 已上传

		if (uploadInfoDap == null) {
			try {
				uploadInfoDap = getDao(UploadInfo.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return uploadInfoDap;

	}

	// 上传数据表Dao
	public Dao<MessageInfoIBean, Integer> getMesgInfoDao() {

		// 已上传

		if (mesgInfoDao == null) {
			try {
				mesgInfoDao = getDao(MessageInfoIBean.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return mesgInfoDao;

	}

	public Dao<PlotItemInfo, Integer> getPlotItemDao() {
		if(plotItemDao == null) {
			try {
				plotItemDao = getDao(PlotItemInfo.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return plotItemDao;
	}

	public Dao<DipperInfoBean, Integer> getDipperDao() {
		if(dipDao == null) {
			try {
				dipDao = getDao(DipperInfoBean.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dipDao;
	}

	public Dao<SyncFireLine, Integer> getSyncDao() {
		if(syncDao == null) {
			try {
				syncDao = getDao(SyncFireLine.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return syncDao;
	}

	public Dao<LocTrackBean, Integer> getLocTrackDao() {
		if(locTrckDao == null) {
			try {
				locTrckDao = getDao(LocTrackBean.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return locTrckDao;
	}

	public Dao<HttpPersonInfo, Integer> getHttpPerDao() {
		if(httpPerDao == null) {
			try {
				httpPerDao = getDao(HttpPersonInfo.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return httpPerDao;
	}

	public Dao<TreeStateBean, Integer> getTreeStateDao() {
		if(treeDao == null) {
			try {
				treeDao = getDao(TreeStateBean.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return treeDao;
	}

	public Dao<WatchLastLocTime, Integer> getLastLocTimeDao() {
		if(lastLocDao == null) {
			try {
				lastLocDao = getDao(WatchLastLocTime.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return lastLocDao;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		persondao = null;
		LocPersondao = null;
		mesgInfoDao = null;
		syncDao = null;
		httpPerDao = null;
		lastLocDao = null;
	}

}
