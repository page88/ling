package com.page.ling;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.page.ling.bean.ContactsInfo;
import com.page.ling.dao.LingContactsCacheDao;
import com.page.ling.greendao.contacts;
import com.page.ling.utils.DbGreenUtil;
import com.page.ling.utils.Logger;
import com.page.ling.widget.MyCheckBox;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsActivity extends Activity {
	/**获取库Phon表字段**/  
    private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
    private static final int PHONE_DISPLAY_NAME_INDEX = 0;
    private static final int PHONE_NUM_INDEX = 1;
	private static final String TAG = "ContactsActivity";
    private List<ContactsInfo> mContactsInfoList;
    private ListView lv_contacts;
    private ImageView iv_title_back;
    private LinearLayout ll_title_back;
    private Button bt_ok;
    private ContactsAdapter mContactsAdapter;
    private LingContactsCacheDao mLingContactsCacheDao;
    private Handler mHandler;
    private ExecutorService SingleThreadExcutor = Executors.newSingleThreadExecutor();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		
		lv_contacts = (ListView) findViewById(R.id.lv_contacts);
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		ll_title_back = (LinearLayout) findViewById(R.id.ll_title_back);
		bt_ok = (Button) findViewById(R.id.bt_ok);
		
		mContactsInfoList = new ArrayList<ContactsInfo>();
		mContactsAdapter = new ContactsAdapter();
		lv_contacts.setAdapter(mContactsAdapter);
		
		mLingContactsCacheDao = new LingContactsCacheDao(this);
		
		getContacts();
		
		ll_title_back.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}			
		});
		
		bt_ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final List<ContactsInfo> cilist = new ArrayList<ContactsInfo>();
				final List<contacts> contactsList = new ArrayList<contacts>();
				for(ContactsInfo ci:mContactsInfoList){
					if (ci.isCheck)
					{
						contacts ct = new contacts();
						ct.setPhoneName(ci.PhoneName);
						ct.setPhoneNum(ci.PhoneNum);
						ct.setSwitchFlag(false);
						ct.setPhonePhoto(null);
						cilist.add(ci);
						contactsList.add(ct);
					}
				}
				
				SingleThreadExcutor.execute(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mLingContactsCacheDao.insert(cilist);
						mHandler.sendMessage(new Message());
					}
					
				});
			}
		});
		
		mHandler = new Handler(){
			
			public void handleMessage(Message msg) {
				finish();
		    }
		};
	}
	
	public void getContacts() {
		ContentResolver resolver = this.getContentResolver();
		
		// 读取手机的联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
		
		if (phoneCursor != null){
			
			while (phoneCursor.moveToNext()){
				String phoneNum = phoneCursor.getString(PHONE_NUM_INDEX);
				
				if (TextUtils.isEmpty(phoneNum)){
					continue;
				}
				
				String phoneName = phoneCursor.getString(PHONE_DISPLAY_NAME_INDEX);
				
				if (!TextUtils.isEmpty(phoneName))
				{
					ContactsInfo ci = new ContactsInfo();
					ci.PhoneName = phoneName;
					ci.PhoneNum  = phoneNum;
					
					if (mContactsInfoList != null)
					{
						mContactsInfoList.add(ci);
						Logger.d(TAG, ci.toString());
					}
				}
			}
			
			phoneCursor.close();
			phoneCursor = null;
		}
		
		// 读取sim卡的联系人
		Uri uri = Uri.parse("content://icc/adn");
		
		try
		{
			phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);
		}
		catch(Exception e)
		{
			phoneCursor = null;
			e.printStackTrace();
		}
		
		if (phoneCursor != null) {  
	        while (phoneCursor.moveToNext()) {  
	  
	        // 得到手机号码  
	        String phoneNum = phoneCursor.getString(PHONE_NUM_INDEX);  
	        // 当手机号码为空的或者为空字段 跳过当前循环  
	        if (TextUtils.isEmpty(phoneNum))  
	            continue;  
	        // 得到联系人名称  
	        String phoneName = phoneCursor.getString(PHONE_DISPLAY_NAME_INDEX);  
	        
		        if (!TextUtils.isEmpty(phoneName))
				{
					ContactsInfo ci = new ContactsInfo();
					ci.PhoneName = phoneName;
					ci.PhoneNum  = phoneNum;
					
					if (mContactsInfoList != null)
					{
						mContactsInfoList.add(ci);
						Logger.d(TAG, ci.toString());
					}
				}
	        }  
	  
	        phoneCursor.close();  
	    }  
		
		mContactsAdapter.notifyDataSetChanged();
	}
	
	private class ContactsAdapter extends BaseAdapter implements OnClickListener{

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
				convertView = View.inflate(getBaseContext(), R.layout.list_contacts, null);
				holder = new Holder();
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
				holder.cb_check = (MyCheckBox) convertView.findViewById(R.id.cb_check);
				convertView.setTag(holder);
			}
			
			holder = (Holder)convertView.getTag();
			holder.cb_check.setPos(position);
			holder.tv_name.setText(mContactsInfoList.get(position).PhoneName);
			holder.tv_num.setText(mContactsInfoList.get(position).PhoneNum);
			
			if (mContactsInfoList.get(position).isCheck)
			{
				holder.cb_check.setBackgroundDrawable(getResources().getDrawable(R.drawable.progress_click));
			}
			else
			{
				holder.cb_check.setBackgroundDrawable(getResources().getDrawable(R.drawable.progress_unclick));
			}
			
			holder.cb_check.setOnClickListener(this);
			
			return convertView;
		}
		
		public class Holder{
			TextView tv_name;
			TextView tv_num;
			MyCheckBox cb_check;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.cb_check)
			{
				int pos = ((MyCheckBox)v).getPos();
				
				if (mContactsInfoList.get(pos).isCheck)
				{
					mContactsInfoList.get(pos).isCheck = false;
				}
				else
				{
					mContactsInfoList.get(pos).isCheck = true;
				}
				
				ContactsAdapter.this.notifyDataSetChanged();
			}
		}
	}	
}
