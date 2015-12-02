package com.page.ling.greendao;

import java.io.File;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.page.ling.constants.DbConstant;
import com.page.ling.greendao.contacts;
import com.page.ling.utils.SDcardUtil;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CONTACTS".
*/
public class contactsDao extends AbstractDao<contacts, Long> {

    public static final String TABLENAME = "CONTACTS";

    /**
     * Properties of entity contacts.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PhoneNum = new Property(1, String.class, "phoneNum", false, "PHONE_NUM");
        public final static Property PhoneName = new Property(2, String.class, "phoneName", false, "PHONE_NAME");
        public final static Property SwitchFlag = new Property(3, Boolean.class, "switchFlag", false, "SWITCH_FLAG");
        public final static Property PhonePhoto = new Property(4, byte[].class, "phonePhoto", false, "PHONE_PHOTO");
        public final static Property Date = new Property(5, java.util.Date.class, "date", false, "DATE");
    };


    public contactsDao(DaoConfig config) {
        super(config);
    }
    
    public contactsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
    	
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
    	
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CONTACTS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PHONE_NUM\" TEXT UNIQUE ," + // 1: phoneNum
                "\"PHONE_NAME\" TEXT NOT NULL ," + // 2: phoneName
                "\"SWITCH_FLAG\" INTEGER," + // 3: switchFlag
                "\"PHONE_PHOTO\" BLOB," + // 4: phonePhoto
                "\"DATE\" INTEGER);"); // 5: date
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CONTACTS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, contacts entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String phoneNum = entity.getPhoneNum();
        if (phoneNum != null) {
            stmt.bindString(2, phoneNum);
        }
        stmt.bindString(3, entity.getPhoneName());
 
        Boolean switchFlag = entity.getSwitchFlag();
        if (switchFlag != null) {
            stmt.bindLong(4, switchFlag ? 1L: 0L);
        }
 
        byte[] phonePhoto = entity.getPhonePhoto();
        if (phonePhoto != null) {
            stmt.bindBlob(5, phonePhoto);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(6, date.getTime());
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public contacts readEntity(Cursor cursor, int offset) {
        contacts entity = new contacts( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // phoneNum
            cursor.getString(offset + 2), // phoneName
            cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // switchFlag
            cursor.isNull(offset + 4) ? null : cursor.getBlob(offset + 4), // phonePhoto
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)) // date
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, contacts entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPhoneNum(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPhoneName(cursor.getString(offset + 2));
        entity.setSwitchFlag(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setPhonePhoto(cursor.isNull(offset + 4) ? null : cursor.getBlob(offset + 4));
        entity.setDate(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(contacts entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(contacts entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
