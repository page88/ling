package com.page.ling.constants;

public class DbConstant {
	public static final String       DB_FILE                                       = "/ling/db";
    public static final String       DB_NAME                                       = "ling_contacts.db";
    public static final String       DB_NAME_GREEN_DAO                             = "ling_contacts_green.db";
    public static final int          DB_VERSION                                    = 1;
    
    private static final String      TERMINATOR                                    = ";";
    
    public static final StringBuffer CREATE_LING_CONTACTS_TABLE_SQL                = new StringBuffer();
    public static final String       LING_CONTACTS_TABLE_NAME                      = "contacts";
    public static final String       LING_CONTACTS_TABLE_ID                        = android.provider.BaseColumns._ID;
    public static final String       LING_CONTACTS_TABLE_PHONE_NUM                 = "phoneNum";
    public static final String       LING_CONTACTS_TABLE_PHONE_NAME                = "phoneName";
    public static final String       LING_CONTACTS_TABLE_HEAD_PHOTO                = "headPhoto";
    public static final String       LING_CONTACTS_TABLE_SWITCH_FLAG               = "switchFlag";
    
    static{
    	CREATE_LING_CONTACTS_TABLE_SQL.append("CREATE TABLE ").append(LING_CONTACTS_TABLE_NAME);
    	CREATE_LING_CONTACTS_TABLE_SQL.append(" (").append(LING_CONTACTS_TABLE_ID);
    	CREATE_LING_CONTACTS_TABLE_SQL.append(" integer primary key autoincrement,");
    	CREATE_LING_CONTACTS_TABLE_SQL.append(LING_CONTACTS_TABLE_PHONE_NUM).append(" text,");
    	CREATE_LING_CONTACTS_TABLE_SQL.append(LING_CONTACTS_TABLE_PHONE_NAME).append(" text,");
    	CREATE_LING_CONTACTS_TABLE_SQL.append(LING_CONTACTS_TABLE_HEAD_PHOTO).append(" text,");
    	CREATE_LING_CONTACTS_TABLE_SQL.append(LING_CONTACTS_TABLE_SWITCH_FLAG).append(" text)");
    	CREATE_LING_CONTACTS_TABLE_SQL.append(TERMINATOR);
    }
}
