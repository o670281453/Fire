package com.zhiyu.fire.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.zhiyu.fire.model.FireStation;
import com.zhiyu.fire.model.WaterSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DBDao
{
	private static final String tbName1 = "source";
	private static final String tbName2 = "station";
	private static final String tbName3 = "timestamp";
	private static DBHelper dbHelper = null;
	//private Context context = null;

	public DBDao(Context context)
	{
		String cacheDirPath = Environment.getExternalStorageDirectory().toString();
        String dstFilePath = cacheDirPath + "/Fire/fire.db";
		if(!new File(dstFilePath).exists()) {

			AssetManager am = context.getAssets();
			String[] files = null;
			List<String> dats = new ArrayList<String>();
			try {
				files = am.list("");
				Log.d("test", files[0].toString());
			} catch (IOException e) {
				e.printStackTrace();
			}

			File f = new File(cacheDirPath + "/Fire/");
			if (!f.exists()) {
				f.mkdirs();
			}

			InputStream in = null;
			int length = 0;
			try {
				in = am.open("fire.db");
				length = in.available();
				Log.d("test", length + "===");
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if (length != 0) {
					FileUtil.copyAssetsToFilesystem("fire.db", dstFilePath, context);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		dbHelper = new DBHelper(context, "fire.db", null, 1);
	}

	/**
	 * 获取当前数据库的时间戳
	 * @return
	 * @throws Exception
	 */
	public long getTimeStamp() throws Exception	{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		// select * from info group by brand,factory;
		String sql = "select timestamp from "+tbName3+ " where rowid=1";
		Cursor cursor = db.rawQuery(sql, null);
		long time = 0L;
		while (cursor.moveToNext())
		{
			time = cursor.getLong(cursor.getColumnIndex("timestamp"));
		}
		cursor.close();
		db.close();
		return time;
	}

	/**
	 * 查询符合条件的水源
	 * @param areaList
	 * @return
	 * @throws Exception
	 */
	public List<WaterSource> getSource(List<Integer> areaList) throws Exception
	{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "";
		for(int i=0; i<areaList.size(); i++){
			if(i != areaList.size()-1) {
				sql += areaList.get(i) + ",";
			}else {
				sql += areaList.get(i);
			}
		}
		sql = "select * from "+tbName1+ " where areaId in (" + sql +");";
		Cursor cursor = db.rawQuery(sql, null);
		List<WaterSource> sourceList = new ArrayList<WaterSource>();
		WaterSource source;
		while (cursor.moveToNext())
		{
			source = new WaterSource();
			source.id = cursor.getInt(cursor.getColumnIndex("id"));
			source.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
			source.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
			source.status = cursor.getInt(cursor.getColumnIndex("status"));
			source.areaId = cursor.getInt(cursor.getColumnIndex("areaId"));
			sourceList.add(source);
		}
		cursor.close();
		db.close();
		return sourceList;
	}

	/**
	 * 查询符合条件的队站
	 * @param areaList
	 * @return
	 * @throws Exception
	 */
	public List<FireStation> getStation(List<Integer> areaList) throws Exception
	{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "";
		for(int i=0; i<areaList.size(); i++){
			if(i != areaList.size()-1) {
				sql += areaList.get(i) + ",";
			}else {
				sql += areaList.get(i);
			}
		}
		sql = "select * from "+tbName2+ " where areaId in (" + sql +");";
		Cursor cursor = db.rawQuery(sql, null);
		List<FireStation> fireStationList = new ArrayList<>();
		FireStation station;
		while (cursor.moveToNext())
		{
			station = new FireStation();
			station.id = cursor.getInt(cursor.getColumnIndex("id"));
			station.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
			station.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
			station.areaId = cursor.getInt(cursor.getColumnIndex("areaId"));
			fireStationList.add(station);
		}
		cursor.close();
		db.close();
		return fireStationList;
	}

	/**
	 * 批量添加或更新水源数据库
	 * @param sourceList
	 * @throws Exception
	 */
	public void refreshWaterSource(List<WaterSource> sourceList) throws Exception{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		int i;
		for( i=0 ;i<sourceList.size(); i++) {
			sql = "select * from "+tbName1+ " where id=" + sourceList.get(i).id + ";";
			cursor = db.rawQuery(sql, null);
			//查询有结果，说明是修改
			if (cursor.moveToNext()) {
				updateSource(db, sourceList.get(i));
				Log.d("test", "修改成功"+sourceList.get(i).id);
			} else {//查询无结果，说明是新增
				addSource(db, sourceList.get(i));
				Log.d("test", "添加成功"+sourceList.get(i).id);
			}
		}
		db.close();
	}

	/**
	 * 批量添加或更新队站数据库
	 * @param fireStationList
	 * @throws Exception
	 */
	public void refreshFireStation(List<FireStation> fireStationList) throws Exception{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		int i;
		for( i=0 ;i<fireStationList.size(); i++) {
			sql = "select * from "+tbName2+ " where id=" + fireStationList.get(i).id + ";";
			cursor = db.rawQuery(sql, null);
			//查询有结果，说明是修改
			if (cursor.moveToNext()) {
				updateFireStation(db, fireStationList.get(i));
				Log.d("test", "修改成功"+fireStationList.get(i).id);
			} else {//查询无结果，说明是新增
				addFireStation(db, fireStationList.get(i));
				Log.d("test", "添加成功"+fireStationList.get(i).id);
			}
		}
		db.close();
	}

	/**
	 * 增加单个水源
	 * @param db
	 * @param source
	 * @throws Exception
	 */
	public void addSource(SQLiteDatabase db, WaterSource source) throws Exception{
		String sql = "insert into "+ tbName1 +" (id, longitude, latitude, status, areaId) values ("+
				source.id + "," + source.longitude + "," + source.latitude+"," + source.status + ","+source.areaId + ")";
		db.execSQL(sql);
	}

	/**
	 * 增加单个队站
	 * @param db
	 * @param station
	 * @throws Exception
	 */
	public void addFireStation(SQLiteDatabase db, FireStation station) throws Exception{
		String sql = "insert into "+ tbName2 +" (id, longitude, latitude, status, areaId) values ("+
				station.id + "," + station.longitude + "," + station.latitude+"," + station.areaId + ")";
		db.execSQL(sql);
	}

	/**
	 * 修改单个水源
	 * @param db
	 * @param source
	 * @throws Exception
	 */
	public void updateSource(SQLiteDatabase db, WaterSource source) throws Exception{
		String sql = "update "+ tbName1 +
				" set longitude=" +source.longitude
				+ ", latitude=" + source.latitude
				+ ", status=" + source.status
				+ ", areaId=" +source.areaId
				+ " where id="+ source.id + ";";
			db.execSQL(sql);
	}

	/**
	 * 修改单个队站
	 * @param db
	 * @param station
	 * @throws Exception
	 */
	public void updateFireStation(SQLiteDatabase db, FireStation station) throws Exception{
		String sql = "update "+ tbName2 +
				" set longitude=" +station.longitude
				+ ", latitude=" + station.latitude
				+ ", areaId=" +station.areaId
				+ " where id="+ station.id + ";";
			db.execSQL(sql);
	}
}
