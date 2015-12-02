package com.page.ling.utils;

import java.io.File;

import com.page.ling.constants.DbConstant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	public DbHelper(Context context) {
		super(context, DbConstant.DB_NAME, null, DbConstant.DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.beginTransaction();
        try {
        	if (SDcardUtil.isHasSdcard())
        	{
        		String dir  = SDcardUtil.getSDPath(DbConstant.DB_FILE);
        		
        		File folder = new File(dir);
        		
        		if (!folder.exists()){
        			folder.mkdirs();
        		}
        		
        		String path = SDcardUtil.getSDPath(DbConstant.DB_FILE + "/" + DbConstant.DB_NAME);
        		db.openOrCreateDatabase(path, null);
        	}
        	
            db.execSQL(DbConstant.CREATE_LING_CONTACTS_TABLE_SQL.toString());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
