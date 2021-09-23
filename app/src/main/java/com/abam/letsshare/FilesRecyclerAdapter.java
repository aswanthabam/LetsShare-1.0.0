/*
MADE BY ASWANTH VC
******************
Copy Righted content dont edit or republish
© ABAM
*/
package com.abam.letsshare;
import android.support.v7.widget.*;
import java.util.*;
import android.view.*;
import android.view.View.*;
import android.content.*;
import android.widget.*;
import java.io.*;
import android.net.*;
import android.webkit.*;
import android.graphics.*;
import android.provider.*;
import android.media.*;
import android.util.*;
import android.graphics.drawable.*;
import android.support.v7.app.*;
import android.database.*;

public class FilesRecyclerAdapter extends RecyclerView.Adapter<FilesRecyclerAdapter.ViewHolder>
{
	private Context context;
	private List<File> mData = new ArrayList<File>();
	public boolean showHiddenFiles = false;
	private ItemClickListener listener;
	public String TAG = "FILES_RECYCLER_ADAPTER";
	private AppCompatActivity activity;
	// Initialize
	public FilesRecyclerAdapter(AppCompatActivity a,List<File> k){
		activity =a;
		context = a.getApplicationContext();
		mData = k;
	}
	// Create view holder for poition
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		View v = LayoutInflater.from((p1.getContext())).inflate(R.layout.files_view_1,p1,false);
		final ViewHolder hol = new ViewHolder(v);
		return hol;
	}
	// Code to execute when holdee is attached
	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position)
	{
		//if(position == 0) if(listener != null) listener.onInit();
		new Thread(new Runnable(){
			@Override public void run(){
				bindView(holder,position);
			}
		}).start();
	}
	// Bind View Holder
	public void bindView(final ViewHolder holder,final int position){
		final File data = mData.get(position);
		final String extension = MimeTypeMap.getFileExtensionFromUrl(data.getAbsolutePath());
		String type = null;
		if(extension != null) type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		if(type == null) type = "Unknown";
		if(data.isDirectory()){
			activity.runOnUiThread(new Runnable(){
				@Override public void run(){
					holder.thumbnail.setBackgroundResource(R.drawable.folder_image);
					holder.FileType.setText("Folder");
				}
			});
		}else if(type.startsWith("image/")){
			activity.runOnUiThread(new Runnable(){
				@Override public void run(){
					holder.thumbnail.setImageResource(R.drawable.image_icon);
					holder.FileType.setText("Image");
				}
			});
			try{
				//final Drawable im = Drawable.creat(data.getAbsolutePath());
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inJustDecodeBounds = false;
				final Bitmap im = getResizedBitmap(BitmapFactory.decodeFile(data.getAbsolutePath(),opt),100);
				activity.runOnUiThread(new Runnable(){
					@Override public void run(){
						holder.thumbnail.setImageBitmap(im);
					}
				});
			}catch(Exception e){Log.e(TAG,"Cant load image thumbnail. Caused by("+e.toString()+")");}
		}else if(type.startsWith("application/pdf")){
			activity.runOnUiThread(new Runnable(){
				@Override public void run(){
					holder.FileType.setText("PDF");
					holder.thumbnail.setBackgroundResource(R.drawable.pdf_file);
				}
			});
		}
		else{
			activity.runOnUiThread(new Runnable(){
				@Override public void run(){
					holder.thumbnail.setBackgroundResource(R.drawable.unknown_file);
					holder.FileType.setText(extension);
				}
			});
		}
		activity.runOnUiThread(new Runnable(){
			@Override public void run(){
				holder.filename.setText(data.getName());
				if(listener != null) listener.selectionHighlight(data,holder);
			}
		});
	}
	public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
		int width = image.getWidth();
		int height = image.getHeight();

		float bitmapRatio = (float)width / (float) height;
		if (bitmapRatio > 1) {
			width = maxSize;
			height = (int) (width / bitmapRatio);
		} else {
			height = maxSize;
			width = (int) (height * bitmapRatio);
		}
		return Bitmap.createScaledBitmap(image, width, height, true);
	}
	
	// Main view holder class
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		TextView filename;
		ImageView thumbnail;
		TextView FileType;
		LinearLayout mainLayout;
		public ViewHolder(View v){
			super(v);
			final ViewHolder holder = this;
			filename = v.findViewById(R.id.files_view_filename);
			thumbnail = v.findViewById(R.id.files_view_Thumbnail);
			FileType = v.findViewById(R.id.files_view_FileType);
			mainLayout = v.findViewById(R.id.files_view_1LinearLayout);
			v.setOnClickListener(this);
		}
		@Override public void onClick(View view){
			int position  = getAdapterPosition();
			listener.onClick(position,mData.get(position),this);
		}
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}
	@Override
	public int getItemViewType(int position)
	{
		return position;
	}
	// Get item count
	@Override
	public int getItemCount()
	{
		return (mData != null) ? mData.size() : 0;
	}
	
	// Set click listener
	public void setClickListener(ItemClickListener i){
		listener = i;
	}
	// Click interface
	public interface ItemClickListener{
		void onClick(int position,File file,ViewHolder v);
		void onBindFinished();
		void selectionHighlight(File file,ViewHolder v);
	}
}
