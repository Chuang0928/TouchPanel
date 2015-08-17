package com.samsung.spensdk.example.groupingeditor;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.samm.common.SObjectImage;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spen.settings.SettingFillingInfo;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spen.settings.SettingTextInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.ColorPickerColorChangeListener;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.PreferencesOfSAMMOption;
import com.samsung.spensdk.example.tools.SPenSDKUtils;


public class SPen_Example_GroupingEditor extends Activity {

	private final String TAG = "SPenSDK Sample";

	//==============================
	// Intent Parameters
	//==============================	
	public final static String KEY_IMAGE_SAVE_PATH = "SavePath";
	public final static String KEY_IMAGE_SRC_PATH = "FilePathOrigin";

	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	private final String APPLICATION_ID_NAME = "SDK Sample Application";
	private final int APPLICATION_ID_VERSION_MAJOR = 1;
	private final int APPLICATION_ID_VERSION_MINOR = 0;
	private final String APPLICATION_ID_VERSION_PATCHNAME = "Debug";

	private final int MENU_EDIT_OBJECT_GROUPING = 1002;
	private final int MENU_EDIT_OBJECT_GROUPING_GROUP = 1003;
	private final int MENU_EDIT_OBJECT_GROUPING_UNGROUP = 1004;
	private final int MENU_EDIT_OBJECT_DEPTH_CHANGE = 1005;
	private final int MENU_EDIT_OBJECT_DEPTH_CHANGE_FORWARD = 1006;
	private final int MENU_EDIT_OBJECT_DEPTH_CHANGE_BACKWARD = 1007;
	private final int MENU_EDIT_OBJECT_DEPTH_CHANGE_FRONT = 1008;
	private final int MENU_EDIT_OBJECT_DEPTH_CHANGE_BACK = 1009;


	//==============================
	// Activity Request code
	//==============================
	private final int REQUEST_CODE_INSERT_IMAGE_OBJECT = 100;

	//==============================
	// Variables
	//==============================
	Context mContext = null;

	private String  mSrcImageFilePath = null;
	private Rect	mSrcImageRect = null;
	private boolean mMultiSelectionMode = false;
	private int		mSettingviewType;

	private FrameLayout		mLayoutContainer;
	private RelativeLayout	mCanvasContainer;
	private SCanvasView		mSCanvas;

	private ImageView		mSelectionModeBtn;
	private ImageView		mPenBtn;
	private ImageView		mEraserBtn;
	private ImageView		mTextBtn;
	private ImageView		mFillingBtn;
	private ImageView		mInsertBtn;
	private ImageView		mColorPickerBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.editor_grouping_editor);

		mContext = this;

		//------------------------------------
		// UI Setting
		//------------------------------------
		mSelectionModeBtn = (ImageView) findViewById(R.id.selectionModeBtn);
		mSelectionModeBtn.setOnClickListener(mBtnClickListener);

		mPenBtn = (ImageView) findViewById(R.id.penBtn);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mPenBtn.setOnLongClickListener(mBtnLongClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn.setOnLongClickListener(mBtnLongClickListener);
		mTextBtn = (ImageView) findViewById(R.id.textBtn);
		mTextBtn.setOnClickListener(mBtnClickListener);
		mTextBtn.setOnLongClickListener(mBtnLongClickListener);
		mFillingBtn = (ImageView) findViewById(R.id.fillingBtn);
		mFillingBtn.setOnClickListener(mBtnClickListener);
		mFillingBtn.setOnLongClickListener(mBtnLongClickListener);

		mColorPickerBtn = (ImageView) findViewById(R.id.colorPickerBtn);
		mColorPickerBtn.setOnClickListener(mColorPickerListener);

		mInsertBtn = (ImageView) findViewById(R.id.insertBtn);
		mInsertBtn.setOnClickListener(mInsertBtnClickListener);

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

		Intent intent = getIntent();
		mSrcImageFilePath = intent.getStringExtra(KEY_IMAGE_SRC_PATH);

		// If initial image exist, resize the canvas size
		if(mSrcImageFilePath!=null){
			mSrcImageRect = getMiniumCanvasRect(mSrcImageFilePath, 20);

			// Place SCanvasView In the Center
			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)mCanvasContainer.getLayoutParams();		
			layoutParams.width = mSrcImageRect.right-mSrcImageRect.left;
			layoutParams.height= mSrcImageRect.bottom-mSrcImageRect.top;
			layoutParams.gravity = Gravity.CENTER; 
			mCanvasContainer.setLayoutParams(layoutParams);

			// Set Background of layout container
			mLayoutContainer.setBackgroundResource(R.drawable.bg_edit);
		}

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

				// Set Initial Setting View Size
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);

				// Set Pen Only Mode with Finger Control
				mSCanvas.setFingerControlPenDrawing(true);

				// Update button state
				updateModeState();

				// Load the file & set Background Image
				if(mSrcImageFilePath!=null){

					if(SCanvasView.isSAMMFile(mSrcImageFilePath)){
						loadSAMMFile(mSrcImageFilePath);
						// Set the editing rect after loading
					}
					else{					
						// set BG Image
						if(!mSCanvas.setBGImagePath(mSrcImageFilePath)){
							Toast.makeText(mContext, "Fail to set Background Image Path.", Toast.LENGTH_LONG).show();
						}
					}
				}

				// Restore last setting information
				// mSCanvas.restoreSettingViewStatus();
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
				if(!(mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_SELECT)){
					mMultiSelectionMode = false;
				}
				else{
					if(mSCanvas.isMultiSelectionMode())
						mMultiSelectionMode = true;
					else
						mMultiSelectionMode = false;
				}
				updateSelectButton();
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


		//------------------------------------------------
		// Color Picker Listener 
		//------------------------------------------------
		mSCanvas.setColorPickerColorChangeListener(new ColorPickerColorChangeListener(){
			@Override
			public void onColorPickerColorChanged(int nColor) {

				int nCurMode = mSCanvas.getCanvasMode();
				if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN) {
					SettingStrokeInfo strokeInfo = mSCanvas.getSettingViewStrokeInfo();
					if(strokeInfo != null) {
						strokeInfo.setStrokeColor(nColor);	
						mSCanvas.setSettingViewStrokeInfo(strokeInfo);
					}	
				}
				else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER) {
					// do nothing
				}
				else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					SettingTextInfo textInfo = mSCanvas.getSettingViewTextInfo();
					if(textInfo != null) {
						textInfo.setTextColor(nColor);
						mSCanvas.setSettingViewTextInfo(textInfo);
					}
				}
				else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING) {
					SettingFillingInfo fillingInfo = mSCanvas.getSettingViewFillingInfo();
					if(fillingInfo != null) {
						fillingInfo.setFillingColor(nColor);
						mSCanvas.setSettingViewFillingInfo(fillingInfo);
					}
				}	
			}			
		});	

		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);
		mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SPENSDK);

		mSCanvas.setSPenHoverListener(new SPenHoverListener() {

			boolean isPenButtonDown = false;
			@Override
			public boolean onHover(View view, MotionEvent event) {				
				return false;
			}

			@Override
			public void onHoverButtonDown(View view, MotionEvent event) {
				isPenButtonDown= true;
			}

			@Override
			public void onHoverButtonUp(View view, MotionEvent event) {
				if(isPenButtonDown==false)	// ignore button up event if button was not pressed on hover
					return;
				isPenButtonDown = false;

				boolean bIncludeDefinedSetting = true;
				boolean bIncludeCustomSetting = true;
				boolean bIncludeEraserSetting = true;
				SettingStrokeInfo settingInfo = mSCanvas.getSettingViewNextStrokeInfo(bIncludeDefinedSetting, bIncludeCustomSetting, bIncludeEraserSetting);
				if(settingInfo!=null) {
					if(mSCanvas.setSettingViewStrokeInfo(settingInfo)) {
						int nPreviousMode = mSCanvas.getCanvasMode();
						// Mode Change : Pen => Eraser					
						if(nPreviousMode == SCanvasConstants.SCANVAS_MODE_INPUT_PEN
								&& settingInfo.getStrokeStyle()==SObjectStroke.SAMM_STROKE_STYLE_ERASER){
							// Change Mode
							mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
							// Show Setting View
							if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN)){
								mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
								mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, true);
							}
							updateModeState();
						}
						// Mode Change : Eraser => Pen 
						if(nPreviousMode == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER
								&& settingInfo.getStrokeStyle()!=SObjectStroke.SAMM_STROKE_STYLE_ERASER){
							// Change Mode
							mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
							// Show Setting View
							if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER)){
								mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
								mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);
							}
							updateModeState();
						}		
					}
				}
			}
		});

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

	private OnClickListener mBtnClickListener = new OnClickListener() {		

		@Override
		public void onClick(View v) {
			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mSelectionModeBtn.getId()){				
				if(mSCanvas.getCanvasMode() != SCanvasConstants.SCANVAS_MODE_SELECT){
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_SELECT);
					mMultiSelectionMode = false;
				}
				else{
					mMultiSelectionMode = !mMultiSelectionMode;
				}
				selectModeChange(true);
			}
			else if(nBtnID == mPenBtn.getId()){				
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
					mSettingviewType = SCanvasConstants.SCANVAS_SETTINGVIEW_PEN;
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);	
					selectModeChange(false);
					updateModeState();
				}
			}
			else if(nBtnID == mEraserBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
					mSettingviewType = SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER;
				}
				else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
					selectModeChange(false);
					updateModeState();
				}
			}
			else if(nBtnID == mTextBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
					mSettingviewType = SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT;
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);	
					selectModeChange(false);
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to insert Text", Toast.LENGTH_SHORT).show();
				}
			}
			else if(nBtnID == mFillingBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING);
					mSettingviewType = SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING;
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, false);
					selectModeChange(false);
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to fill color", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	private OnLongClickListener mBtnLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {

			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){					
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);					
					updateModeState();
				}
				return true;
			}
			else if(nBtnID == mEraserBtn.getId()){
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				}
				else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, true);
					updateModeState();
				}
				return true;
			}	
			else if(nBtnID == mTextBtn.getId()){
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, true);										
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to insert Text", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
			else if(nBtnID == mFillingBtn.getId()){
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, true);										
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to fill color", Toast.LENGTH_SHORT).show();
				}
				return true;
			}

			return false;
		}
	};

	// insert image
	private OnClickListener mInsertBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mInsertBtn)) {	
				callGalleryForInputImage(REQUEST_CODE_INSERT_IMAGE_OBJECT);
			}
		}
	};

	// color picker mode
	private OnClickListener mColorPickerListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mColorPickerBtn)) {
				// Toggle
				boolean bIsColorPickerMode = !mSCanvas.isColorPickerMode(); 
				mSCanvas.setColorPickerMode(bIsColorPickerMode);
			}
		}
	};

	// Update tool button
	private void updateModeState(){
		SPenSDKUtils.updateModeState(mSCanvas, null, null, mPenBtn, mEraserBtn, mTextBtn, mFillingBtn, mInsertBtn, mColorPickerBtn, null, mSelectionModeBtn);
	}	


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode==RESULT_OK){
			if(data == null)
				return;

			if(requestCode == REQUEST_CODE_INSERT_IMAGE_OBJECT) {    			
				Uri imageFileUri = data.getData();
				if(imageFileUri == null)
					return;
				String imagePath = SPenSDKUtils.getRealPathFromURI(this, imageFileUri);
				insertImageObject(imagePath);	
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){	
		SubMenu editGroupingMenu = menu.addSubMenu(MENU_EDIT_OBJECT_GROUPING, MENU_EDIT_OBJECT_GROUPING, 1,"Object Grouping");
		editGroupingMenu.add(MENU_EDIT_OBJECT_GROUPING, MENU_EDIT_OBJECT_GROUPING_GROUP, 2, "Group");
		editGroupingMenu.add(MENU_EDIT_OBJECT_GROUPING, MENU_EDIT_OBJECT_GROUPING_UNGROUP, 3, "Ungroup");
		SubMenu editDepthChangeMenu = menu.addSubMenu(MENU_EDIT_OBJECT_DEPTH_CHANGE, MENU_EDIT_OBJECT_DEPTH_CHANGE, 4, "Object Depth Change");
		editDepthChangeMenu.add(MENU_EDIT_OBJECT_DEPTH_CHANGE, MENU_EDIT_OBJECT_DEPTH_CHANGE_FORWARD, 5, "Forward");
		editDepthChangeMenu.add(MENU_EDIT_OBJECT_DEPTH_CHANGE, MENU_EDIT_OBJECT_DEPTH_CHANGE_BACKWARD, 6, "Backward");
		editDepthChangeMenu.add(MENU_EDIT_OBJECT_DEPTH_CHANGE, MENU_EDIT_OBJECT_DEPTH_CHANGE_FRONT, 7, "Front");
		editDepthChangeMenu.add(MENU_EDIT_OBJECT_DEPTH_CHANGE, MENU_EDIT_OBJECT_DEPTH_CHANGE_BACK, 8, "Back");

		return super.onCreateOptionsMenu(menu);
	} 


	@Override
	public boolean onMenuOpened(int featureId, Menu menu){
		super.onMenuOpened(featureId, menu);

		if (menu == null) 
			return true;

		boolean bSObjectSelected = mSCanvas.isSObjectSelected();

		MenuItem menuItemObjectGrouping = menu.findItem(MENU_EDIT_OBJECT_GROUPING);
		MenuItem menuItemObjectDepthChange = menu.findItem(MENU_EDIT_OBJECT_DEPTH_CHANGE);
		
		if(menuItemObjectGrouping!=null) {
			menuItemObjectGrouping.setEnabled(bSObjectSelected);
			if(bSObjectSelected) {
				SubMenu menuItemGrouping = menuItemObjectGrouping.getSubMenu();
				if(menuItemGrouping!=null){
					MenuItem menuItemGroup = menuItemGrouping.findItem(MENU_EDIT_OBJECT_GROUPING_GROUP);
					MenuItem menuItemUngroup = menuItemGrouping.findItem(MENU_EDIT_OBJECT_GROUPING_UNGROUP);
					if(menuItemGroup!=null) menuItemGroup.setEnabled(mSCanvas.isSelectedObjectGroupable());
					if(menuItemUngroup!=null) menuItemUngroup.setEnabled(mSCanvas.isSelectedObjectUngroupable());
				}
			}
		}
		if(menuItemObjectDepthChange!=null) menuItemObjectDepthChange.setEnabled(bSObjectSelected);

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()) {
		case MENU_EDIT_OBJECT_GROUPING_GROUP:
		{
			mSCanvas.groupSelectedObjects();
		}
		break;
		case MENU_EDIT_OBJECT_GROUPING_UNGROUP:
		{
			mSCanvas.ungroupSelectedObjects();
		}
		break;
		case MENU_EDIT_OBJECT_DEPTH_CHANGE_FORWARD:
		{
			mSCanvas.bringObjectsForward();
		}
		break;
		case MENU_EDIT_OBJECT_DEPTH_CHANGE_BACKWARD:
		{
			mSCanvas.sendObjectsBackward();
		}
		break;
		case MENU_EDIT_OBJECT_DEPTH_CHANGE_FRONT:
		{
			mSCanvas.bringObjectsFront();
		}
		break;
		case MENU_EDIT_OBJECT_DEPTH_CHANGE_BACK:
		{
			mSCanvas.sendObjectsBack();
		}
		break;
		}	
		return true;
	}

	// Call Gallery
	private void callGalleryForInputImage(int nRequestCode){
		try {
			Intent galleryIntent;
			galleryIntent = new Intent(); 
			galleryIntent.setAction(Intent.ACTION_GET_CONTENT);				
			galleryIntent.setType("image/*");
			galleryIntent.setClassName("com.cooliris.media", "com.cooliris.media.Gallery");
			startActivityForResult(galleryIntent, nRequestCode);
		} catch(ActivityNotFoundException e) {
			Intent galleryIntent;
			galleryIntent = new Intent();
			galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
			galleryIntent.setType("image/*");
			startActivityForResult(galleryIntent, nRequestCode);
			e.printStackTrace();
		}		
	}

	// Get the minimum image scaled rect which is fit to current screen 
	Rect getMiniumCanvasRect(String strImagePath, int nMargin){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		int nScreenWidth =  displayMetrics.widthPixels - nMargin*2;
		int nScreenHeight = displayMetrics.heightPixels - nMargin*2;

		// Make more small for screen rotation T.T
		if(nScreenWidth<nScreenHeight)
			nScreenHeight = nScreenWidth;
		else
			nScreenWidth = nScreenHeight;

		int nImageWidth = nScreenWidth;
		int nImageHeight = nScreenHeight;	
		if(strImagePath!=null){
			BitmapFactory.Options opts = SPenSDKUtils.getBitmapSize(strImagePath);
			nImageWidth = opts.outWidth;
			nImageHeight = opts.outHeight;
		}		


		float fResizeWidth = (float) nScreenWidth / nImageWidth;
		float fResizeHeight = (float) nScreenHeight / nImageHeight;
		float fResizeRatio;

		// Fit to Height
		if(fResizeWidth>fResizeHeight){
			fResizeRatio = fResizeHeight;
		}
		// Fit to Width
		else {	
			fResizeRatio = fResizeWidth;
		}

		return new Rect(0,0, (int)(nImageWidth*fResizeRatio), (int)(nImageHeight*fResizeRatio));
	}

	// Load SAMM file
	boolean loadSAMMFile(String strFileName){
		if(mSCanvas.isAnimationMode()){
			// It must be not animation mode.
		}
		else {
			// set progress dialog
			mSCanvas.setProgressDialogSetting(R.string.load_title, R.string.load_msg, ProgressDialog.STYLE_HORIZONTAL, false);

			// canvas option setting
			SOptionSCanvas canvasOption = mSCanvas.getOption();					
			if(canvasOption == null)
				return false;
			canvasOption.mSAMMOption.setConvertCanvasSizeOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasSize(mContext));
			canvasOption.mSAMMOption.setConvertCanvasHorizontalAlignOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasHAlign(mContext));
			canvasOption.mSAMMOption.setConvertCanvasVerticalAlignOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasVAlign(mContext));
			canvasOption.mSAMMOption.setDecodePriorityFGData(PreferencesOfSAMMOption.getPreferenceDecodePriorityFGData(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);					

			// show progress for loading data
			if(mSCanvas.loadSAMMFile(strFileName, true, true, true)){
				// Loading Result can be get by callback function
			}
			else{
				Toast.makeText(this, "Load AMS File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}

	// insert Image Object	
	boolean insertImageObject(String imagePath){
		// Check Valid Image File
		if(!SPenSDKUtils.isValidImagePath(imagePath))
		{
			Toast.makeText(this, "Invalid image path or web image", Toast.LENGTH_LONG).show();	
			return false;
		}

		// canvas option setting
		SOptionSCanvas canvasOption = mSCanvas.getOption();					
		if(canvasOption == null)
			return false;

		if(canvasOption.mSAMMOption == null)
			return false;

		canvasOption.mSAMMOption.setContentsQuality(PreferencesOfSAMMOption.getPreferenceSaveImageQuality(mContext));
		// option setting
		mSCanvas.setOption(canvasOption);

		RectF rectF = getDefaultImageRect(imagePath);
		int nContentsQualityOption = canvasOption.mSAMMOption.getContentsQuality();
		SObjectImage sImageObject = new SObjectImage(nContentsQualityOption);
		sImageObject.setRect(rectF);
		sImageObject.setImagePath(imagePath);

		if(mSCanvas.insertSAMMImage(sImageObject, true)){
			//Toast.makeText(this, "Insert image file("+ imagePath +") Success!", Toast.LENGTH_SHORT).show();
			return true;
		}
		else{
			Toast.makeText(this, "Insert image file("+ imagePath +") Fail!", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	// get default image rect 
	RectF getDefaultImageRect(String strImagePath){
		// Rect Region : Consider image real size
		BitmapFactory.Options opts = SPenSDKUtils.getBitmapSize(strImagePath);
		int nImageWidth = opts.outWidth;
		int nImageHeight = opts.outHeight;
		int nScreenWidth = mSCanvas.getWidth();
		int nScreenHeight = mSCanvas.getHeight();    			
		int nBoxRadius = (nScreenWidth>nScreenHeight) ? nScreenHeight/4 : nScreenWidth/4;
		int nCenterX = nScreenWidth/2;
		int nCenterY = nScreenHeight/2;
		if(nImageWidth > nImageHeight)
			return new RectF(nCenterX-nBoxRadius,nCenterY-(nBoxRadius*nImageHeight/nImageWidth),nCenterX+nBoxRadius,nCenterY+(nBoxRadius*nImageHeight/nImageWidth));
		else
			return new RectF(nCenterX-(nBoxRadius*nImageWidth/nImageHeight),nCenterY-nBoxRadius,nCenterX+(nBoxRadius*nImageWidth/nImageHeight),nCenterY+nBoxRadius);
	}	

	
	
	private void selectModeChange(boolean updateMode){
		mSCanvas.showSettingView(mSettingviewType, false);
		if(updateMode){
			mSCanvas.setMultiSelectionMode(mMultiSelectionMode);
		}
		else{
			mMultiSelectionMode = false;
		}
		updateSelectButton();
	}
	
	private void updateSelectButton(){
		if(mMultiSelectionMode){
			mSelectionModeBtn.setImageResource(R.drawable.selector_multiselect);
		}
		else{
			mSelectionModeBtn.setImageResource(R.drawable.selector_singleselect);
		}
	}
}
