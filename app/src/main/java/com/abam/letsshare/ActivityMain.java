/*
MADE BY ASWANTH VC
******************
Copy Righyed content dont edit or republish.
© ABAM
*/
package com.abam.letsshare;

import android.support.v7.app.*;
import android.os.*;
import android.support.v7.widget.*;
import android.widget.*;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.design.widget.*;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.*;
import android.view.View.*;
import android.app.Dialog;
import android.support.v4.content.*;
import android.*;
import android.content.pm.*;
import android.content.*;
import android.net.wifi.p2p.*;

public class ActivityMain extends AppCompatActivity
{
	private DrawerLayout mDrawer;
	private NavigationView mNavView;
	private Toolbar mToolbar;
	private ActionBarDrawerToggle mToogle;
	private ActionListener listener;
	private BottomNavigationView bottomNav;
	private Button shareButton;
	private Button receiveButton;
	private TextView connectTextBottom;
	private FileFragmet frag2;
	public ActivityMain This;
	// OnCreate
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		This = this;
		initDrawer();
		initBottomNavigation();
		setConnectButton();
		getPermission();
	}
	// Asks for needed permissions
	public boolean getPermission(){
		DialogMessage m = new DialogMessage(this,R.layout.ask_permission);
		m.setAction(new DialogMessage.SetAction(){
			@Override public void setFunction(final DialogMessage m){
				TextView head = m.findViewById(R.id.dialogMessageHeading);
				TextView content = m.findViewById(R.id.dialogMessageContent);
				Button but1 = m.findViewById(R.id.dialogMessageButton1);
				Button but2 = m.findViewById(R.id.dialogMessageButton2);
				head.setText("Allow Permissions");
				content.setText(R.string.need_permission);
				but1.setOnClickListener(new OnClickListener(){
					@Override public void onClick(View v){
						m.dismiss();
						finish();
					}
				});
				but2.setOnClickListener(new OnClickListener(){
					@Override public void onClick(View v){
						m.dismiss();
						requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION },9836);
						getPermission();
					}
				});
			}
			@Override public void setDismiss(){
				
			}
		});
		if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			m.show();
		else setFileFrame();
		return false;
	}
	// Initialize bottom connect dialog
	public void openBottomDialog(String type){
		BottomDialog mDialog = new BottomDialog(this,R.layout.main,type);
		mDialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
			@Override
			public void onDismiss(DialogInterface m){
				/*if(checkWifiStatus()){
					//connectButton.setBackgroundResource(R.drawable.connected_button);
					//connectTextBottom.setText("Connected");
				}*/
			}
		});
		mDialog.setListener(new BottomDialog.SetActions(){
			@Override
			public void setDeviceList(Manager m){
				m.setFilesSelected(frag2.SelectedFiles);
			}
		});
		mDialog.show();
	}
	// Initialize connect button
	private void setConnectButton(){
		connectTextBottom = (TextView) findViewById(R.id.main_activityConnectedText);
		connectTextBottom.setText("Connect");
		shareButton = (Button) findViewById(R.id.main_activityConnectButton);
		shareButton.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){
				openBottomDialog("snd");
			}
		});
		receiveButton = (Button) findViewById(R.id.main_activityReceive);
		receiveButton.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				openBottomDialog("rcv");
			}
		});
		
	}
	// Set file frame Layout
	private void setFileFrame(){
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){
			@Override public void run(){
				frag2 = new FileFragmet(This);
				frag2.mainAc = This;
				Fragment frag = frag2;
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.frame_container,frag,"FileFragment");
				transaction.commit();
			}
		},200);
	}
	// initialize bottom navigatio  view
	public void initBottomNavigation(){
		bottomNav = (BottomNavigationView) findViewById(R.id.navigation);
		bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
			@Override public boolean onNavigationItemSelected(MenuItem item){
				itemSelection(item.getItemId());
				mDrawer.closeDrawers();
				return true;
			}
		});
	}
	public void itemSelection(int item){
		switch(item){
			case android.R.id.home:
				mDrawer.openDrawer(Gravity.START);
				break;
			case R.id.navigation_item1:
			case R.id.drawer_navigation_item1:
				setFileFrame();
				break;
			case R.id.navigation_item2:
			case R.id.drawer_navigation_item2:
				
				break;
			case R.id.navigation_item3:
			case R.id.drawer_navigation_item3:
				
				break;
			case R.id.navigation_item4:
			case R.id.drawer_navigation_item4:
				
				break;
		}
	}
	// Open drawer by handler
	public void openDrawerAction(){
		new Handler().postDelayed(new Runnable(){
			@Override public void run(){
				mDrawer.openDrawer(Gravity.START);
			}
		},20);
	}
	// Close drawer by handler
	public void closeDrawerAction(){
		new Handler().postDelayed(new Runnable(){
				@Override public void run(){
					mDrawer.closeDrawers();
				}
			},20);
	}
	// Init drawer
	public void initDrawer(){
		mDrawer = (DrawerLayout)findViewById(R.id.mainActivitydrawer_layout);
		
		mNavView = (NavigationView)findViewById(R.id.mainActivity_drawer);
		mToolbar = (Toolbar)findViewById(R.id.mainActivitytoolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		mToogle = new ActionBarDrawerToggle(this,mDrawer,mToolbar,R.string.drawer_open,R.string.drawer_close);
		mDrawer.setDrawerListener(mToogle);
		// Drawer listener working with
		mDrawer.setDrawerListener(new DrawerLayout.DrawerListener(){
			@Override public void onDrawerStateChanged(int p1){
				// Drawer State changed
			}
			@Override public void onDrawerClosed(View v){
				// Drawer opened
				closeDrawerAction();
			}
			@Override public void onDrawerSlide(View v,float p1){
				// Drawer slide
			}
			@Override public void onDrawerOpened(View v){
				// Drawer Opened
				openDrawerAction();
			}
		});
		mToogle.syncState();
		mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
			@Override public boolean onNavigationItemSelected(MenuItem item){
				item.setCheckable(true);
				itemSelection(item.getItemId());
				closeDrawerAction();
				return true;
			}
		});
		mNavView.inflateHeaderView(R.layout.drawer_header);
	}
	// Set action lister
	public void setActionListener(ActionListener l){
		listener = l;
	}
	// Interface for onBackPresa etc. to use in fragments
	public interface ActionListener{
		boolean onBackPressed();
	}
	// On back pressed
	@Override
	public void onBackPressed()
	{
		if(listener != null)
		if(!listener.onBackPressed()){
			super.onBackPressed();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.options_menu,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		itemSelection(item.getItemId());
		return true;
	}
	
}
