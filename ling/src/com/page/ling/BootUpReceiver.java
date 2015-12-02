package com.page.ling;


import com.page.ling.service.listener;
import com.page.ling.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {

	static final String action_boot="android.intent.action.BOOT_COMPLETED";
	private static final String TAG = "BootUpReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Logger.d(TAG, "BOOT_COMPLETED");
		
		 if (intent.getAction().equals(action_boot)){
			 context.startService(new Intent(context, listener.class));
		}
	}

	
}
