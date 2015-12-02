package com.page.ling.bean;

import java.io.Serializable;

public class ContactsInfo implements Serializable {
	private static final long serialVersionUID = 5375804597574885028L;
	
	public String PhoneNum;
	public String PhoneName;
	public boolean isCheck;
	
	public String toString()
	{
		return "ContactsInfo{ PhoneName:" + PhoneName + " PhoneNum:" + PhoneNum + " }";
	}
}
