package com.abam.letsshare;
import android.content.*;
import android.net.wifi.*;
import android.net.wifi.p2p.*;
import android.net.wifi.p2p.WifiP2pManager.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.view.*;
import android.*;
import android.content.pm.*;
import android.location.*;
import android.view.View.*;
import android.util.*;
import java.util.*;
import java.io.*;
import android.net.*;
import java.net.*;

public class Manager implements MainReciver.ActionListener{
	private ActivityMain activity;
	private Context context;
	private ManagerInterface listener;
	private WifiManager wifiManager;
	private WifiP2pManager wifiP2pManager;
	private IntentFilter filter;
	private boolean WifiP2pSupported = true;
	private Channel wifiChannel;
	private MainReciver wifiReciver;
	private Button mainTurnOn,ButtonMakeConnection,ButtonConnect,ButtonDisconnect,connectRecive;
	private ImageView mainImage;
	private TextView mainText,bottomFilesSelected;
	private AnimationDrawable wifiAnimation;
	private boolean LocationOn = false;
	public static String TAG = "MANAGER";
	private List<WifiP2pDevice> mDeviceList = new ArrayList<WifiP2pDevice>();
	private ArrayAdapter<String> mAdapter;
	private int flag = 0;
	private WifiP2pDevice currentDevice;
	private boolean WifiDirectConnected = false;
	private List<File> filesSelected = new ArrayList<File>();
	private BottomDialog currentDialog;
	private boolean reciver = false;
	private WifiP2pInfo info;
	private InetAddress groupOwnerAdress;
	private FileShareManager fileSharer;
	
	public Manager(ActivityMain a){
		activity = a;
		context = a.getApplicationContext();
		wifiManager = (WifiManager)activity.getSystemService(Context.WIFI_SERVICE);
		boolean isP2pSupported = true;
		
		if(!isP2pSupported) WifiP2pSupported = false;
		else{
			wifiP2pManager = (WifiP2pManager)activity.getSystemService(Context.WIFI_P2P_SERVICE);
			WifiP2pSupported = true;
			wifiManager.setWifiEnabled(true);
			wifiChannel = wifiP2pManager.initialize(context,activity.getMainLooper(),null);
			wifiReciver = new MainReciver(wifiP2pManager,wifiChannel,activity);
			wifiReciver.setActionListener(this);
			filter = new IntentFilter();
			filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
			filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
			filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
			filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
			activity.registerReceiver(wifiReciver,filter);
			
		}
	}
	
	
	
	
	
	
	public void createGroup(){
		wifiP2pManager.createGroup(wifiChannel,new ActionListener(){
			@Override public void onSuccess(){
				
			}
			@Override public void onFailure(int reason){
				Log.e(TAG,"Cant create group. Caused by("+reason+")");
			}
		});
	}
	
	public void setFilesSelected(List<File> m){
		filesSelected = m;
	}
	
	// Disconnect from peer
	public void disconnect(){
		if(wifiP2pManager != null){
			wifiP2pManager.removeGroup(wifiChannel,new ActionListener(){
					@Override public void onSuccess(){
						wifiSccess();
						mainText.setText("Disconnected");
						Log.i(TAG,"Disconnected From Group");
					}
					@Override public void onFailure(int reason){
						mainText.setText("Couldnt disconnect");
						Log.e(TAG,"Cant disconnect");
					}
				});
		}
	}
	// Discover to peers
	public void DiscoverPeers(){
		if(wifiP2pManager != null){
			wifiP2pManager.discoverPeers(wifiChannel,new ActionListener(){
					@Override public void onSuccess(){
						Log.i(TAG,"Discovered peers successful");
					}
					@Override public void onFailure(int reason){
						Log.e(TAG+"_ERROR","Cant discover peers. caused by reason coede("+reason+" )");
					}
				});
		}
	}
	// Check For peers
	public void checkPeer(){
		if(wifiP2pManager != null){
			wifiP2pManager.requestPeers(wifiChannel,new PeerListListener(){
					@Override public void onPeersAvailable(WifiP2pDeviceList peers){
						if(peers != null){
							//ArrayList<WifiP2pDevice> dd = new ArrayList<WifiP2pDevice>();
							mDeviceList.clear();
							mDeviceList.addAll(peers.getDeviceList());
							ArrayList<String> devices = new ArrayList<String>();
							for(WifiP2pDevice device : mDeviceList){
								devices.add(device.deviceName);
							}
							if(devices.size() > 0){
								mAdapter = new ArrayAdapter<String>(activity.getApplicationContext(),android.R.layout.simple_list_item_1,devices);
								// Shows device selection dialog only for once
								if(flag == 0){
									flag = 1;
									showDeviceListDialog();
								}
							}else{
								mainText.setText("No Nearby Devices found. Searching...");
							}
						}
					}

				});
		}
	}
	// Show device list fragment
	public void showDeviceListDialog(){
		DeviceListDialog dialog = new DeviceListDialog(activity.getApplicationContext(),mAdapter);
		dialog.setOnDeviceSelectedListener(new DeviceListDialog.OnDeviceSelectedListener(){
				@Override public void onDeviceSelected(int which){
					final WifiP2pDevice device = mDeviceList.get(which);
					if(device != null){
						WifiP2pConfig config = new WifiP2pConfig();
						config.deviceAddress = device.deviceAddress;
						toast("Devices available "+device.deviceName);
						// Connect to device
						wifiP2pManager.connect(wifiChannel,config,new ActionListener(){
								@Override public void onSuccess(){
									// Sets current device
									currentDevice = device;
									// Sets wifi direct is connected
									WifiDirectConnected = true;
								}
								@Override public void onFailure(int reason){
									// Connection failed
									Log.e(TAG+"_MANAGER_CONNECT","Wifi direct connection failiure. caused by( "+reason+ " )");
								}
							});
					}

				}
			});
		// Show fragment
		dialog.show(activity.getFragmentManager(),"devices");
	}
	// File saving 
	public static long copyFile(InputStream in,OutputStream out){
		byte buf[] = new byte[1024];
		int len;
		long starttime = System.currentTimeMillis();
		try{
			while((len = in.read(buf)) != -1){
				out.write(buf,0,len);
			}
			out.close();
			in.close();
			long endtime = System.currentTimeMillis() - starttime;
			return endtime;
		}catch(Exception e){
			Log.e("SAVE_FILE_ERROR",e.toString());
		}
		return 0;
	}
	// Toast
	public void toast(final String text){
		try{
			Runnable r = new Runnable(){
				@Override public void run(){
					Toast.makeText(activity.getApplicationContext(),text,12).show();
				}
			};
			activity.runOnUiThread(r);
		}catch(Exception e){
			Log.e(TAG,"Error in toasting. Caused by( "+e.toString()+" )");
		}
	}
	
	// Set button click listeners
	public void setClickListeners(){
		// Turn on wifi button
		mainTurnOn.setOnClickListener(new OnClickListener(){
				@Override public void onClick(View v){
					wifiManager.setWifiEnabled(true);
				}
			});
		// Make connection button
		ButtonMakeConnection.setOnClickListener(new OnClickListener(){
				@Override public void onClick(View v){
					// Discover peers
					// This will be captured by the wifi_on_peers_changed
					DiscoverPeers();
					// Show scanning animation
					wifiScan();
				}
			});
		// Share button
		ButtonConnect.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){
				fileSharer.start();
			}
		});
		// Disconnect button
		ButtonDisconnect.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){
				disconnect();
			}
		});
		// Recive button
		connectRecive.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				
			}
		});
	}
	
	//Show connect button
	public void showConnect(){
		wifiConnected();
		ButtonMakeConnection.setVisibility(View.GONE);
		ButtonConnect.setVisibility(View.VISIBLE);
		ButtonDisconnect.setVisibility(View.VISIBLE);
		connectRecive.setVisibility(View.VISIBLE);
		// ButtonConnect.setText("Share to "+currentDevice.deviceName);
	}
	// Wifi disable animation
	public void wifiDisabled(){
		wifiAnimation.stop();
		mainImage.setBackgroundResource(R.drawable.wifi_failed);
		mainText.setText("Wifi Disabled");
		mainTurnOn.setVisibility(View.VISIBLE);
		ButtonMakeConnection.setVisibility(View.GONE);
		ButtonConnect.setVisibility(View.GONE);
		ButtonDisconnect.setVisibility(View.GONE);
		connectRecive.setVisibility(View.GONE);
	}
	// Wifi success animation
	public void wifiSccess(){
		wifiAnimation.stop();
		mainTurnOn.setVisibility(View.GONE);
		mainImage.setBackgroundResource(R.drawable.wifi_success_animation);
		wifiAnimation = (AnimationDrawable) mainImage.getBackground();
		wifiAnimation.start();
		mainText.setText("Ready to go on");
		ButtonMakeConnection.setVisibility(View.VISIBLE);
		ButtonConnect.setVisibility(View.GONE);
		ButtonDisconnect.setVisibility(View.GONE);
		connectRecive.setVisibility(View.GONE);
	}
	// Wifi connected animation
	public void wifiConnected(){
		wifiAnimation.stop();
		mainTurnOn.setVisibility(View.GONE);
		mainImage.setBackgroundResource(R.drawable.connected_animation);
		wifiAnimation = (AnimationDrawable) mainImage.getBackground();
		wifiAnimation.start();
		mainText.setText("Connected");
		ButtonMakeConnection.setVisibility(View.VISIBLE);
		ButtonConnect.setVisibility(View.GONE);
		ButtonDisconnect.setVisibility(View.GONE);
		connectRecive.setVisibility(View.GONE);
	}
	// wifi scanning animation
	public void wifiScan(){
		wifiAnimation.stop();
		mainTurnOn.setVisibility(View.GONE);
		mainImage.setBackgroundResource(R.drawable.wifi_animation);
		wifiAnimation = (AnimationDrawable) mainImage.getBackground();
		wifiAnimation.start();
		mainText.setText("Scanning..");
		ButtonMakeConnection.setVisibility(View.VISIBLE);
		ButtonConnect.setVisibility(View.GONE);
		ButtonDisconnect.setVisibility(View.GONE);
		connectRecive.setVisibility(View.GONE);
	}
	
	public void askPermission(String[] perm){
		activity.requestPermissions(perm,8088);
	}
	
	public void showLocationMessages(){
		if(activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
			LocationManager mm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
			if(!mm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				DialogMessage mess = new DialogMessage(activity,R.layout.ask_permission);
				mess.setAction(new DialogMessage.SetAction(){
						@Override public void setFunction(final DialogMessage k){
							TextView v1 = k.findViewById(R.id.dialogMessageHeading);
							TextView v2 = k.findViewById(R.id.dialogMessageContent);
							Button but1 = k.findViewById(R.id.dialogMessageButton1);
							Button but2 = k.findViewById(R.id.dialogMessageButton2);
							v1.setText("Please turn on location");
							v2.setText("Lets Share need loaction turned on. for working");
							but1.setOnClickListener(new View.OnClickListener(){
									@Override public void onClick(View v){
										k.dismiss();
									}
								});
							but2.setOnClickListener(new View.OnClickListener(){
									@Override public void onClick(View v){
										
										askPermission(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION});
										activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
										LocationOn = true;
										k.dismiss();
									}
								});
						}
						@Override public void setDismiss(){
							if(!LocationOn){
								activity.finish();
							}
						}
					});
				mess.show();
			}
		}else askPermission(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION});
	}
	
	public void initViews(BottomDialog dialog){
		currentDialog = dialog;
		// Buttons
		mainTurnOn = (Button)dialog.findViewById(R.id.mainButtonTurnOn);
		ButtonMakeConnection = (Button)dialog.findViewById(R.id.mainButtonMakeConnection);
		ButtonConnect = (Button)dialog.findViewById(R.id.mainButtonConnect);
		ButtonDisconnect = (Button)dialog.findViewById(R.id.mainButtonDisconnect);
		connectRecive = (Button) dialog.findViewById(R.id.mainButtonRecive);
		// Image and text
		mainImage = (ImageView)dialog.findViewById(R.id.mainImage);
		mainText = (TextView)dialog.findViewById(R.id.mainText);
		bottomFilesSelected = (TextView) dialog.findViewById(R.id.mainBottomFiles);
		// bottomFilesSelected.setText("Total "+filesSelected.size()+" files selected");
		// Wifi animation
		mainImage.setBackgroundResource(R.drawable.wifi_success_animation);
		wifiAnimation = (AnimationDrawable) mainImage.getBackground();
		wifiAnimation.start();
		setClickListeners();
	}
	
	@Override
	public void onWifiDisabled()
	{
		wifiDisabled();
	}

	@Override
	public void onWifiEnabled()
	{
		wifiSccess();
	}

	@Override
	public void onDeviceChanged()
	{
		
	}

	@Override
	public void onPeersChanged(Intent p2)
	{
		checkPeer();
	}

	@Override
	public void onConnectionChanged(Intent p2)
	{
		NetworkInfo in = p2.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
		if(in.isConnected()){
			showConnect();
			wifiP2pManager.requestConnectionInfo(wifiChannel,new ConnectionInfoListener(){
				@Override public void onConnectionInfoAvailable(WifiP2pInfo in2){
					info = in2;
					if(info.groupFormed){
						groupOwnerAdress = info.groupOwnerAddress;
						if(info.isGroupOwner){
							fileSharer = new FileShareManager(activity);
							Log.i(TAG,"You are the group owner");
						}else{
							fileSharer = new FileShareManager(activity,info.groupOwnerAddress);
							Log.i(TAG,"You are a group member");
						}
					}
				}
			});
		}
	}
	
	public void setInterface(ManagerInterface l){
		listener = l;
	}
}
