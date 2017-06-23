package com.zhiyu.fire.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DBHelper extends SQLiteOpenHelper
{
	private static final String DB_NAME = "tmp.tmp";
	private static final int DB_VERSION = 1;
	private static final String TABLE_NAME = "timestamp";
//	private static final String CREATE_INFO = "create table if not exists fire("
//			+ "id integer primary key autoincrement,id varchar(20),"
//			+ "time varchar(20),img BLOB)";
	private SQLiteDatabase db;
	private Context context;
	
	public DBHelper(Context context, String name, CursorFactory factory, int version)
	{
		super(new CustomPathDatabaseContext(context, getDirPath(context)), name, factory, version);
		this.context = context;
	}
	
	private static String getDirPath(Context context)
	{
//		return context.getCacheDir().getAbsolutePath();
		return Environment.getExternalStorageDirectory().toString()+"/Fire";
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		this.db = db;
//		db.execSQL("create table if not exists info("
//				+ "id integer primary key autoincrement,name varchar(20),"
//				+ "brand varchar(20),"
//				+ "factory varchar(20)"
//				+ ")");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
	}
	
	public void insert(ContentValues values, String tableName)
	{
		db = getWritableDatabase();
		db.insert(tableName, null, values);
		db.close();
	}

	// Return cursor with all columns by tableName
	public Cursor query(String tableName)
	{
		db = getWritableDatabase();
		Cursor c = db.query(tableName, null, null, null, null, null, null);
		return c;
	}

	// Return cursor by SQL string
	public Cursor rawQuery(String sql, String[] args)
	{
		db = getWritableDatabase();
		Cursor c = db.rawQuery(sql, args);
		return c;
	}

	// Execute a single SQL statement(as insert,create,delete)instead of a query
	public void execSQL(String sql)
	{
		db = getWritableDatabase();
		db.execSQL(sql);
	}

	// Delete by id
	public void del(int id)
	{
		if (db == null)
			db = getWritableDatabase();
		db.delete(TABLE_NAME, "id=?", new String[] { String.valueOf(id) });
	}

	public void close()
	{
		if (db != null)
			db.close();
	}

	

	// Bitmap to byte[]
	public byte[] bmpToByteArray(Bitmap bmp)
	{
		// Default size is 32 bytes
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

	// Cursor to bitmap
	Bitmap cursorToBmp(Cursor c, int columnIndex)
	{

		byte[] data = c.getBlob(columnIndex);
		try
		{
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (Exception e)
		{
			return null;
		}
	}
}
