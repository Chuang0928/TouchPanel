package com.samsung.spensdk.example.canvassize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.samm.common.SOptionSAMM;
import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

public class SPen_Example_CanvasSizeStatic extends Activity {

	private final String TAG = "SPenSDK Sample";
	private Context mContext = null;

	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	public static String APPLICATION_ID_NAME = "StaticCanvasSize";
	public static int APPLICATION_ID_VERSION_MAJOR = 1;
	public static int APPLICATION_ID_VERSION_MINOR = 0;
	public static String APPLICATION_ID_VERSION_PATCHNAME = "Debug";

	public static final String DEFAULT_APP_IMAGEDATA_DIRECTORY = "SPenSDK/"+APPLICATION_ID_NAME;
	public static final String DEFAULT_APP_IMAGEDATA_FILENAME = "Test.png";
	private static final boolean USE_SAMM_FORMAT = false;

	private File mFolder = null;

	private Bitmap 	mBGBitmap;

	private final int STATIC_CANVAS_WIDTH = 1000;
	private final int STATIC_CANVAS_HEIGHT = 2000;

	private final int	 CANVASVIEW_WIDTH_MARGIN = 40;
	private final int	 CANVASVIEW_HEIGHT_MARGIN = 260;
	private int mCanvasViewWidth;
	private int mCanvasViewHeight;

	private FrameLayout	mLayoutContainer;
	private RelativeLayout	mCanvasContainer;
	private SCanvasView		mSCanvas;
	private ImageView		mOpenBtn;
	private ImageView		mPenBtn;
	private ImageView		mEraserBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;
	private ImageView		mSaveBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.editor_canvas_size_static);
		mContext = this;

		//------------------------------------
		// UI Setting
		//------------------------------------
		mPenBtn = (ImageView) findViewById(R.id.penBtn);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);

		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);

		mOpenBtn = (ImageView) findViewById(R.id.openBtn);
		mOpenBtn.setOnClickListener(openBtnClickListener);

		mSaveBtn = (ImageView) findViewById(R.id.saveBtn);
		mSaveBtn.setOnClickListener(saveBtnClickListener);

		//------------------------------------
		// Create SCanvasView
		//------------------------------------
		mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);

		mSCanvas = new SCanvasView(mContext);
		mCanvasContainer.addView(mSCanvas);

		mBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.letter_bg_grass);

		// DO NOT set canvas size statically here. 		
		//mSCanvas.createSCanvasView(STATIC_CANVAS_WIDTH, STATIC_CANVAS_HEIGHT);

		// Set Background of layout container
		mLayoutContainer.setBackgroundResource(R.drawable.bg_edit);


		//------------------------------------
		// SettingView Setting
		//------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, false, false);
		// Talk & Description Setting by Locale
		SPenSDKUtils.addTalkbackAndDescriptionStringResourceMap(settingResourceMapInt);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, false, false);
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
		SCanvasInitializeListener mSCanvasInitializeListener = new SCanvasInitializeListener() {
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

				// Set Initial Setting View Size
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);				

				// Set Pen Only Mode with Finger Control	
				mSCanvas.setFingerControlPenDrawing(true);			

				// Prevent SCanvasView boundary scroll by touching
				mSCanvas.setEnableBoundaryTouchScroll(false);		

				// Update button state
				updateModeState();

				// Set Background Image
				if(!mSCanvas.setBGImage(mBGBitmap)){
					Toast.makeText(mContext, "Fail to set Background Image Bitmap.", Toast.LENGTH_LONG).show();
				}
			}
		};

		//------------------------------------------------
		// History Change
		//------------------------------------------------
		HistoryUpdateListener mHistoryUpdateListener = new HistoryUpdateListener() {
			@Override
			public void onHistoryChanged(boolean undoable, boolean redoable) {
				mUndoBtn.setEnabled(undoable);
				mRedoBtn.setEnabled(redoable);
			}
		};

		//------------------------------------------------
		// SCanvas Mode Changed Listener 
		//------------------------------------------------
		SCanvasModeChangedListener mModeChangedListener = new SCanvasModeChangedListener() {

			@Override
			public void onModeChanged(int mode) {
				updateModeState();				
			}

			@Override
			public void onMovingModeEnabled(boolean bEnableMovingMode) {
				updateModeState();
			}

			@Override
			public void onColorPickerModeEnabled(boolean bEnableColorPickerMode) {
				updateModeState();
			}
		};


		// Register Application Listener
		mSCanvas.setSCanvasInitializeListener(mSCanvasInitializeListener);
		mSCanvas.setHistoryUpdateListener(mHistoryUpdateListener);
		mSCanvas.setSCanvasModeChangedListener(mModeChangedListener);


		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);

		// create basic save/road file path
		File sdcard_path = Environment.getExternalStorageDirectory();
		mFolder =  new File(sdcard_path, DEFAULT_APP_IMAGEDATA_DIRECTORY);
		if(!mFolder.exists()){
			if(!mFolder.mkdirs()){
				Log.e(TAG, "Default Save Path Creation Error");
				return ;
			}
		}

		// Be sure to set canvas size statically at the end of onCreate() 
		// Set Static Canvas Size		
		mSCanvas.createSCanvasView(STATIC_CANVAS_WIDTH, STATIC_CANVAS_HEIGHT);
		// Update Layout
		setSCanvasViewLayout();		

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



	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setSCanvasViewLayout();
		super.onConfigurationChanged(newConfig);
	}

	void setSCanvasViewLayout(){		

		Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int nScreenWidth = display.getWidth();
		int nScreenHeight = display.getHeight();

		mCanvasViewWidth =  nScreenWidth - CANVASVIEW_WIDTH_MARGIN;
		mCanvasViewHeight = nScreenHeight - CANVASVIEW_HEIGHT_MARGIN;

		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)mCanvasContainer.getLayoutParams();
		layoutParams.width = mCanvasViewWidth;
		layoutParams.height= mCanvasViewHeight;
		layoutParams.gravity = Gravity.CENTER; 
		mCanvasContainer.setLayoutParams(layoutParams);

		// float zoomScale = mSCanvas.getCanvasZoomScale();
		float zoomScale = (float)mCanvasViewWidth/STATIC_CANVAS_WIDTH;
		Toast.makeText(mContext,  
				"View Size (" + mCanvasViewWidth + "x" + mCanvasViewHeight + "), " +
						"Real Canvas Size (" + STATIC_CANVAS_WIDTH + "x" + STATIC_CANVAS_HEIGHT + ") " + String.format("%.0f %%", 100*zoomScale) , 
						Toast.LENGTH_LONG).show();
	}

	private OnClickListener undoNredoBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mUndoBtn)) {
				mSCanvas.undo();
			} else if (v.equals(mRedoBtn)) {
				mSCanvas.redo();
			}
			mUndoBtn.setEnabled(mSCanvas.isUndoable());
			mRedoBtn.setEnabled(mSCanvas.isRedoable());
		}
	};


	OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);					
					updateModeState();
				}
			}
			else if(nBtnID == mEraserBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				}
				else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
					updateModeState();
				}
			}
		}
	};

	// Update tool button
	private void updateModeState(){
		SPenSDKUtils.updateModeState(mSCanvas, null, null, mPenBtn, mEraserBtn, null, null, null, null, null, null);
	}

	private OnClickListener openBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mOpenBtn)) {
				loadSAMMFile();
			} 
		}
	};

	private OnClickListener saveBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mSaveBtn)) {
				saveSAMMFile();
			} 
		}
	};


	private boolean saveSAMMFile() {
		String strFileName = mFolder.getPath() + "/" + DEFAULT_APP_IMAGEDATA_FILENAME;		

		//=============================================
		// Save as SAMM format file : Can handle object(stroke) data
		//=============================================
		if(USE_SAMM_FORMAT){
			// canvas option setting
			SOptionSCanvas canvasOption = mSCanvas.getOption();					
			if(canvasOption == null)
				return false;
			canvasOption.mSAMMOption.setSaveImageSize(SOptionSAMM.SAMM_SAVE_OPTION_ORIGINAL_SIZE);
			mSCanvas.setOption(canvasOption);
			if(mSCanvas.saveSAMMFile(strFileName)){
				Toast.makeText(this, "Save SAMM Image File("+ strFileName +") Success!", Toast.LENGTH_LONG).show();
				return true;
			}
			else{
				Toast.makeText(this, "Save Image File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		//=============================================
		// Just Save as Image
		//=============================================
		else{
			Bitmap bitmap = mSCanvas.getCanvasBitmap(false);
			if(bitmap == null)
				return false;
			FileOutputStream file = null;

			try {
				file = new FileOutputStream(strFileName);
				bitmap.compress(CompressFormat.PNG, 100, file);
			} catch (FileNotFoundException e) {
				Toast.makeText(this, "Save Image File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} finally {
				if(file != null)
					try {
						file.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}

			Toast.makeText(this, "Save Image File("+ strFileName +") Success!", Toast.LENGTH_LONG).show();
			return true;            
		}
	}

	private boolean loadSAMMFile(){
		String strFileName =  mFolder.getPath() + "/" + DEFAULT_APP_IMAGEDATA_FILENAME;

		//=============================================
		// Load as SAMM format file : Can handle object(stroke) data
		//=============================================
		if(USE_SAMM_FORMAT){
			// Load as SAMM format Image
			if(mSCanvas.loadSAMMFile(strFileName, true, false, false)){

				// set Zoom Scale to View Size
				mSCanvas.setZoomFitToViewSize();

				// update UI components
				mUndoBtn.setEnabled(mSCanvas.isUndoable());
				mRedoBtn.setEnabled(mSCanvas.isRedoable());
				updateModeState();

				Toast.makeText(this, "Load SAMM File("+ strFileName +") Success!", Toast.LENGTH_LONG).show();
				return true;
			}
			else{
				Toast.makeText(this, "Load SAMM File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		//=============================================
		// Just Load as Image
		//=============================================
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(strFileName);
			if (bitmap != null) {
				mSCanvas.setBitmap(bitmap, true);
				bitmap.recycle();

				Toast.makeText(this, "Load Image File("+ strFileName +") Success!", Toast.LENGTH_LONG).show();
				return true;
			}
			else{
				Toast.makeText(this, "Load File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
	}	
}
