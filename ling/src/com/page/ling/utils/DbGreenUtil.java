package com.page.ling.utils;

import java.util.List;

import android.content.Context;

import com.page.ling.SoftApplication;
import com.page.ling.constants.DbConstant;
import com.page.ling.greendao.DaoSession;
import com.page.ling.greendao.contacts;
import com.page.ling.greendao.contactsDao;
import com.page.ling.greendao.contactsDao.Properties;

import de.greenrobot.dao.query.QueryBuilder;

public class DbGreenUtil {
	private static final String TAG = "DbGreenUtil";
	private DaoSession mDaoSession;
	private contactsDao  mContactsDao;
	private static DbGreenUtil instance;
	
	public DbGreenUtil(){
		
	}
	
	public static DbGreenUtil getInstance(Context context){
		if (instance == null){
			synchronized (DbGreenUtil.class){
				if (instance == null){
					instance = new DbGreenUtil();
					instance.mDaoSession = SoftApplication.getDaoSession(context);
					instance.mContactsDao = instance.mDaoSession.getContactsDao();
				}
			}
		}
		
		return instance;
	}
	
	public contacts loadContacts(long id){
		return mContactsDao.load(id);
	}
	
	public List<contacts> loadAllContacts(){
		return mContactsDao.loadAll();
	}
	
	public void saveContacts(contacts Contacts){
		if (Contacts != null)
		{
			mContactsDao.insertOrReplace(Contacts);
		}
	}
	
	public void updateContacts(contacts Contacts)
	{
		if (Contacts != null)
		{
			mContactsDao.update(Contacts);
		}
	}
	
	public void saveContactsList(final List<contacts> contactsList){
		
		if (contactsList != null && contactsList.size() > 0)
		{
//			mContactsDao.getSession().runInTx(new Runnable(){
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					for (int i=0; i<contactsList.size(); i++){
//						contacts Contacts = contactsList.get(i);
//						mContactsDao.insertOrReplace(Contacts);
//					}
//				}
//				
//			});
			
			for (int i=0; i<contactsList.size(); i++){
				contacts Contacts = contactsList.get(i);
				mContactsDao.insertOrReplace(Contacts);
			}
		}
	}
	
	public boolean queryPhoneNum(String PhoneNum)
	{
		if (PhoneNum != null && PhoneNum.length() > 0)
		{
//			StringBuffer query = new StringBuffer();
//			query.append("phoneNum =? and");
//			query.append("switchFlag =?");
//			String[] phoneNum = {PhoneNum, "1"};
//			mContactsDao.queryRaw(query.toString(), phoneNum);
			QueryBuilder qb = mContactsDao.queryBuilder().where(Properties.PhoneNum.eq(PhoneNum), Properties.SwitchFlag.eq(true));
			
			if (qb != null)
			{
				List<contacts> Contacts = qb.list();
				
				if (Contacts != null && Contacts.size() > 0){
					return true;
				}
			}
			
		}
		
		return false;
	}
	

    public void deleteAll(){  
    	mContactsDao.deleteAll();  
    }  

    public void deleteContacts(long id){  
    	mContactsDao.deleteByKey(id);  
    }  
      
    public void deleteContacts(contacts note){  
    	mContactsDao.delete(note);  
    }	
}

