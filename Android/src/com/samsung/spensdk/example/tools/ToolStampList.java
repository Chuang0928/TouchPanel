package com.samsung.spensdk.example.tools;

import java.io.IOException;

import com.samsung.spensdk.example.R;

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

public class ToolStampList extends Activity {

	private Context mContext;
	private String mAssetPath="spen_sdk_resource_stamp";;
	private String[] mAsetsList;
	private GridView mImageGrid;
	private ImageGridAdapter mImageAdapter;
	private Bitmap[] mBitmaplist;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.tool_stamp_list);
		mContext = this;	 

		mImageGrid = (GridView)findViewById(R.id.stampLayout);
		mImageGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {				
				Intent resultInt = new Intent();
				resultInt.putExtra("stamp_path", mAssetPath + "/" + mAsetsList[position]);
				setResult(RESULT_OK, resultInt);

				finish();
			}
		});
	}

	private void readAssets() {
		AssetManager am = this.getAssets();
		try {
			mAsetsList = am.list(mAssetPath);

		} catch (IOException e) {
			e.printStackTrace();
		}		

		mBitmaplist = new Bitmap[mAsetsList.length];

		mImageAdapter= new ImageGridAdapter(this, am, mAsetsList);
		mImageGrid.setAdapter(mImageAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		readAssets();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mBitmaplist != null){
			int nCount = mBitmaplist.length;
			if (nCount > 0){
				for(; nCount > 0; nCount--){
					if (mBitmaplist[nCount - 1] != null){
						mBitmaplist[nCount - 1].recycle();
						mBitmaplist[nCount - 1] = null;
					}
				}
			}
		}

		if (mImageAdapter != null)
			mImageAdapter = null;
		if (mAsetsList != null)
			mAsetsList = null;
		if (mImageGrid != null)
			mImageGrid = null;

		System.gc();
	}

	public class ImageGridAdapter extends ArrayAdapter<String> {

		public ImageGridAdapter(Context context, AssetManager am, String[] objects) {
			super(context, 0, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.tool_stamp_list_item, parent, false);
			}
			ImageView img = (ImageView)convertView.findViewById(R.id.stamp);
			try {				
				if(mBitmaplist[position] == null) {
					mBitmaplist[position] = BitmapFactory.decodeStream(
							mContext.getAssets().open(mAssetPath + "/" + mAsetsList[position]));
				}
				img.setImageBitmap(mBitmaplist[position]);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return convertView;
		}

		public void updateDisplay() {
			this.notifyDataSetChanged();
		}
	}
}
