package com.samsung.spensdk.example.insertstamp;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.samm.common.SObjectImage;
import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.PreferencesOfSAMMOption;
import com.samsung.spensdk.example.tools.SPenSDKUtils;
import com.samsung.spensdk.example.tools.ToolStampList;


public class SPen_Example_InsertStamp extends Activity {

	private final String TAG = "SPenSDK Sample";

	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	private final String APPLICATION_ID_NAME = "SDK Sample Application";
	private final int APPLICATION_ID_VERSION_MAJOR = 1;
	private final int APPLICATION_ID_VERSION_MINOR = 0;
	private final String APPLICATION_ID_VERSION_PATCHNAME = "Debug";

	//==============================
	// Variables
	//==============================
	Context mContext = null;

	private FrameLayout	mLayoutContainer;
	private RelativeLayout	mCanvasContainer;
	private SCanvasView		mSCanvas;
	private ImageView		mStampBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;

	private final int REQUEST_CODE_INSERT_STAMP_OBJECT = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.editor_insert_stamp);

		mContext = this;

		//------------------------------------
		// UI Setting
		//------------------------------------
		mStampBtn = (ImageView) findViewById(R.id.stampBtn);
		mStampBtn.setOnClickListener(mBtnClickListener);

		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);

		//------------------------------------
		// Create SCanvasView
		//------------------------------------
		mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);

		mSCanvas = new SCanvasView(mContext);        
		mCanvasContainer.addView(mSCanvas);

		//------------------------------------
		// SettingView Setting
		//------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(false, false, false, false);
		// Talk & Description Setting by Locale
		SPenSDKUtils.addTalkbackAndDescriptionStringResourceMap(settingResourceMapInt);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(false, false, false, false);
		// Create Setting View
		mSCanvas.createSettingView(mLayoutContainer, settingResourceMapInt, settingResourceMapString);


		//====================================================================================
		//
		// Set Callback Listener(Interface)
		//
		//====================================================================================
		//------------------------------------------------
		// SCanvas Listener
		//------------------------------------------------
		mSCanvas.setSCanvasInitializeListener(new SCanvasInitializeListener() {
			@Override
			public void onInitialized() { 
				//--------------------------------------------
				// Start SCanvasView/CanvasView Task Here
				//--------------------------------------------
				// Application Identifier Setting
				if(!mSCanvas.setAppID(APPLICATION_ID_NAME, APPLICATION_ID_VERSION_MAJOR, APPLICATION_ID_VERSION_MINOR,APPLICATION_ID_VERSION_PATCHNAME))
					Toast.makeText(mContext, "Fail to set App ID.", Toast.LENGTH_LONG).show();

				// Set Title
				if(!mSCanvas.setTitle("SPen-SDK Test"))
					Toast.makeText(mContext, "Fail to set Title.", Toast.LENGTH_LONG).show();

				// Set Pen Only Mode with Finger Control
				mSCanvas.setFingerControlPenDrawing(true);
			}
		});

		//------------------------------------------------
		// History Change Listener
		//------------------------------------------------
		mSCanvas.setHistoryUpdateListener(new HistoryUpdateListener() {
			@Override
			public void onHistoryChanged(boolean undoable, boolean redoable) {
				mUndoBtn.setEnabled(undoable);
				mRedoBtn.setEnabled(redoable);
			}
		});

		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);

		// Caution:
		// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView Task at onInitialized() of SCanvasInitializeListener
	}

	@Override
	protected void onDestroy() {	
		super.onDestroy();
		// Release SCanvasView resources
		if(!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
	}

	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
	} 


	private OnClickListener undoNredoBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mUndoBtn)) {
				mSCanvas.undo();
			}
			else if (v.equals(mRedoBtn)) {
				mSCanvas.redo();
			}
			mUndoBtn.setEnabled(mSCanvas.isUndoable());
			mRedoBtn.setEnabled(mSCanvas.isRedoable());
		}
	};

	OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SPen_Example_InsertStamp.this, ToolStampList.class);
			startActivityForResult(intent, REQUEST_CODE_INSERT_STAMP_OBJECT);	
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Check result error
		if(resultCode!=RESULT_OK)
			return;		
		if(data == null)
			return;

		if(requestCode == REQUEST_CODE_INSERT_STAMP_OBJECT) {    			
			String strStampPath = data.getStringExtra("stamp_path");

			Bitmap bm = null;
			try {
				bm = BitmapFactory.decodeStream(mContext.getAssets().open(strStampPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(bm == null)
				return;

			// canvas option setting
			SOptionSCanvas canvasOption = mSCanvas.getOption();					
			if(canvasOption == null)
				return;

			if(canvasOption.mSAMMOption == null)
				return;

			canvasOption.mSAMMOption.setContentsQuality(PreferencesOfSAMMOption.getPreferenceSaveImageQuality(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);			

			RectF rectF = getDefaultRect(bm);
			int nContentsQualityOption = canvasOption.mSAMMOption.getContentsQuality();
			SObjectImage sImageObject = new SObjectImage(nContentsQualityOption);
			sImageObject.setRect(rectF);
			sImageObject.setImagePath(strStampPath);
			sImageObject.setImageBitmap(bm);

			if(mSCanvas.insertSAMMImage(sImageObject, true)){
				Toast.makeText(this, "Insert image file("+ strStampPath +") Success!", Toast.LENGTH_SHORT).show();	
			}
			else{
				Toast.makeText(this, "Insert image file("+ strStampPath +") Fail!", Toast.LENGTH_LONG).show();    				
			}
		}
	}

	RectF getDefaultRect(Bitmap bm){
		// Rect Region : Consider image real size		
		int nImageWidth = bm.getWidth();
		int nImageHeight = bm.getHeight();
		int nScreenWidth = mSCanvas.getWidth();
		int nScreenHeight = mSCanvas.getHeight();    			
		int nBoxRadius = (nScreenWidth>nScreenHeight) ? nScreenHeight/8 : nScreenWidth/8;
		int nCenterX = nScreenWidth/2;
		int nCenterY = nScreenHeight/2;
		if(nImageWidth > nImageHeight)
			return new RectF(nCenterX-nBoxRadius,nCenterY-(nBoxRadius*nImageHeight/nImageWidth),nCenterX+nBoxRadius,nCenterY+(nBoxRadius*nImageHeight/nImageWidth));
		else
			return new RectF(nCenterX-(nBoxRadius*nImageWidth/nImageHeight),nCenterY-nBoxRadius,nCenterX+(nBoxRadius*nImageWidth/nImageHeight),nCenterY+nBoxRadius);
	}

}
