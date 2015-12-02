package com.page.ling;

import com.page.ling.dao.LingContactsCacheDao;
import com.page.ling.utils.Logger;
import com.page.ling.utils.SharedPrefHelper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhoneBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "PhoneBroadcastReceiver";
	private AudioManager am;
	private LingContactsCacheDao mLingContactsCacheDao;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
//		String action = intent.getAction();
//		
//		if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)){
//			String phoneNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//			Log.d(TAG, phoneNum + " call out");
//		}
//		else
//		{
//			am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//			TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
//			tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
//			mLingContactsCacheDao = new LingContactsCacheDao(context);
//		}
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
