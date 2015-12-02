package com.page.ling.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.ImageView;

public class MyCheckBox extends ImageView {
	
	private int pos;
	
	public int getPos()
	{
		return this.pos;
	}

	public void setPos(int pos)
	{
		this.pos = pos;
	}
	
	public MyCheckBox(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public MyCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public MyCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

}
