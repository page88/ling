package com.page.ling.utils;

import com.page.ling.SoftApplication;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefHelper {
	private static final String TAG = "SharedPrefHelper";
	private static final String SP_FILE_NAME = "ling_sp";
	private static SharedPrefHelper sharedPrefHelper = null;
	private static SharedPreferences sharedPreferences;
	
	/**
	 * 音量信息
	 */
	private static final String OLD_RING_VOLUME = "OLD_RING_VOLUME";
	private static final String SETTING_RING_VOLUME = "SETTING_RING_VOLUME";
	
	public static SharedPrefHelper getInstance()
	{
		if (sharedPrefHelper == null)
		{
			synchronized (SharedPrefHelper.class){
				if (sharedPrefHelper == null){
					sharedPrefHelper = new SharedPrefHelper();
				}
			}
		}
		
		return sharedPrefHelper;
	}
	
	private SharedPrefHelper(){
		sharedPreferences = SoftApplication.softApplication.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
	}
	
	public void save(String name, String value){
		sharedPreferences.edit().putString(name, value).commit();
		Logger.d(TAG, "SharePreferences save:" + value);
	} 
	
	public String get(String name, String defValue) {
		return sharedPreferences.getString(name, defValue);
	}
	
	public void save(String name, int value){
		sharedPreferences.edit().putInt(name, value).commit();
		Logger.d(TAG, "SharePreferences save:" + value);
	}
	
	public int get(String name, int defValue){
		return sharedPreferences.getInt(name, defValue);
	}
	
	public int getOldRingVolume(){
		return get(OLD_RING_VOLUME, 0);
	}
	
	public void saveOldRingVolume(int value){
		Logger.d(TAG, "value=" + value);
		save(OLD_RING_VOLUME, value);
	}

	public int getSettingRingVolume(){
		return get(SETTING_RING_VOLUME, -1);
	}
	
	public void saveSettingRingVolume(int value){
		Logger.d(TAG, "value=" + value);
		save(SETTING_RING_VOLUME, value);
	}
}
