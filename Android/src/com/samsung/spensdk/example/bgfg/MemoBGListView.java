package com.samsung.spensdk.example.bgfg;

import java.io.IOException;

import com.samsung.spen.lib.image.SPenImageProcess;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.bgfg.SPen_Example_BackgroundForeground;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class MemoBGListView extends Activity {	
	private GridView momoBGGrid;
	private String[] momoBGList;
	private Bitmap[] momoBGBitmapList;
	private MemoGridAdapter momoBGAdapter;

	boolean bSelectBGBaseMode = false;
	String selectedBGBaseFileName;	
	Bitmap bmBGBase = null;
	Bitmap bmBGThumb = null;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memo_layout);

		Intent intent = getIntent();
		// select BG base or BG pattern
		bSelectBGBaseMode = intent.getBooleanExtra(SPen_Example_BackgroundForeground.EXTRA_SELECT_BG_BASE_MDOE, false);			

		if(!bSelectBGBaseMode){
			selectedBGBaseFileName = intent.getStringExtra(SPen_Example_BackgroundForeground.EXTRA_BG_BASE_FILENAME);				
			try {			
				bmBGBase = BitmapFactory.decodeStream(this.getAssets().open(SPen_Example_BackgroundForeground.BG_BASE_ASSET_PATH + "/" + selectedBGBaseFileName));			
			} catch (IOException e) {				
				e.printStackTrace();
			}			
		}


		momoBGGrid = (GridView) findViewById(R.id.memoLayout);
		momoBGGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {	
				Intent result = new Intent();

				if(bSelectBGBaseMode){
					result.putExtra(SPen_Example_BackgroundForeground.EXTRA_BG_BASE_FILENAME, momoBGList[position]);

					//BG Base None
					if(position==0)
						result.putExtra(SPen_Example_BackgroundForeground.EXTRA_BG_BASE_NONE, true);
					else
						result.putExtra(SPen_Example_BackgroundForeground.EXTRA_BG_BASE_NONE, false);

				}else{					
					result.putExtra(SPen_Example_BackgroundForeground.EXTRA_BG_BASE_FILENAME, selectedBGBaseFileName);
					result.putExtra(SPen_Example_BackgroundForeground.EXTRA_BG_PATTERN_FILENAME, momoBGList[position]);
				}

				setResult(RESULT_OK, result);
				finish();
			}
		});	

		readMemoBGImage();
	}	


	private void readMemoBGImage() {
		AssetManager am = this.getAssets();
		if(am == null)
			return;

		try {
			if(bSelectBGBaseMode)		
				momoBGList = am.list(SPen_Example_BackgroundForeground.BG_BASE_ASSET_PATH);
			else
				momoBGList = am.list(SPen_Example_BackgroundForeground.BG_PATTERN_ASSET_PATH);

			if(momoBGList.length > 0) {				
				momoBGBitmapList = new Bitmap[momoBGList.length];
				momoBGAdapter = new MemoGridAdapter(this, momoBGList);
				momoBGGrid.setAdapter(momoBGAdapter);			
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {		
		super.onDestroy();
		if (momoBGBitmapList != null) {
			for (int i = 0; i < momoBGBitmapList.length; i++) {
				if (momoBGBitmapList[i] != null) {
					momoBGBitmapList[i].recycle();
					momoBGBitmapList[i] = null;
				}
			}
		}
		if (momoBGList != null)
			momoBGList = null;
		if (momoBGGrid != null)
			momoBGGrid = null;

		System.gc();		
	}

	public class MemoGridAdapter extends ArrayAdapter<String> {
		private Context mContext;
		int nThumbnailWidth = 300;
		int nThumbnailHeight = 300;
		public MemoGridAdapter(Context context, String[] objects) {
			super(context, 0, objects);
			mContext = context;	
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {		
			if (convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();				
				convertView = inflater.inflate(R.layout.memo_item, parent, false);
			}		

			ImageView img = (ImageView) convertView.findViewById(R.id.memo);					

			try {
				if(momoBGBitmapList[position] == null){
					if(bSelectBGBaseMode){
						momoBGBitmapList[position] = BitmapFactory.decodeStream(mContext.getAssets().open(SPen_Example_BackgroundForeground.BG_BASE_ASSET_PATH + "/" + momoBGList[position]));
						img.setImageBitmap(momoBGBitmapList[position]);
					}
					else{										
						momoBGBitmapList[position] = BitmapFactory.decodeStream(mContext.getAssets().open(SPen_Example_BackgroundForeground.BG_PATTERN_ASSET_PATH + "/" + momoBGList[position]));						

						// get combined two bitmaps						
						bmBGThumb = SPenImageProcess.getCombinedImage(nThumbnailWidth, nThumbnailHeight, bmBGBase, momoBGBitmapList[position]);								
						img.setImageBitmap(bmBGThumb);						
					}
				}						
			} catch (IOException e) {
				e.printStackTrace();
			}			

			return convertView;
		}
	}	

}
