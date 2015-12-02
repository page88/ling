package com.page.ling.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.page.ling.bean.ContactsInfo;
import com.page.ling.constants.DbConstant;
import com.page.ling.utils.Logger;
import com.page.ling.utils.SqliteUtils;

public class LingContactsCacheDao {
	
	private static final String TAG = "LingContactsCacheDao";
	private SqliteUtils mSqliteUtils;
	
	public LingContactsCacheDao(Context ct)
	{
		mSqliteUtils = SqliteUtils.getInstance(ct);
	}
	
	public void insert(List<ContactsInfo> ContactsInfoList)
	{
		if (ContactsInfoList != null)
		{
			mSqliteUtils.getDb().beginTransaction();
			
			
			for (int i=0; i<ContactsInfoList.size(); i++)
			{
				StringBuffer query = new StringBuffer();
				query.append("select * from ").append(DbConstant.LING_CONTACTS_TABLE_NAME);
				query.append(" where ").append(DbConstant.LING_CONTACTS_TABLE_PHONE_NUM).append("=?");
				String[] phoneNum = new String[1];
				phoneNum[0] = ContactsInfoList.get(i).PhoneNum.replace(" ", "");	
				
				Cursor cursor = mSqliteUtils.getDb().rawQuery(query.toString(), phoneNum);
				
				if (cursor != null)
				{		
					if(cursor.moveToFirst()){
						Logger.d(TAG, cursor.getString(cursor.getColumnIndex(DbConstant.LING_CONTACTS_TABLE_PHONE_NUM)));
						ContentValues cv = new ContentValues();
						cv.put(DbConstant.LING_CONTACTS_TABLE_PHONE_NAME, ContactsInfoList.get(i).PhoneName);
						cv.put(DbConstant.LING_CONTACTS_TABLE_PHONE_NUM, phoneNum[0]);
						mSqliteUtils.getDb().update(DbConstant.LING_CONTACTS_TABLE_NAME, cv, 
								DbConstant.LING_CONTACTS_TABLE_PHONE_NUM + "=?", phoneNum);
					}
					else
					{
						ContentValues cv = new ContentValues();
						cv.put(DbConstant.LING_CONTACTS_TABLE_PHONE_NAME, ContactsInfoList.get(i).PhoneName);
						cv.put(DbConstant.LING_CONTACTS_TABLE_PHONE_NUM, phoneNum[0]);
						mSqliteUtils.getDb().insert(DbConstant.LING_CONTACTS_TABLE_NAME, null, cv);
					}
					
					if (!cursor.isClosed()){
						cursor.close();
					}
				}
			}
			
			mSqliteUtils.getDb().setTransactionSuccessful();
			mSqliteUtils.getDb().endTransaction();

		}
	}
	
	public List<ContactsInfo> getLingContacts(){
		List<ContactsInfo> ContactsInfoList = null;
		StringBuffer query = new StringBuffer();
		query.append("select * from ").append(DbConstant.LING_CONTACTS_TABLE_NAME);
		Cursor cursor = mSqliteUtils.getDb().rawQuery(query.toString(), null);
		
		if (cursor != null)
		{
			if (cursor.moveToFirst())
			{
				ContactsInfoList = new ArrayList<ContactsInfo>();
				ContactsInfo ci = new ContactsInfo();
				ci.PhoneName = cursor.getString(cursor.getColumnIndex(DbConstant.LING_CONTACTS_TABLE_PHONE_NAME));
				ci.PhoneNum = cursor.getString(cursor.getColumnIndex(DbConstant.LING_CONTACTS_TABLE_PHONE_NUM));
				
				String flag = cursor.getString(cursor.getColumnIndex(DbConstant.LING_CONTACTS_TABLE_SWITCH_FLAG));
				if (flag != null && flag.equals("1"))
				{
					ci.isCheck = true;
				}
				else
				{
					ci.isCheck = false;
				}
				
				ContactsInfoList.add(ci);
			}
			
			while (cursor.moveToNext())
			{
				ContactsInfo ci = new ContactsInfo();
				ci.PhoneName = cursor.getString(cursor.getColumnIndex(DbConstant.LING_CONTACTS_TABLE_PHONE_NAME));
				ci.PhoneNum = cursor.getString(cursor.getColumnIndex(DbConstant.LING_CONTACTS_TABLE_PHONE_NUM));
				String flag = cursor.getString(cursor.getColumnIndex(DbConstant.LING_CONTACTS_TABLE_SWITCH_FLAG));
				if (flag != null && flag.equals("1"))
				{
					ci.isCheck = true;
				}
				else
				{
					ci.isCheck = false;
				}
				
				ContactsInfoList.add(ci);
			}
			
			if (cursor.isClosed())
			{
				cursor.close();
			}
		}
		
		return ContactsInfoList;
	}

	public boolean queryPhoneNum(String PhoneNum){
		
		if (PhoneNum != null && PhoneNum.length() > 0)
		{
			StringBuffer query = new StringBuffer();
			query.append("select * from ").append(DbConstant.LING_CONTACTS_TABLE_NAME);
			query.append(" where ").append(DbConstant.LING_CONTACTS_TABLE_PHONE_NUM).append("=? and ");
			query.append(DbConstant.LING_CONTACTS_TABLE_SWITCH_FLAG).append("=?");
			String[] phoneNum = {PhoneNum, "1"};
			Cursor cursor = mSqliteUtils.getDb().rawQuery(query.toString(), phoneNum);
			Logger.d(TAG, "queryPhoneNum :" + PhoneNum);
			if (cursor != null)
			{
				if (cursor.moveToFirst())
				{
					Logger.d(TAG, "queryPhoneNum true");
					return true;
				}
				
				cursor.close();
			}
		}
		
		return false;
	}
	
	public void DeletPhoneNum(String[] phoneNum)
	{
		if (phoneNum != null && phoneNum.length > 0)
		{
			mSqliteUtils.getDb().beginTransaction();
			mSqliteUtils.getDb().delete(DbConstant.LING_CONTACTS_TABLE_NAME, DbConstant.LING_CONTACTS_TABLE_PHONE_NUM+"=?", phoneNum);
			mSqliteUtils.getDb().setTransactionSuccessful();
			mSqliteUtils.getDb().endTransaction();
		}
	}
	
	public void ChangeSwitchFlag(String PhoneNum, String flag)
	{
		if (PhoneNum != null && PhoneNum.length() > 0)
		{
			StringBuffer query = new StringBuffer();
			query.append("select * from ").append(DbConstant.LING_CONTACTS_TABLE_NAME);
			query.append(" where ").append(DbConstant.LING_CONTACTS_TABLE_PHONE_NUM).append("=?");
			String[] phoneNum = {PhoneNum};
			Cursor cursor = mSqliteUtils.getDb().rawQuery(query.toString(), phoneNum);
			Logger.d(TAG, "queryPhoneNum :" + PhoneNum);
			if (cursor != null)
			{		
				if(cursor.moveToFirst()){
					ContentValues cv = new ContentValues();
					cv.put(DbConstant.LING_CONTACTS_TABLE_SWITCH_FLAG, flag);
					mSqliteUtils.getDb().update(DbConstant.LING_CONTACTS_TABLE_NAME, cv, 
							DbConstant.LING_CONTACTS_TABLE_PHONE_NUM + "=?", phoneNum);
				}
				
				if (!cursor.isClosed()){
					cursor.close();
				}
			}
		}
	}
}
