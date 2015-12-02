package com.page.ling;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.page.ling.bean.ContactsInfo;
import com.page.ling.dao.LingContactsCacheDao;
import com.page.ling.greendao.contacts;
import com.page.ling.service.listener;
import com.page.ling.utils.DbGreenUtil;
import com.page.ling.utils.DensityUtil;
import com.page.ling.utils.Logger;
import com.page.ling.utils.SharedPrefHelper;
import com.page.ling.widget.CircularProgressDrawable;
import com.page.ling.widget.MyCheckBox;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	/**获取库Phon表字段**/  
    private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
    private static final int PHONE_DISPLAY_NAME_INDEX = 0;
    private static final int PHONE_NUM_INDEX = 1;
    private List<ContactsInfo> mContactsInfoList;
    private SwipeMenuListView lv_contacts;
    private TextView tv_total_num;
    private ImageView iv_circle;
    private Button bt_add;
    private LingContactsCacheDao mLingContactsCacheDao;
    private ContactsAdapter mContactsAdapter;
    private ExecutorService SingleThreadExcutor = Executors.newSingleThreadExecutor();
    private Handler mHandler;
    private CircularProgressDrawable drawable;
    private Animator currentAnimation;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lv_contacts = (SwipeMenuListView) findViewById(R.id.lv_contacts);
		iv_circle = (ImageView) findViewById(R.id.iv_circle);
		bt_add = (Button) findViewById(R.id.bt_add);
		tv_total_num = (TextView) findViewById(R.id.tv_total_num);
		//am.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);	
		mLingContactsCacheDao = new LingContactsCacheDao(this);
		
		mContactsAdapter = new ContactsAdapter();
		lv_contacts.setAdapter(mContactsAdapter);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {				
			setTranslucentStatus(this, true);
		}
			
		SystemBarTintManager tintManager = new SystemBarTintManager(this);	
		tintManager.setStatusBarTintEnabled(true);			
		// 使用颜色资源		
		tintManager.setStatusBarTintResource(R.color.title_bg);
		
		mContactsInfoList = new ArrayList<ContactsInfo>();
		
		
		drawable = new CircularProgressDrawable.Builder()
        .setRingWidth(getResources().getDimensionPixelSize(R.dimen.drawable_ring_size))
        .setOutlineColor(getResources().getColor(android.R.color.white))
        .setRingColor(getResources().getColor(R.color.circle_green))
        .setCenterColor(getResources().getColor(android.R.color.holo_blue_dark))
        .setRingbgColor(getResources().getColor(R.color.circle_bg))
        .create();
		iv_circle.setImageDrawable(drawable);
		

		

		//Animation animation = AnimationUtils.loadAnimation(this, R.anim.circle_anim);
		//iv_circle.startAnimation(animation);
		
		bt_add.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getBaseContext(), ContactsActivity.class);
				startActivity(it);
			}
			
		});
		
		mHandler = new Handler(){
			public void handleMessage(Message msg){
				if (mContactsInfoList != null && mContactsInfoList.size() > 0)
				{
					tv_total_num.setText(mContactsInfoList.size() + "");
					lv_contacts.setVisibility(View.VISIBLE);
					mContactsAdapter.notifyDataSetChanged();
				}	
				else
				{
					tv_total_num.setText("0");
					lv_contacts.setVisibility(View.GONE);
				}
			}
		};
		
		SwipeMenuCreator creator = new SwipeMenuCreator(){

			@Override
			public void create(SwipeMenu menu) {
				// TODO Auto-generated method stub
				// create "delete" item
		        SwipeMenuItem deleteItem = new SwipeMenuItem(
		                getApplicationContext());
		        // set item background
		        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
		                0x3F, 0x25)));
		        // set item width
		        deleteItem.setWidth(DensityUtil.dip2px(MainActivity.this, 90));
		        // set a icon
		        deleteItem.setIcon(R.drawable.ic_delete);
		        // add to menu
		        menu.addMenuItem(deleteItem);
			}
		};		
		
		lv_contacts.setMenuCreator(creator);
		lv_contacts.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		    @Override
		    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
		        switch (index) {
		        case 0:
		            // delet
		        	final String phoneNum = mContactsInfoList.get(position).PhoneNum;
		        	if (phoneNum != null && phoneNum.length() > 0)
		        	{
		        		final String[] phoneNumList = {phoneNum};
		        		SingleThreadExcutor.execute(new Runnable(){

		        			@Override
		        			public void run() {
		        				// TODO Auto-generated method stub
		        				mLingContactsCacheDao.DeletPhoneNum(phoneNumList);		        		
		        				mContactsInfoList = mLingContactsCacheDao.getLingContacts();
		        				mHandler.sendMessage(mHandler.obtainMessage());
		        			}			
		        		});     		
		        	}

		            break;
		        }
		        // false : close the menu; true : not close the menu
		        return false;
		    }
		});
		
		Intent it = new Intent(this, listener.class);
		startService(it);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onResume(){
		super.onResume();
		
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int curValue = am.getStreamVolume(AudioManager.STREAM_RING);
		SharedPrefHelper.getInstance().saveOldRingVolume(curValue);
		
		SingleThreadExcutor.execute(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mContactsInfoList = mLingContactsCacheDao.getLingContacts();
				mHandler.sendMessage(mHandler.obtainMessage());
			}			
		});
		
		int setting = SharedPrefHelper.getInstance().getSettingRingVolume();
		float end = 0.0f;
		
		if (setting > 0)
		{
			end = (float) ((setting * 100.0) / (SoftApplication.maxVolume * 100.0));
		}
		currentAnimation = progressAnimation(0.0f, end, true);
		currentAnimation.start();
	}
	
	@SuppressLint("NewApi")
	@Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        Logger.d(TAG, "onKeyDown: keyCode: " + keyCode);
    	int OldSet;
    	int CurSet;
		float end = 0.0f;
		float start = 0.0f;
    	
        if (KeyEvent.KEYCODE_VOLUME_UP == keyCode) {
        	Logger.d(TAG,"KEYCODE_VOLUME_UP");
        	OldSet = SharedPrefHelper.getInstance().getSettingRingVolume();
        	CurSet = OldSet + 1;
        	
        	if (CurSet >= SoftApplication.maxVolume)
        	{
        		CurSet = SoftApplication.maxVolume;
        	}
        	
        	SharedPrefHelper.getInstance().saveSettingRingVolume(CurSet);
    		
    		if (OldSet >= 0)
    		{
    			start = (float) ((OldSet * 100.0) / (SoftApplication.maxVolume * 100.0));
    			end = (float) ((CurSet * 100.0) / (SoftApplication.maxVolume * 100.0));
    		}
    		currentAnimation = progressAnimation(start, end, false);
    		currentAnimation.start();
    		
        	return true;
        } else if (KeyEvent.KEYCODE_VOLUME_DOWN == keyCode) {
        	Logger.d(TAG,"KEYCODE_VOLUME_DOWN");
        	
        	OldSet = SharedPrefHelper.getInstance().getSettingRingVolume();
        	CurSet = OldSet - 1;
        	
        	if (CurSet <= 0){
        		CurSet = 0;
        	}
        	
        	SharedPrefHelper.getInstance().saveSettingRingVolume(CurSet);
    		
    		if (OldSet >= 0)
    		{
    			start = (float) ((OldSet * 100.0) / (SoftApplication.maxVolume * 100.0));
    			end = (float) ((CurSet * 100.0) / (SoftApplication.maxVolume * 100.0));
    		}
    		currentAnimation = progressAnimation(start, end, false);
    		currentAnimation.start();
    		
        	return true;
        }

        return super.onKeyDown(keyCode, event);
    }		
    
    @SuppressLint("NewApi")
	private Animator progressAnimation(float start, float end, boolean isDelay) {
        AnimatorSet animation = new AnimatorSet();

        ObjectAnimator invertedProgress = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.PROGRESS_PROPERTY, start, end);
        invertedProgress.setDuration(1200);
        
        if (isDelay)
        {
        	invertedProgress.setStartDelay(1000);
        }
        
        invertedProgress.setInterpolator(new OvershootInterpolator());

        animation.playTogether(invertedProgress);
        return animation;
    }    

	private static void setTranslucentStatus(Activity activity, boolean on) {
	
		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		
		if (on) {
			winParams.flags |= bits;
		} else {
		winParams.flags &= ~bits;
		}
	
		win.setAttributes(winParams);	
	}	
	
	private class ContactsAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mContactsInfoList != null)
			{
				return mContactsInfoList.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (mContactsInfoList != null)
			{
				return mContactsInfoList.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder = null;
			
			if (convertView == null)
			{
				convertView = View.inflate(getBaseContext(), R.layout.list_selected_contacts, null);
				holder = new Holder();
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
				holder.v_divider_up = (View) convertView.findViewById(R.id.v_divider_up);
				holder.iv_switch = (ImageView) convertView.findViewById(R.id.iv_switch);
				convertView.setTag(holder);
			}
			
			holder = (Holder)convertView.getTag();
			holder.tv_name.setText(mContactsInfoList.get(position).PhoneName);
			holder.tv_num.setText(mContactsInfoList.get(position).PhoneNum);
			
			if (position == 0){
				holder.v_divider_up.setVisibility(View.VISIBLE);
			}
			
			if (mContactsInfoList.get(position).isCheck)
			{
				holder.iv_switch.setImageDrawable(getResources().getDrawable(R.drawable.on));
			}
			else
			{
				holder.iv_switch.setImageDrawable(getResources().getDrawable(R.drawable.off));
			}
			
			holder.iv_switch.setOnClickListener(new SwitchListener(position));
			
			return convertView;
		}
		
		class SwitchListener implements OnClickListener {
	        private int position;

	        SwitchListener(int pos) {
	            position = pos;
	        }
	        
	        @Override
	        public void onClick(View v) {
				if (mContactsInfoList.get(position).isCheck)
				{
					mContactsInfoList.get(position).isCheck = false;
					mLingContactsCacheDao.ChangeSwitchFlag(mContactsInfoList.get(position).PhoneNum, "0");
				}
				else
				{
					mContactsInfoList.get(position).isCheck = true;	
					mLingContactsCacheDao.ChangeSwitchFlag(mContactsInfoList.get(position).PhoneNum, "1");
				}
				
				ContactsAdapter.this.notifyDataSetChanged();
	        }
	    }
		
		public class Holder{
			TextView tv_name;
			TextView tv_num;
			View     v_divider_up;
			ImageView iv_switch;
		}
	}
}
