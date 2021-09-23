package com.abam.letsshare;
import android.support.v7.app.*;
import android.content.*;
import java.net.*;
import java.io.*;
import android.widget.*;
import android.widget.SearchView.*;
import android.view.*;
import android.util.*;
import android.os.*;

public class FileReciver
{
	private AppCompatActivity activity;
	private Context context;
	private InetAddress HOST_IP;
	public static int HOST_PORT = 8080;
	private PrintWriter output;
	private BufferedReader input;
	public static String TAG = "FILE_CLIENT";
	public FileReciver(AppCompatActivity a,InetAddress host_ip){
		activity = a;
		context = a.getApplicationContext();
		HOST_IP = host_ip;
		new Thread(new Thread1()).start();
		Log.i(TAG,"File reciver initialized. Thread started running. Listening for message initializing..");
	}
	public void start(){
		new Thread(new Thread3("Client Message")).start();
		Log.i(TAG,"Sending client messgae to server...");
	}
	
	public class Thread1 implements Runnable{
		private Socket socket;
		@Override public void run(){
			try{
				for(int i = 0;true;i++){
					try{
						socket = new Socket(HOST_IP,HOST_PORT);
						break;
					}catch(IOException e){
						Log.e(TAG,"Reconnecting("+i+")...");
						if(i == 10)
							break;
						continue;
					}
				}
				while(socket != null){
					output = new PrintWriter(socket.getOutputStream());
					input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					Log.i(TAG,"socket and streams initialized. Socket("+HOST_IP+","+HOST_PORT+")");
					new Thread(new Thread2()).start();
					break;
				}
				
			}catch(IOException e){
				e.printStackTrace();
			}
			
		}
	}
	public class Thread2 implements Runnable{
		@Override public void run(){
			try{
				while(true){
					if(input != null){
						final String message = input.readLine();
						if(message != null){
							Log.i(TAG,"Got a message from server "+message);
							activity.runOnUiThread(new Runnable(){
								@Override public void run(){
									Toast.makeText(context,message,12).show();
								}
							});
							/*File file = new File(Environment.getExternalStorageDirectory()+"/LetsShare/SharedText.txt");
							FileOutputStream fos = new FileOutputStream(file);
							Manager.copyFile(
							break;*/
						}else{
							Log.i(TAG,"Searching for mesaage. Running Thread 1 again.");
							new Thread(new Thread1()).start();
						}
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	public class Thread3 implements Runnable{
		public String message;
		public Thread3(String m){
			message = m;
		}
		@Override public void run(){
			try{
				while(true){
					if(output != null){
						Log.i(TAG,"Sending client message using output . to "+HOST_IP);
						output.write(message);
						output.flush();
						break;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
