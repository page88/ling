package com.page.ling.utils;

import java.io.IOException;

import android.util.Log;

public class Logger {

	private static int LOGLEVEL = 5; 
	private static int VERBOSE = 5;
	private static int DEBUG = 4;
	private static int INFO = 3;
	private static int WARN = 2;
	private static int ERROR = 1;
	
	public static void SetLogLevel(String Level)
	{
		LOGLEVEL = Integer.parseInt(Level);
	}
	
	public static void v(String tag, String msg){
		if(LOGLEVEL >= VERBOSE){
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag, String msg){
		if(LOGLEVEL >= DEBUG){
			Log.d(tag, msg);
		}
	}
	public static void i(String tag, String msg){
		if(LOGLEVEL >= INFO){
			Log.i(tag, msg);
		}
	}
	public static void w(String tag, String msg) throws IOException{
		if(LOGLEVEL >= WARN){
			Log.w(tag, msg);
		}
	}
	
	public static void w(String tag, String msg, Throwable tr){
		if(LOGLEVEL >= WARN){
			Log.w(tag, msg, tr);
		}
	}
	
	public static void e(String tag, String msg){
		if(LOGLEVEL >= ERROR){
			Log.e(tag, msg);
		}
	}
	
}
