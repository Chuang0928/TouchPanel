package com.samsung.spensdk.example.bgfg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.samm.common.SOptionSAMM;
import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spen.lib.image.SPenImageProcess;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;


public class SPen_Example_BackgroundForeground extends Activity {

	public final static String TAG = "SPenSDK Sample";

	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	private final String APPLICATION_ID_NAME = "SDK Sample Application";
	private final int APPLICATION_ID_VERSION_MAJOR = 1;
	private final int APPLICATION_ID_VERSION_MINOR = 0;
	private final String APPLICATION_ID_VERSION_PATCHNAME = "Debug";

	//==============================
	// BG & FG Path
	//==============================
	public static final String DEFAULT_APP_IMAGEDATA_DIRECTORY = "SPenSDK/images";
	public static final String EXTRA_IMAGE_PATH = "path";
	public static final String EXTRA_IMAGE_NAME = "filename";

	public static final String SAVED_FILE_EXTENSION = "png";

	private static final int DIALOG_CHANGE_BACKGROUND = 1;

	//==============================
	// memo BG Intent
	//==============================	
	public static final String EXTRA_BG_BASE_FILENAME = "base_bg_filename";
	public static final String EXTRA_BG_BASE_NONE = "base_bg_none";
	public static final String EXTRA_BG_PATTERN_FILENAME = "pattern_bg_filename";
	public static final String EXTRA_SELECT_BG_BASE_MDOE = "select_base_mode";	
	public static final String BG_BASE_ASSET_PATH = "spen_sdk_bg_base_resource";
	public static final String BG_PATTERN_ASSET_PATH = "spen_sdk_bg_pattern_resource";

	public boolean mbBGBaseNone = false;

	//==============================
	// Menu
	//==============================
	private final int MENU_SAVE_AS_IMAGE_GROUP = 1000;
	private final int MENU_SAVE_AS_IMAGE_FOREGROUND_ONLY = 1001;
	private final int MENU_SAVE_AS_IMAGE_ALL = 1002;

	private final int MENU_LOAD_AS_IMAGE_GROUP = 2000;
	private final int MENU_LOAD_AS_FOREGROUND_IMAGE = 2001;	
	private final int MENU_LOAD_AS_BACKGROUND_IMAGE = 2002;

	private final int MENU_SAVE_SAMM_FILE = 3000;
	private final int MENU_LOAD_SAMM_FILE = 4000;

	private final int MENU_CHANGE_BACKGROUND = 5000;
	private final int MENU_COMBINE_BACKGROUND = 6000;
	private final int MENU_CHECK_CANVAS_DRAWING = 7000;


	//==============================
	// Activity Request code
	//==============================
	private final int REQUEST_CODE_LOAD_IMAGE_AS_FOREGROUND = 101;
	private final int REQUEST_CODE_LOAD_IMAGE_AS_BACKGROUND = 102;
	private final int REQUEST_CODE_LOAD_SAMM_FILE = 103;
	private final int REQUEST_CODE_BASE_BG_MEMO = 104;
	private final int REQUEST_CODE_PATTERN_BG_MEMO = 105;
	//==============================
	// Variables
	//==============================
	Context mContext = null;

	private FrameLayout	mLayoutContainer;
	private RelativeLayout	mCanvasContainer;
	private SCanvasView		mSCanvas;
	private ImageView		mPenBtn;
	private ImageView		mEraserBtn;
	private ImageView		mTextBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;

	private File mFolder = null;
	private int mChangeBGItem;
	private String[] mBgNames = { "Background 1", "Background 2", "Background 3", "Clear background", };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.editor_bg_fg);

		mContext = this;
		mChangeBGItem = 0;

		//------------------------------------
		// UI Setting
		//------------------------------------
		mPenBtn = (ImageView) findViewById(R.id.penBtn);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);
		mTextBtn = (ImageView) findViewById(R.id.textBtn);
		mTextBtn.setOnClickListener(mBtnClickListener);

		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);

		//------------------------------------
		// Create SCanvasView
		//------------------------------------
		mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);

		// Add SCanvasView under minSDK 14(AndroidManifext.xml)
		// mSCanvas = new SCanvasView(mContext);        
		// mCanvasContainer.addView(mSCanvas);

		// Add SCanvasView under minSDK 10(AndroidManifext.xml) for preventing text input error
		mSCanvas = new SCanvasView(mContext);
		mSCanvas.addedByResizingContainer(mCanvasContainer);

		//------------------------------------
		// SettingView Setting
		//------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, true, true);
		// Talk & Description Setting by Locale
		SPenSDKUtils.addTalkbackAndDescriptionStringResourceMap(settingResourceMapInt);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, true, true);
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

				// Update button state
				updateModeState();

				Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.letter_bg_grass);
				if(bg == null)
					return;
				mSCanvas.setBackgroundImage(bg);
				bg.recycle();
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


		//------------------------------------------------
		// SCanvas Mode Changed Listener 
		//------------------------------------------------
		mSCanvas.setSCanvasModeChangedListener(new SCanvasModeChangedListener() {

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
		});

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
			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
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
			else if(nBtnID == mTextBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);										
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to insert Text", Toast.LENGTH_SHORT).show();
				}
			}		
		}
	};



	@Override
	public boolean onCreateOptionsMenu(Menu menu){	

		SubMenu fileMenu = menu.addSubMenu("Save as Image");
		fileMenu.add(MENU_SAVE_AS_IMAGE_GROUP, MENU_SAVE_AS_IMAGE_FOREGROUND_ONLY, 1, "Foreground Only");
		fileMenu.add(MENU_SAVE_AS_IMAGE_GROUP, MENU_SAVE_AS_IMAGE_ALL, 2, "Foreground + background");

		SubMenu dataMenu = menu.addSubMenu("Load Image");		
		dataMenu.add(MENU_LOAD_AS_IMAGE_GROUP, MENU_LOAD_AS_FOREGROUND_IMAGE, 1, "Into Foreground");
		dataMenu.add(MENU_LOAD_AS_IMAGE_GROUP, MENU_LOAD_AS_BACKGROUND_IMAGE, 2, "Into Background");

		menu.add(MENU_SAVE_SAMM_FILE, MENU_SAVE_SAMM_FILE, 1, "Save as SAMM File");
		menu.add(MENU_LOAD_SAMM_FILE, MENU_LOAD_SAMM_FILE, 1, "Load SAMM File");

		menu.add(MENU_CHANGE_BACKGROUND, MENU_CHANGE_BACKGROUND, 1, "Change Background");
		menu.add(MENU_COMBINE_BACKGROUND, MENU_COMBINE_BACKGROUND, 1, "Combine Background");
		menu.add(MENU_CHECK_CANVAS_DRAWING, MENU_CHECK_CANVAS_DRAWING, 1, "Check Canvas");

		return super.onCreateOptionsMenu(menu);
	} 


	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()) {
		case MENU_SAVE_AS_IMAGE_FOREGROUND_ONLY:
			saveCanvasImage(true);
			break;
		case MENU_SAVE_AS_IMAGE_ALL:
			saveCanvasImage(false);
			break;
		case MENU_SAVE_SAMM_FILE:
			saveSAMMFile();
			break;
		case MENU_LOAD_AS_FOREGROUND_IMAGE:
			selectImageForCanvasImage(true);
			break;
		case MENU_LOAD_AS_BACKGROUND_IMAGE:
			selectImageForCanvasImage(false);
			break;
		case MENU_LOAD_SAMM_FILE:
			selectSAMMFile();
			break;
		case MENU_CHANGE_BACKGROUND:
			showDialog(DIALOG_CHANGE_BACKGROUND);
			break;	
		case MENU_COMBINE_BACKGROUND:
			selectBGBaseBitmap();
			break;		
		case MENU_CHECK_CANVAS_DRAWING:
			checkCanvas();
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode==RESULT_OK){
			if(data == null)
				return;

			Bundle bundle = data.getExtras();          
			if(bundle == null)
				return;

			if(requestCode == REQUEST_CODE_LOAD_IMAGE_AS_FOREGROUND) {    			
				String mFileName = bundle.getString(EXTRA_IMAGE_NAME);
				loadCanvasImage(mFileName, true);
			}else if(requestCode == REQUEST_CODE_LOAD_IMAGE_AS_BACKGROUND) {    			
				String mFileName = bundle.getString(EXTRA_IMAGE_NAME);
				loadCanvasImage(mFileName, false);
			}else if(requestCode == REQUEST_CODE_LOAD_SAMM_FILE) {
				String mFileName = bundle.getString(EXTRA_IMAGE_NAME);
				loadSAMMFile(mFileName);
			}else if(requestCode == REQUEST_CODE_BASE_BG_MEMO) {
				String nBGBaseFileName = bundle.getString(EXTRA_BG_BASE_FILENAME);	
				mbBGBaseNone = bundle.getBoolean(EXTRA_BG_BASE_NONE);
				selectBGPatternBitmap(nBGBaseFileName);
			}else if(requestCode == REQUEST_CODE_PATTERN_BG_MEMO) {						
				String nBGBaseFileName = bundle.getString(EXTRA_BG_BASE_FILENAME);	
				String nBGPatternFileName = bundle.getString(EXTRA_BG_PATTERN_FILENAME);				
				setBGImageAsSelectedImages(nBGBaseFileName, nBGPatternFileName);
			}
		}
	}
	// Update tool button
	private void updateModeState(){
		SPenSDKUtils.updateModeState(mSCanvas, null, null, mPenBtn, mEraserBtn, mTextBtn, null, null, null, null);
	}

	private boolean saveCanvasImage(boolean bSaveOnlyForegroundImage) {    	
		Bitmap bmCanvas = mSCanvas.getCanvasBitmap(bSaveOnlyForegroundImage);

		if (!(mFolder.exists()))
			if(!mFolder.mkdirs())
				return false;
		String savePath = mFolder.getPath() + '/' + ExampleUtils.getUniqueFilename(mFolder, "image", SAVED_FILE_EXTENSION);
		Log.d(TAG, "Save Path = " + savePath);

		return saveBitmapPNG(savePath, bmCanvas);
	}

	private boolean saveBitmapPNG(String strFileName, Bitmap bitmap){		
		if(strFileName==null || bitmap==null)
			return false;

		boolean bSuccess1 = false;	
		boolean bSuccess2;
		boolean bSuccess3;
		File saveFile = new File(strFileName);			

		if(saveFile.exists()) {
			if(!saveFile.delete())
				return false;
		}

		try {
			bSuccess1 = saveFile.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		OutputStream out = null;
		try {
			out = new FileOutputStream(saveFile);
			bSuccess2 = bitmap.compress(CompressFormat.PNG, 100, out);			
		} catch (Exception e) {
			e.printStackTrace();			
			bSuccess2 = false;
		}
		try {
			if(out!=null)
			{
				out.flush();
				out.close();
				bSuccess3 = true;
			}
			else
				bSuccess3 = false;

		} catch (IOException e) {
			e.printStackTrace();
			bSuccess3 = false;
		}finally
		{
			if(out != null)
			{
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}			
		}		

		return (bSuccess1 && bSuccess2 && bSuccess3);
	}

	private boolean saveSAMMFile() {
		String savePath = mFolder.getPath() + '/' + ExampleUtils.getUniqueFilename(mFolder, "SAMM", SAVED_FILE_EXTENSION);		
		Log.d(TAG, "Save Path = " + savePath);

		// canvas option setting
		SOptionSCanvas canvasOption = mSCanvas.getOption();					
		if(canvasOption == null)
			return false;
		canvasOption.mSAMMOption.setSaveImageSize(SOptionSAMM.SAMM_SAVE_OPTION_ORIGINAL_SIZE);
		mSCanvas.setOption(canvasOption);
		return mSCanvas.saveSAMMFile(savePath);
	}

	private void selectSAMMFile() {
		Intent intent = new Intent(getApplicationContext(), ListActivity.class);
		startActivityForResult(intent, REQUEST_CODE_LOAD_SAMM_FILE);
	}

	private boolean loadSAMMFile(String fileName) {
		String loadPath = mFolder.getPath() + '/' + fileName;
		return mSCanvas.loadSAMMFile(loadPath, true, true, true);
	}

	private void selectImageForCanvasImage(boolean loadAsForegroundImage){
		if(loadAsForegroundImage){
			Intent intent = new Intent(getApplicationContext(), ListActivity.class);
			startActivityForResult(intent, REQUEST_CODE_LOAD_IMAGE_AS_FOREGROUND);
		}
		else{
			Intent intent = new Intent(mContext, ListActivity.class);
			startActivityForResult(intent, REQUEST_CODE_LOAD_IMAGE_AS_BACKGROUND);
		}
	}

	private boolean loadCanvasImage(String fileName, boolean loadAsForegroundImage) {

		String loadPath = mFolder.getPath() + '/' + fileName;

		Log.i(TAG, "Load Path = " + loadPath);

		if(loadAsForegroundImage){
			Bitmap bmForeground = BitmapFactory.decodeFile(loadPath);
			if(bmForeground == null)
				return false;
			int nWidth = mSCanvas.getWidth();
			int nHeight = mSCanvas.getHeight();
			bmForeground = Bitmap.createScaledBitmap(bmForeground, nWidth, nHeight, true);
			return mSCanvas.setClearImageBitmap(bmForeground);
		}
		else {
			return mSCanvas.setBGImagePath(loadPath);
		}
	}

	private void checkCanvas() {		
		if(mSCanvas.isCanvasDrawingEmpty(true)){
			Toast.makeText(mContext, "There is no Canvas Drawing", Toast.LENGTH_LONG).show();		
		}
		else
			Toast.makeText(mContext, "There is some of Canvas Drawing", Toast.LENGTH_LONG).show();		
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CHANGE_BACKGROUND:
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setSingleChoiceItems(mBgNames, mChangeBGItem, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {                  
					mChangeBGItem = which;
					Bitmap bm = null;
					switch(which) {
					case 0:
						bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.letter_bg_grass);
						break;
					case 1:
						bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.letter_bg_present);
						break;
					case 2:
						bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.letter_bg);
						break;
					case 3:
						mSCanvas.setBGColor(0x00000000);
						break;
					}	
					if(bm != null) {
						mSCanvas.setBGImage(bm);
					}
					removeDialog(DIALOG_CHANGE_BACKGROUND);
				}
			});
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	private void selectBGBaseBitmap(){		
		Intent intent = new Intent(getApplicationContext(), MemoBGListView.class);
		// select bg base
		intent.putExtra(EXTRA_SELECT_BG_BASE_MDOE, true);
		startActivityForResult(intent, REQUEST_CODE_BASE_BG_MEMO);
	}	

	private void selectBGPatternBitmap(String nBGBaseFileName){		
		Intent intent = new Intent(getApplicationContext(), MemoBGListView.class);
		// select bg pattern
		intent.putExtra(EXTRA_SELECT_BG_BASE_MDOE, false);
		intent.putExtra(EXTRA_BG_BASE_FILENAME, nBGBaseFileName);	
		startActivityForResult(intent, REQUEST_CODE_PATTERN_BG_MEMO);
	}	


	private void setBGImageAsSelectedImages(String nBGBaseFileName, String nBGPatternFileName){
		Bitmap bmBGBase = null;
		Bitmap bmBGPattern = null;
		try {
			bmBGBase = BitmapFactory.decodeStream(mContext.getAssets().open(BG_BASE_ASSET_PATH + "/" + nBGBaseFileName));
			bmBGPattern = BitmapFactory.decodeStream(mContext.getAssets().open(BG_PATTERN_ASSET_PATH + "/" + nBGPatternFileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(bmBGBase != null && bmBGPattern != null) {
			Bitmap bmBG = null;
			//BG Base None : Only Pattern Image
			if(mbBGBaseNone)
				bmBG = SPenImageProcess.getCombinedImage(mSCanvas.getWidth(), mSCanvas.getHeight(), null, bmBGPattern);	
			else
				bmBG = SPenImageProcess.getCombinedImage(mSCanvas.getWidth(), mSCanvas.getHeight(), bmBGBase, bmBGPattern);	
			mSCanvas.setBGImage(bmBG);
		}		
	}
}
