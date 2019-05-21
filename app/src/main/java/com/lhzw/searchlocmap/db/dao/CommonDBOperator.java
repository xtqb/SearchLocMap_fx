package com.lhzw.searchlocmap.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.utils.BaseUtils;

/**
 * 数据库操作类
 */
public class CommonDBOperator {

	private static final String TAG = "CommonDBOperator";

	/**
	 * 通过一个条件查询数据库
	 * 
	 * @param dao
	 * @param key
	 * @param value
	 *            key =value
	 * @return
	 */
	public synchronized static <T> List<T> queryByKeys(Dao<T, Integer> dao,
			String key, String value) {

		List<T> list = null;
		if (dao != null && !BaseUtils.isStringEmpty(key)) {
			try {
				String formatValue = "";
				if (!BaseUtils.isStringEmpty(value)) {
					formatValue = value;
				}
				list = dao.queryBuilder().where().eq(key, formatValue).query();

			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return list;
	}

	/**
	 * 按时间顺序查询
	 * 
	 * @param dao
	 * @param key
	 * @param value
	 * @param time
	 * @return
	 */
	public synchronized static <T> List<T> queryByKeysOrderByTime(
			Dao<T, Integer> dao, String key, String value, String time) {
		List<T> list = null;
		if (dao != null && !BaseUtils.isStringEmpty(key)) {
			try {
				String formatValue = "";
				if (!BaseUtils.isStringEmpty(value)) {
					formatValue = value;
				}
				list = dao.queryBuilder().orderBy(time, false).where()
						.eq(key, formatValue).query();

			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return list;
	}

	/**
	 * 查询 经纬度是否存在
	 * 
	 * @param dao
	 * @return
	 */
	public synchronized static <T> List<T> queryOnLine(Dao<T, Integer> dao,
			boolean isNormal) {

		List<T> list = null;
		if (dao != null) {

			if (isNormal) {
				try {
					list = dao.queryBuilder().where().eq("state", 1).and()
							.eq("pending", "1").query();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					list = dao.queryBuilder().where().eq("state", 1).and()
							.eq("pending", "0").query();
				} catch (SQLException e) {

					e.printStackTrace();
				}

			}

		}
		return list;
	}

	/**
	 * 模糊查询
	 * 
	 * 
	 */
	public synchronized static <T> ArrayList<T> queryByFuzzy(
			Dao<T, Integer> dao, String key, String value) {

		ArrayList<T> list = null;
		if (dao != null && !BaseUtils.isStringEmpty(key)) {
			try {
				String formatValue = "";
				if (!BaseUtils.isStringEmpty(value)) {
					formatValue = value;
				}
				list = (ArrayList<T>) dao.queryBuilder().where()
						.like(key, "%" + formatValue + "%").query();

			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return list;
	}

	/**
	 * 查询某个条件大于某个值
	 * 
	 * @param dao
	 * @param key
	 * @param value
	 * @return
	 */
	public synchronized static <T> List<T> greaterThanByKey(
			Dao<T, Integer> dao, String key, int value) {
		List<T> list = null;
		if (dao != null && !BaseUtils.isStringEmpty(key)) {
			try {
				list = dao.queryBuilder().where().gt(key, value).query();
			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return list;
	}

	/**
	 * 查询某个条件大于某个值
	 * 
	 * @param dao
	 * @param key
	 * @param value
	 * @return
	 */
	public static <T> List<T> queryByMultiKeysRangeList(Dao<T, Integer> dao,
			Map<String, String> map, String key, int value) {
		List<T> list = null;
		if (dao != null && !BaseUtils.isStringEmpty(key) && map != null) {
			try {
				Where<T, Integer> where = dao.queryBuilder().where();
				for (Map.Entry<String, String> entry : map.entrySet()) {
					where = where.eq(entry.getKey(), entry.getValue()).and();
				}
				list = where.gt(key, value).query();
			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return list;
	}

	/**
	 * 查询某一列不等于某个值
	 * 
	 * @param dao
	 * @param key
	 * @param value
	 * @return
	 */
	public synchronized static <T> List<T> notEqualByKey(Dao<T, Integer> dao,
			String key, String value) {
		List<T> list = null;
		if (dao != null && !BaseUtils.isStringEmpty(key)) {
			try {
				list = dao.queryBuilder().where().ne(key, value).query();
			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return list;
	}

	/**
	 * 通过多个条件查询数据库 map是多个条件
	 * 
	 * @param dao
	 * @param map
	 * @return
	 */
	public synchronized static <T> List<T> queryByMultiKeys(
			Dao<T, Integer> dao, Map<String, String> map) {

		List<T> list = null;
		if (dao != null && map != null) {
			try {
				Where<T, Integer> where = dao.queryBuilder().where();
				int count = 0;
				for (Map.Entry<String, String> entry : map.entrySet()) {
					count++;
					if (count == map.size()) {
						where = where.eq(entry.getKey(), entry.getValue());
					} else {
						where = where.eq(entry.getKey(), entry.getValue()).and();
					}
				}
				list = where.query();
			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return list;
	}

	/**
	 * 通过多个条件模糊查询
	 * 
	 * @param dao
	 * @param map
	 * @return
	 */
	public synchronized static <T> List<T> queryByMultiKeysFuzzy(
			Dao<T, Integer> dao, Map<String, String> map) {

		List<T> list = null;
		if (dao != null && map != null) {
			try {
				Where<T, Integer> where = dao.queryBuilder().where();
				int count = 0;
				for (Map.Entry<String, String> entry : map.entrySet()) {

					count++;

					if (count == map.size()) {
						where = where.like(entry.getKey(),
								"%" + entry.getValue() + "%");
					} else {
						where = where.like(entry.getKey(),
								"%" + entry.getValue() + "%").or();
					}
				}

				list = where.query();

			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return list;
	}

	/**
	 * 查找所有数据
	 * 
	 * @param <T>
	 * 
	 * @param dao
	 * @return
	 */
	public synchronized static <T> List<T> getList(Dao<T, Integer> dao) {
		List<T> list = null;
		try {
			list = dao.queryForAll();
		} catch (SQLException e) {
			if (e != null) {
				Log.e(TAG, e.getMessage() + "");
			}
		}
		return list;
	}

	public synchronized static <T> List<T> queryAllItemByOrder(Dao<T, Integer> dao, String order) {
		List<T> list = null;
		try {
			list = dao.queryBuilder().orderBy(order, false).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 *
	 * @param dao
	 * @param key
	 * @param <T>
     * @return
     */
	public synchronized static <T> List<T> queryByOrderKey(Dao<T, Integer> dao, String key){
		List<T> list = null;
		try {
			list = dao.queryBuilder().orderBy("num", true).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 保存一条数据
	 * 
	 * @param dao
	 * @param item
	 * @return
	 */
	public synchronized static <T> boolean saveToDB(Dao<T, Integer> dao, T item) {
		int ret = Constants.FAILED;
		try {
			ret = dao.create(item);
		} catch (SQLException e) {
			if (e != null) {
				Log.e(TAG, e.getMessage() + "");
			}
		}
		return ret > 0 ? true : false;
	}

	/**
	 * 看某条数据是否已存在
	 * 
	 * @param dao
	 * @param key
	 * @param value
	 * @return
	 */
	public synchronized static boolean isExists(Dao<?, Integer> dao,
			String key, String value) {
		try {
			long count = dao.queryBuilder().where().eq(key, value).countOf();
			if (count > 0) {
				return true;
			}
		} catch (SQLException e) {
			if (e != null) {
				Log.e(TAG, e.getMessage() + "");
			}
		}
		return false;
	}

	/**
	 * 通过一个条件计数
	 * 
	 * @param dao
	 * @param key
	 * @param value
	 * @return
	 */
	public synchronized static long countByKey(Dao<?, Integer> dao, String key,
			String value) {
		long count = 0;
		try {
			count = dao.queryBuilder().where().eq(key, value).countOf();
		} catch (SQLException e) {
			if (e != null) {
				Log.e(TAG, e.getMessage() + "");
			}
		}
		return count;
	}

	/**
	 * 通过多个条件计数
	 * 
	 * @param dao
	 * @param map
	 * @return
	 */
	public synchronized static long countByMultiKeys(Dao<?, Integer> dao,
			Map<String, String> map) {
		long count = 0;
		if (dao != null && map != null) {
			try {
				Where<?, Integer> where = dao.queryBuilder().where();
				int i = 0;
				for (Map.Entry<String, String> entry : map.entrySet()) {

					i++;

					if (i == map.size()) {
						where = where.eq(entry.getKey(), entry.getValue());
					} else {
						where = where.eq(entry.getKey(), entry.getValue())
								.and();
					}
				}

				count = where.countOf();

			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return count;
	}

	public synchronized static <T> List<T> queryByMultiKeysEqual(Dao<T, Integer> dao, String key, List<String> values) {
		List<T> list = null;
		if (dao != null && values != null) {
			try {
				Where<T, Integer> where = dao.queryBuilder().where();
				int count = 0;
				for (String item : values) {
					count++;
					if (count == values.size()) {
						where = where.eq(key, item);
					} else {
						where = where.eq(key, item).or();
					}
				}
				list = where.query();
			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return list;
	}

	public synchronized static <T> List<T> queryByMultiKeysEqual(Dao<T, Integer> dao, String key, List<String> values, String key1, String values1) {
		List<T> list = null;
		if (dao != null && values != null) {
			try {
				Where<T, Integer> where = dao.queryBuilder().where();
				int count = 0;
				for (String item : values) {
					count++;
					if (count == values.size()) {
						where = where.eq(key, item);
					} else {
						where = where.eq(key, item).or();
					}
				}
				list = where.eq(key1, values1).query();
			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return list;
	}


	/**
	 * 判断某张表内是否有数据
	 * 
	 * @param dao
	 * @return
	 */
	public synchronized static boolean isTableEmpty(Dao<?, Integer> dao) {
		try {
			if (dao.countOf() == 0) {
				return true;
			}
		} catch (SQLException e) {
			if (e != null) {
				Log.e(TAG, e.getMessage() + "");
			}
		}
		return false;
	}

	/**
	 * 批量保存数据（不查重，用于清空数据后批量插入）
	 * 
	 * @param <T>
	 * 
	 * @param dao
	 * @param items
	 * @return
	 */
	public synchronized static <T> int saveToDBBatch(final Dao<T, Integer> dao,
			final List<T> items) {
		try {
			// 数据库批处理
			int ret = (Integer) dao.callBatchTasks(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					for (T item : items) {
						try {
							dao.create(item);
						} catch (SQLException e) {
							if (e != null) {
								Log.e(TAG, e.getMessage() + "");
							}
							return Constants.FAILED;
						}
					}
					return Constants.SUCCEED;
				}
			});
			return ret;
		} catch (Exception e) {
			if (e != null) {
				Log.e(TAG, e.getMessage() + "");
			}
		}
		return Constants.FAILED;

	}

	/**
	 * 更新一条数据
	 * 
	 * @param dao
	 * @param item
	 * @return
	 */
	public synchronized static <T> boolean updateItem(Dao<T, Integer> dao,
			T item) {
		int ret = Constants.FAILED;
		try {
			ret = dao.update(item);
		} catch (SQLException e) {
			if (e != null) {
				Log.e(TAG, e.getMessage() + "");
			}
		}
		return ret > 0 ? true : false;
	}

	/**
	 * 删除指定短信
	 * 
	 * @param id
	 */
	public static void deleteSMSById(Context context, int id) {
		Uri uri = Uri.parse("content://sms/");
		ContentResolver resolver = context.getContentResolver();
		resolver.delete(uri, "_id=" + id, null);
	}

	/**
	 * 删除一条数据
	 * 
	 * @param dao
	 * @param item
	 * @return
	 */
	public synchronized static <T> boolean deleteItem(Dao<T, Integer> dao,
			T item) {
		int ret = Constants.FAILED;
		try {
			ret = dao.delete(item);
		} catch (SQLException e) {
			if (e != null) {
				Log.e(TAG, e.getMessage() + "");
			}
		}
		return ret > 0 ? true : false;
	}

	/**
	 * 清空表中所有数据
	 * 
	 * @param dao
	 * @return
	 */
	public synchronized static boolean deleteAllItems(Dao<?, Integer> dao) {
		int ret = Constants.FAILED;
		DeleteBuilder<?, Integer> builder = dao.deleteBuilder();
		try {
			builder.setWhere(null);
			ret = builder.delete();
		} catch (SQLException e) {
			if (e != null) {
				Log.e(TAG, e.getMessage() + "");
			}
		}
		return ret > 0 ? true : false;
	}

	/**
	 * 通过一个条件删除数据项
	 * 
	 * @param dao
	 * @param key
	 * @param value
	 * @return
	 */
	public synchronized static boolean deleteByKeys(Dao<?, Integer> dao,
			String key, String value) {

		int ret = Constants.FAILED;
		DeleteBuilder<?, Integer> builder = dao.deleteBuilder();
		if (dao != null && !BaseUtils.isStringEmpty(key)) {
			try {
				String formatValue = "";
				if (!BaseUtils.isStringEmpty(value)) {
					formatValue = value;
				}
				builder.where().eq(key, formatValue);
				ret = builder.delete();
			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return ret > 0 ? true : false;
	}

	/**
	 * 通过多个条件删除数据项
	 * 
	 * @param dao
	 * @param map
	 * @return
	 */
	public synchronized static boolean deleteByMultiKeys(Dao<?, Integer> dao,
			Map<String, String> map) {

		int ret = Constants.FAILED;
		DeleteBuilder<?, Integer> builder = dao.deleteBuilder();

		if (dao != null && map != null) {
			try {
				Where<?, Integer> where = builder.where();
				int count = 0;
				for (Map.Entry<String, String> entry : map.entrySet()) {
					count++;
					if (count == map.size()) {
						where = where.eq(entry.getKey(), entry.getValue());
					} else {
						where = where.eq(entry.getKey(), entry.getValue())
								.and();
					}
				}
				ret = builder.delete();
			} catch (SQLException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage() + "");
				}
			}
		}
		return ret > 0 ? true : false;
	}

}
