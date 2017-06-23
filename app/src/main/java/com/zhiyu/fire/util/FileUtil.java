package com.zhiyu.fire.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil
{
	public static InputStream getAssetFileInputSream(String path, Context context) throws IOException
	{
		AssetManager am = context.getApplicationContext().getAssets();
		return am.open(path);
	}
	
	public static boolean copyAssetsToFilesystem(String assetsSrc, String des, Context context)
	{
		InputStream istream = null;
		OutputStream ostream = null;
		
		try
		{
			AssetManager am = context.getAssets();
			istream = am.open(assetsSrc);
			ostream = new FileOutputStream(des);
			//FileOutputStream outStream = context.openFileOutput("tmp.tmp", Context.MODE_PRIVATE);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = istream.read(buffer)) > 0)
			{
				ostream.write(buffer, 0, length);
			}
			istream.close();
			ostream.close();
		} catch (Exception e)
		{
			e.printStackTrace();
			try
			{
				if (istream != null)
					istream.close();
				if (ostream != null)
					ostream.close();
			} catch (Exception ee)
			{
				ee.printStackTrace();
			}
			return false;
		}
		return true;
	}

}
