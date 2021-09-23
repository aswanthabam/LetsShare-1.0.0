package com.abam.letsshare;
import android.support.v7.app.*;
import android.content.*;
import android.net.wifi.*;
import java.net.*;
import java.nio.*;
import java.io.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.util.*;

public class FilesSender
{
	private AppCompatActivity activity;
	private Context context;
	public static int MY_PORT = 8080;
	private PrintWriter output;
	private BufferedReader input;
	public static String TAG = "FILE_SERVER";
	public FilesSender(AppCompatActivity a){
		// For file reciveing 
		activity =a;
		context = a.getApplicationContext();
		new Thread(new Thread1()).start();
		Log.i(TAG,"Initializing serevr socket.");
	}
	
	public void start(){
		new Thread(new Thread3("Server Message")).start();
		Log.i(TAG,"Sending server message...");
	}
	/*private String getLocalIpAddress() throws UnknownHostException {
		WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
		assert wifiManager != null;
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipInt = wifiInfo.getIpAddress();
		return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
	}*/
	public class Thread1 implements Runnable{
		private ServerSocket serverSocket;
		private Socket socket;
		public Thread1(){
			
		}
		@Override public void run(){
			try{
				serverSocket = new ServerSocket(MY_PORT);
				Log.i(TAG,"Server socket initialization success.");
			}catch(IOException e){
				Log.e(TAG,"server socket initialization failed. Caused by IOException("+e.toString()+")");
			}
			try{
				if(serverSocket != null){
					socket = serverSocket.accept();
					output = new PrintWriter(socket.getOutputStream());
					input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					Log.i(TAG,"Socket accepting now. socket created. streams initialized");
					new Thread(new Thread2()).start();
				}
			}catch(IOException e){
				Log.e(TAG,"Creating socket failed. in thread 1. streams not created. Caused by("+e.toString()+")");
			}
		}
	}
	public class Thread2 implements Runnable{
		@Override public void run(){
			while(true){
				try{
					if(input != null){
						final String message = input.readLine();
						if(message != null){
							Log.i(TAG,"Got a message from client"+message);
							activity.runOnUiThread(new Runnable(){
								@Override public void run(){
									Toast.makeText(context,message,12).show();
								}
							});
							break;
						}else{
							Log.i(TAG,"No message got. re running thread 1");
							new Thread(new Thread1()).start();
						}
					}
				}catch(IOException e){
					Log.e(TAG,"Error occured in thread 2. reading messages failed. Caused by("+e.toString()+")");
				}
			}
		}
	}
	public class Thread3 implements Runnable {
		private String message;
		public Thread3(String m){
			message = m;
		}
		@Override public void run(){
			while(true){
				if(output != null){
					output.write(message);
					output.flush();
					Log.i(TAG,"Message send to client. Output stream written");
					break;
				}
			}
		}
	}
}
