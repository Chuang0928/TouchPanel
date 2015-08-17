package com.samsung.spensdk.example.spenhover_general;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

public class SPen_Example_SPenHoverPointerGeneral extends Activity {

	private Context mContext = null;
	private SPenEventLibrary mSPenEventLibrary;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.general_purpose_hover_pointer);
		TextView view;

		mContext = this;
		mSPenEventLibrary = new SPenEventLibrary();

		view = (TextView)findViewById(R.id.cursor);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_CURSOR);
		view = (TextView)findViewById(R.id.split01);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_SPLIT_01);
		view = (TextView)findViewById(R.id.split02);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_SPLIT_02);
		view = (TextView)findViewById(R.id.resize01);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_03);
		view = (TextView)findViewById(R.id.resize02);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_02);
		view = (TextView)findViewById(R.id.resize03);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_04);
		view = (TextView)findViewById(R.id.resize04);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_01);
		view = (TextView)findViewById(R.id.move);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_MOVE);        
		view = (TextView)findViewById(R.id.resize05);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_01);
		view = (TextView)findViewById(R.id.resize06);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_04);
		view = (TextView)findViewById(R.id.resize07);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_02);
		view = (TextView)findViewById(R.id.resize08);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_03);
		view = (TextView)findViewById(R.id.circle);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_MORE);        
		view = (TextView)findViewById(R.id.scroll01);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SCROLLICON_POINTER_08);
		view = (TextView)findViewById(R.id.scroll02);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SCROLLICON_POINTER_01);
		view = (TextView)findViewById(R.id.scroll03);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SCROLLICON_POINTER_02);
		view = (TextView)findViewById(R.id.scroll04);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SCROLLICON_POINTER_07);
		view = (TextView)findViewById(R.id.hide);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_HIDE);        
		view = (TextView)findViewById(R.id.scroll05);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SCROLLICON_POINTER_03);
		view = (TextView)findViewById(R.id.scroll06);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SCROLLICON_POINTER_06);
		view = (TextView)findViewById(R.id.scroll07);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SCROLLICON_POINTER_05);
		view = (TextView)findViewById(R.id.scroll08);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SCROLLICON_POINTER_04);

		Button button = (Button)findViewById(R.id.custom);
		button.setOnClickListener(new OnClickListener() {

			boolean bCustomDrawable = false;
			@Override
			public void onClick(View arg0) {
				bCustomDrawable = !bCustomDrawable;
				if(bCustomDrawable){
					mSPenEventLibrary.setSPenCustomHoverIcon(mContext, arg0, getResources().getDrawable(R.drawable.hover_ic_point) );
					TextView btnText = (TextView) findViewById(R.id.custom);
					btnText.setText("DEFAULT BUTTON");
				}
				else{
					mSPenEventLibrary.setSPenCustomHoverIcon(mContext, arg0, null );
					TextView btnText = (TextView) findViewById(R.id.custom);
					btnText.setText("CUSTOM BUTTON");
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
	}
}
