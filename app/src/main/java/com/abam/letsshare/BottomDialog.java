package com.abam.letsshare;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.support.v7.app.AppCompatActivity;
public class BottomDialog extends Dialog
{
	private int layoutIs;
	public ActivityMain activity;
	private SetActions listener;
	private String type;
	private Button btn1;
	private Button btn2;
	// Normal Constructor
	public BottomDialog(ActivityMain a,String t){
		super(a,R.style.MaterialDialogSheet);
		activity = a;
		layoutIs = R.layout.bottom_popup;
		type = t;
	}
	// Constructor with layout defined
	public BottomDialog(ActivityMain a,int layout,String t){
		super(a,R.style.MaterialDialogSheet);
		activity = a;
		layoutIs = layout;
		type = t;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		//View v = findViewById(R.id.drawer_layout);
		setContentView(R.layout.main);
		getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.BOTTOM);
		switch(type){
			case "snd":
				final Manager manager = new Manager(activity);
				manager.initViews(this);
				if(listener != null) listener.setDeviceList(manager);
				/*btn2.setOnClickListener(new View.OnClickListener(){
					@Override public void onClick(View v){
						manager.turnOffHotspot();
					}
				});*/
				break;
			case "rcv":
				//recive();
				break;
		}
	}
	
	public void setListener(SetActions m){
		listener = m;
	}
	// Set interface
	public interface SetActions{
		void setDeviceList(Manager m);
	}
}
