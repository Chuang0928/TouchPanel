package com.samsung.spensdk.example.sammeditor;

import java.io.File;
import java.util.ArrayList;

import com.samsung.spensdk.example.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SPen_Example_VideoDemoFileList extends Activity {

	Context mContext = null;

	private Button BtnOK, BtnCancel;
	private ListView lView;		
	private ArrayList<String> userVideoList;
	private ListAdapter listAdapter = null;		
	private int m_nCurVideoFileIndex;
	private String strFilepath = Environment.getExternalStorageDirectory().getAbsolutePath();
	private String m_strVideoFileName = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_file_list);

		mContext = this;		
		m_nCurVideoFileIndex = -1;

		// load sound file list
		userVideoList = new ArrayList<String>();        
		videoFileListUp(strFilepath);

		listAdapter = new ListAdapter(this);
		lView = (ListView)findViewById(R.id.videoList);
		lView.setAdapter(listAdapter);

		lView.setItemsCanFocus(false);
		lView.setTextFilterEnabled(true);
		lView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				m_nCurVideoFileIndex = position;		
				m_strVideoFileName = userVideoList.get(m_nCurVideoFileIndex);				
				listAdapter.notifyDataSetChanged();
				listAdapter.updateDisplay();
			}
		});

		BtnOK = (Button) findViewById(R.id.btnOK);
		BtnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(m_strVideoFileName != null) {
					//	Intent intent = new Intent(VideoDemoFileList.this, MediaPlayerDemo_FromFile.class);
					//	intent.putExtra("videofilename", m_strVideoFileName);
					//	startActivity(intent);
					Intent intent = new Intent();
					intent.putExtra("videofilename", m_strVideoFileName);
					setResult(RESULT_OK, intent);
					finish();

				}
				else {
					Toast.makeText(mContext, "Select valid videofile!!!!", Toast.LENGTH_SHORT).show();					
				}
			}
		});        
		BtnCancel = (Button) findViewById(R.id.btnCancel);
		BtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				finish();
			}
		});        


		// update button and listview
		if(userVideoList.size() <= 0) {
			BtnOK.setEnabled(false);			
			lView.setVisibility(View.GONE);
			findViewById(R.id.noItemListMsg).setVisibility(View.VISIBLE);
		}
		else {
			BtnOK.setEnabled(true);
			lView.setVisibility(View.VISIBLE);
			findViewById(R.id.noItemListMsg).setVisibility(View.GONE);
		}   
	}	

	public void videoFileListUp(String filepath) {
		if(filepath == null)
			return;		

		File file = new File(filepath);
		File[] files = file.listFiles();
		if (files == null)
			return;

		for(int i = 0;i < files.length;i++) {	
			if(files[i].isDirectory()) {
				strFilepath = files[i].getPath();
				videoFileListUp(strFilepath);
			}
			else {
				int nExtIndex = files[i].getName().lastIndexOf(".");				
				String strExt = files[i].getName().substring(nExtIndex + 1);
				// Supporting audio type
				if(strExt.compareToIgnoreCase("3gp")==0 ||	strExt.compareToIgnoreCase("mp4")==0) {
					userVideoList.add(files[i].getPath());
				}
			}
		}
	}

	public class ListAdapter extends BaseAdapter {	
		//private Context mContext;

		public ListAdapter(Context context) {
			//mContext = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.video_file_list_item, parent, false);
			}

			String strPath = userVideoList.get(position);
			int ntemp = strPath.lastIndexOf("/");			
			String strTitle = strPath.substring(ntemp+1); 
			TextView textText=  (TextView)convertView.findViewById(R.id.itemText);
			textText.setText(strTitle);
			if(position == m_nCurVideoFileIndex) {
				textText.setTextColor(0xFF00FF00);
			}
			else {
				textText.setTextColor(0xFFFFFFFF);
			}

			RadioButton radio = (RadioButton)convertView.findViewById(R.id.checkradio);
			if(position == m_nCurVideoFileIndex) radio.setChecked(true);
			else radio.setChecked(false);

			return convertView; 
		}	

		public void updateDisplay() {
			this.notifyDataSetChanged();
		}       

		@Override
		public int getCount() {
			if(userVideoList==null)
				return 0;
			return userVideoList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}	
}


