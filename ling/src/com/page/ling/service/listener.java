package com.page.ling.service;

import java.util.List;

import com.page.ling.dao.LingContactsCacheDao;
import com.page.ling.utils.Logger;
import com.page.ling.utils.SharedPrefHelper;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class listener extends Service {
	protected static final String TAG = "listener";
	private AudioManager am;
	private LingContactsCacheDao mLingContactsCacheDao;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate()
	{
		Logger.d(TAG, "start service listener");
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		TelephonyManager tm = (TelephonyManager)getSystemService(Service.TELEPHONY_SERVICE);
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		mLingContactsCacheDao = new LingContactsCacheDao(this);
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);   
	      
	    TickBroadcastReceiver receiver = new TickBroadcastReceiver();   
	    registerReceiver(receiver, filter);
	}
	
	private class TickBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			boolean isServiceRunning = false;
			
			if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {   
		        
			    //检查Service状态   	        
			    ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);  
			    List<RunningAppProcessInfo> lists = manager.getRunningAppProcesses();
			    for (RunningAppProcessInfo info : lists) {
				    if("com.page.ling:listener".equals(info.processName))   			            
				    {   
				    	isServiceRunning = true;   
				    } 
			    }  
			    
			    if (!isServiceRunning) {   
			    	Logger.d(TAG, "TickBroadcastReceiver startup listener service");
			    Intent i = new Intent(context, listener.class);   
			           context.startService(i);   
			    } 
			}
		}		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {        
		return START_STICKY;   
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		Logger.d(TAG, "Destroy service listener");
	}	
	
	@Override
	public void onTrimMemory(int level){
		Logger.d(TAG, "onTrimMemory level : " + level);
		Intent intent = new Intent(this, listener.class);
		PendingIntent restartIntent = PendingIntent.getService(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK); 
        
        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);    
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,    
                restartIntent); // 1秒钟后重启应用   
	}
	
	PhoneStateListener listener=new PhoneStateListener(){
		 
		  @Override
		  public void onCallStateChanged(int state, String incomingNumber) {
			   //注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
			   int curVolume;
			   int Oldvolume;
			   super.onCallStateChanged(state, incomingNumber);
			   switch(state){
			   case TelephonyManager.CALL_STATE_IDLE:
			    System.out.println("挂断");
			    Log.d(TAG, incomingNumber + " call IDLE");
			    
			    curVolume = am.getStreamVolume(AudioManager.STREAM_RING);
			    Oldvolume = SharedPrefHelper.getInstance().getOldRingVolume();
			    
			    if (curVolume != Oldvolume)
			    {
				    am.setStreamVolume(AudioManager.STREAM_RING, Oldvolume,  AudioManager.FLAG_SHOW_UI);
				    System.out.println("响铃:来电号码"+incomingNumber);
				    Logger.d(TAG, "设置音量 value=" + Oldvolume);		
			    }
			    
			    break;
			   case TelephonyManager.CALL_STATE_OFFHOOK:
				    System.out.println("接听");
				    Log.d(TAG, incomingNumber + " call OFFHOOK");
			    break;
			   case TelephonyManager.CALL_STATE_RINGING:	
				   
				   if (mLingContactsCacheDao.queryPhoneNum(incomingNumber))
				   {
					   curVolume = am.getStreamVolume(AudioManager.STREAM_RING);
					   
					   if (curVolume != SharedPrefHelper.getInstance().getSettingRingVolume())
					   {
						   SharedPrefHelper.getInstance().saveOldRingVolume(curVolume);
					   }

					   int volume = SharedPrefHelper.getInstance().getSettingRingVolume();
					   am.setStreamVolume(AudioManager.STREAM_RING, volume,  AudioManager.FLAG_SHOW_UI);
					   System.out.println("响铃:来电号码"+incomingNumber);
					   Logger.d(TAG, "设置音量 value=" + volume);					  
				   }

				    Log.d(TAG, incomingNumber + " call RINGING");
			    //输出来电号码
			    break;
			   }
		  }
	};	
}
