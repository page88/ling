package com.page.ling;


import com.page.ling.constants.DbConstant;
import com.page.ling.greendao.DaoMaster;
import com.page.ling.greendao.DaoMaster.OpenHelper;
import com.page.ling.greendao.DaoSession;
import com.page.ling.utils.SharedPrefHelper;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

public class SoftApplication extends Application {

	private static final String TAG = "SoftApplication";
	public static SoftApplication softApplication;
	public static int maxVolume;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	@Override
	public void onCreate() {
		super.onCreate();
		
		softApplication = this;
		
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_RING);
		int curValue = am.getStreamVolume(AudioManager.STREAM_RING);
		
		if (SharedPrefHelper.getInstance().getSettingRingVolume() == -1)
		{
			SharedPrefHelper.getInstance().saveSettingRingVolume(maxVolume/2);
		}

		SharedPrefHelper.getInstance().saveOldRingVolume(curValue);
	}
	
	public static DaoMaster getDaoMaster(Context context){
		if (daoMaster == null){
			OpenHelper helper = new DaoMaster.DevOpenHelper(context, DbConstant.DB_NAME_GREEN_DAO, null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}
	
	public static DaoSession getDaoSession(Context context) {  
        if (daoSession == null) {  
            if (daoMaster == null) {  
                daoMaster = getDaoMaster(context);  
            }  
            daoSession = daoMaster.newSession();  
        }  
        return daoSession;  
    }  
}
