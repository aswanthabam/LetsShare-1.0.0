package com.abam.letsshare;
import android.support.v7.app.*;
import java.net.*;

public class FileShareManager
{
	private AppCompatActivity activity;
	private InetAddress adress;
	private boolean type;
	private FilesSender sender;
	private FileReciver receiver;
	/*
	type will be true if need filesender
	false if need filereciver
	*/
	public FileShareManager(AppCompatActivity a){
		activity = a;
		type = true;
		sender = new FilesSender(a);
	}
	public FileShareManager(AppCompatActivity a,InetAddress ad){
		activity = a;
		type = false;
		receiver = new FileReciver(a,ad);
	}
	public void start(){
		if(type){
			sender.start();
		}else{
			receiver.start();
		}
	}
}
