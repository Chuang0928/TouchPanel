package com.samsung.spensdk.example.sammeditor;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.samsung.samm.common.SDataAttachFile;
import com.samsung.samm.common.SDataPageMemo;
import com.samsung.samm.common.SObject;
import com.samsung.samm.common.SObjectFilling;
import com.samsung.samm.common.SObjectImage;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.samm.common.SObjectText;
import com.samsung.samm.common.SObjectVideo;
import com.samsung.samm.common.SOptionSAMM;
import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spen.lib.image.SPenImageFilterConstants;
import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spen.settings.SettingFillingInfo;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spen.settings.SettingTextInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.ColorPickerColorChangeListener;
import com.samsung.spensdk.applistener.FileProcessListener;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;
import com.samsung.spensdk.applistener.SettingStrokeChangeListener;
import com.samsung.spensdk.applistener.SettingViewShowListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.PreferencesOfAnimationOption;
import com.samsung.spensdk.example.tools.PreferencesOfOtherOption;
import com.samsung.spensdk.example.tools.PreferencesOfSAMMOption;
import com.samsung.spensdk.example.tools.SPenSDKUtils;
import com.samsung.spensdk.example.tools.ToolAudioListView;
import com.samsung.spensdk.example.tools.ToolColorPickerDialog;
import com.samsung.spensdk.example.tools.ToolColorPickerDialog.OnColorChangedListener;
import com.samsung.spensdk.example.tools.ToolFileTotalInfoShow;
import com.samsung.spensdk.example.tools.ToolListActivity;
import com.samsung.spensdk.example.tools.ToolTextDialogInput;


public class SPen_Example_SAMMEditor extends Activity {

	private final String TAG = "SPenSDK Sample";
	private final boolean SHOW_LOG = false;

	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	// remove 'final' to edit AppID
	private String APPLICATION_ID_NAME = "SDK Sample Application";	
	private int APPLICATION_ID_VERSION_MAJOR = 1;
	private int APPLICATION_ID_VERSION_MINOR = 0;
	private String APPLICATION_ID_VERSION_PATCHNAME = "Debug";

	//==============================
	// Menu
	//==============================

	private final int MENU_FILE_GROUP = 1000;
	private final int MENU_FILE_NEW = 1001;
	private final int MENU_FILE_LOAD = 1002;
	private final int MENU_FILE_SAVE_AS = 1003;
	private final int MENU_FILE_CHECK_CANVAS_EMPTY = 1004;	
	private final int MENU_FILE_PLAY_NORMAL = 1005;


	private final int MENU_EDIT_GROUP = 2000;
	private final int MENU_EDIT_DELETE_OBJECT = 2010;
	private final int MENU_EDIT_ROTATE_DEGREE = 2020;
	private final int MENU_EDIT_COPY = 2030;
	private final int MENU_EDIT_CUT = 2040;
	private final int MENU_EDIT_PASTE = 2050;
	private final int MENU_EDIT_CLEAR_CLIPBOARD = 2060;	
	private final int MENU_EDIT_OBJECT_GROUPING_GROUP = 2070;
	private final int MENU_EDIT_OBJECT_GROUPING_UNGROUP = 2080;
	private final int MENU_EDIT_OBJECT_DEPTH_CHANGE = 2090;

	private final int MENU_DATA_META_GROUP = 3000;
	private final int MENU_DATA_SET_TITLE = 3001;	
	private final int MENU_DATA_ATTACH_FILE_ADD = 3002;
	private final int MENU_DATA_ATTACH_FILE_SHOW = 3003;
	private final int MENU_DATA_EXTRA_DATA = 3004;
	private final int MENU_DATA_ADD_TAG = 3005;
	private final int MENU_DATA_REMOVE_TAG = 3006;
	private final int MENU_DATA_SET_PREFERENCE = 3007;
	private final int MENU_DATA_SET_AUTHOR = 3008;	
	private final int MENU_DATA_SET_HYPERTEXT = 3009;
	private final int MENU_DATA_SET_GEOTAG = 3010;

	private final int MENU_DATA_BACKGROUND_GROUP = 4000;
	private final int MENU_DATA_BACKGROUND_SET_PAGE_MEMO = 4001;
	private final int MENU_DATA_BACKGROUND_SET_COLOR = 4002;
	private final int MENU_DATA_BACKGROUND_SET_IMAGE = 4003;	
	private final int MENU_DATA_BACKGROUND_SET_IMAGE_EFFECT = 4004;		
	private final int MENU_DATA_BACKGROUND_CLEAR = 4005;
	private final int MENU_DATA_BACKGROUND_SET_INITIAL_FG = 4006;
	private final int MENU_DATA_BACKGROUND_CLEAR_INITIAL_FG = 4007;
	private final int MENU_DATA_BACKGROUND_SET_VOICE_RECORDING = 4008;
	private final int MENU_DATA_BACKGROUND_SET_AUDIO_FILE  = 4009;	
	private final int MENU_DATA_BACKGROUND_CLEAR_AUDIO  = 4010;	

	private final int MENU_INSERT_GROUP = 5000;
	private final int MENU_INSERT_STROKE = 5001;
	private final int MENU_INSERT_TEXT = 5002;
	private final int MENU_INSERT_IMAGE = 5003;
	private final int MENU_INSERT_FILLING = 5004;
	private final int MENU_INSERT_BEAUTIFY_STROKE = 5005;

	private final int MENU_MORE_GROUP = 6000;
	private final int MENU_MORE_APP_ID = 6001;
	private final int MENU_MORE_SHOW_TOTAL_INFO = 6002;
	private final int MENU_MORE_SAMM_OPTION_SETTING = 6003;
	private final int MENU_MORE_ANIMATION_OPTION_SETTING = 6004;
	private final int MENU_MORE_OTHER_OPTION = 6005;



	//==============================
	// Activity Request code
	//==============================
	private final int REQUEST_CODE_INSERT_VIDEO_OBJECT = 99;
	private final int REQUEST_CODE_INSERT_IMAGE_OBJECT = 100;	
	private final int REQUEST_CODE_FILE_SELECT = 101;
	private final int REQUEST_CODE_VOICE_RECORD = 102;
	private final int REQUEST_CODE_SET_BACKGROUND_AUDIO = 103;
	private final int REQUEST_CODE_ATTACH_SELECT = 104;
	private final int REQUEST_CODE_SET_PAGE_MEMO = 105;
	private final int REQUEST_CODE_SELECT_IMAGE_BACKGROUND = 106;
	private final int REQUEST_CODE_SELECT_IMAGE_FOREGROUND = 107;
	private final int REQUEST_CODE_TOTAL_INFO_SHOW = 108;
	private final int REQUEST_CODE_PRIVIEW_BUTTON_CLICK = 109;
	private final int REQUEST_CODE_INPUT_AUTHOR_IMAGE = 110;
	private final int REQUEST_CODE_OTHER_OPTION = 111;


	//==============================
	// Object Depth Cange Constants
	//==============================
	private final int OBJECT_DEPTH_CHANGE_FORWARD =  0;
	private final int OBJECT_DEPTH_CHANGE_BACKWARD = 1;
	private final int OBJECT_DEPTH_CHANGE_FRONT = 2;
	private final int OBJECT_DEPTH_CHANGE_BACK = 3;


	//==============================
	// Hover Pointer Constants
	//==============================
	private final int HOVER_POINTER_DEFAULT =  0;
	private final int HOVER_POINTER_SIMPLE_ICON = 1;
	private final int HOVER_POINTER_SIMPLE_DRAWABLE = 2;
	private final int HOVER_POINTER_SPEN = 3;
	private final int HOVER_POINTER_SNOTE = 4;

	private final int HOVER_SHOW_ALWAYS_ONHOVER = 0;
	private final int HOVER_SHOW_ONCE_ONHOVER = 1;

	//==============================
	// Hover Pointer Constants
	//==============================
	private final int SIDE_BUTTON_CHANGE_SETTING = 0;
	private final int SIDE_BUTTON_SHOW_SETTING_VIEW = 1;

	//==============================
	// Insert Object Constants
	//==============================
	private final int INSERT_IMAGE = 0;
	private final int INSERT_VIDEO = 1;

	public final static String KEY_EDITOR_VERSION = "EditorVersion";
	public final static String KEY_EDITOR_GUI_STYLE = "EditorGUIStyle";

	//==============================
	// Variables
	//==============================
	Context mContext = null;

	private String  mTempAMSFolderPath = null;
	private String  mTempAMSAttachFolderPath = null;
	private int mSideButtonStyle;


	private final String DEFAULT_SAVE_PATH = "SPenSDK";
	private final String DEFAULT_ATTACH_PATH = "SPenSDK/attach";
	private final String DEFAULT_FILE_EXT = ".png";

	//	private RelativeLayout	mViewContainer;
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
	private ImageView		mPlayBtn;

	private String mAuthorImagePath;
	private String mAuthorImageTempPath;
	private ImageView mAuthorImageView;
	private boolean mMultiSelectionMode = false;
	private int		mSettingviewType;
	private int		mCanvasHeight;
	private int		mCanvasWidth;

	private OnColorChangedListener mBGColorPickerListener;

	private boolean mbContentsOrientationHorizontal = false;
	private int mPreferenceCheckedItem = 0;
	private boolean mbPreviewBtnClick = false;

	private int mEditorGUIStyle = SCanvasConstants.SCANVAS_GUI_STYLE_NORMAL;
	private boolean mbSingleSelectionFixedLayerMode = false;
	private String currentLanguage = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.editor_samm_editor);

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
		mInsertBtn = (ImageView) findViewById(R.id.insertBtn);
		mInsertBtn.setOnClickListener(mInsertBtnClickListener);
		mColorPickerBtn = (ImageView) findViewById(R.id.colorPickerBtn);
		mColorPickerBtn.setOnClickListener(mColorPickerListener);

		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);

		mPlayBtn = (ImageView) findViewById(R.id.playBtn);
		mPlayBtn.setOnClickListener(playBtnClickListener);

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
		mbSingleSelectionFixedLayerMode = intent.getBooleanExtra(KEY_EDITOR_VERSION, mbSingleSelectionFixedLayerMode);
		mEditorGUIStyle = intent.getIntExtra(KEY_EDITOR_GUI_STYLE, mEditorGUIStyle);

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

		// Save current locale
		Configuration config = getBaseContext().getResources().getConfiguration();
		currentLanguage = config.locale.getLanguage();


		
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
				// Place SCanvasView In the Center
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mSCanvas.getLayoutParams(); 
				layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0); 
				mSCanvas.setLayoutParams(layoutParams); 

				// Application Identifier Setting
				if(!mSCanvas.setAppID(APPLICATION_ID_NAME, APPLICATION_ID_VERSION_MAJOR, APPLICATION_ID_VERSION_MINOR,APPLICATION_ID_VERSION_PATCHNAME))
					Toast.makeText(mContext, "Fail to set App ID.", Toast.LENGTH_LONG).show();

				// Set Title
				if(!mSCanvas.setTitle("SPen-SDK Test"))
					Toast.makeText(mContext, "Fail to set Title.", Toast.LENGTH_LONG).show();

				// Set Initial Setting View Size
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);

				// Set Editor Version (mEditorGUIStyle)	
				// - SCanvasConstants.SCANVAS_GUI_STYLE_NORMAL;
				// - SCanvasConstants.SCANVAS_GUI_STYLE_KIDS;
				mSCanvas.setSCanvasGUIStyle(mEditorGUIStyle);

				// Set Editor GUI Style (mbSingleSelectionFixedLayerMode)
				// - true :  S Pen SDK 2.2 (Single selection, Fixed layer Editor : Image-Text-Stroke ordering)
				// - false : S Pen SDK 2.3 (Multi-selection, Flexible layer Editor : Input ordering)				
				mSCanvas.setSingleSelectionFixedLayerMode(mbSingleSelectionFixedLayerMode);
				if(mbSingleSelectionFixedLayerMode)
					mSelectionModeBtn.setVisibility(View.GONE);

				mCanvasWidth = mSCanvas.getWidth();
				mCanvasHeight = mSCanvas.getHeight();
				// Get the direction of contents(Canvas)
				if(mCanvasWidth > mCanvasHeight)
					mbContentsOrientationHorizontal = true;
				else
					mbContentsOrientationHorizontal = false;

				applyOtherOption();

				// Update button state
				updateModeState();
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
		// OnSettingStrokeChangeListener Listener 
		//------------------------------------------------		
		SettingStrokeChangeListener	mSettingStrokeChangeListener = new SettingStrokeChangeListener() {
			@Override
			public void onClearAll(boolean bClearAllCompleted) {
				// If don't set eraser mode, then change to pen mode automatically.
				if(bClearAllCompleted)
					updateModeState();
			}
			@Override
			public void onEraserWidthChanged(int eraserWidth) {				
			}

			@Override
			public void onStrokeColorChanged(int strokeColor) {
			}

			@Override
			public void onStrokeStyleChanged(int strokeStyle) {			
			}

			@Override
			public void onStrokeWidthChanged(int strokeWidth) {				
			}

			@Override
			public void onStrokeAlphaChanged(int strokeAlpha) {								
			}

			@Override
			public void onBeautifyPenStyleParameterCursiveChanged(int cursiveParameter) {				
			}

			@Override
			public void onBeautifyPenStyleParameterDummyChanged(int dummyParamter) {				
			}

			@Override
			public void onBeautifyPenStyleParameterModulationChanged(int modulationParamter) {				
			}

			@Override
			public void onBeautifyPenStyleParameterSustenanceChanged(int sustenanceParamter) {				
			}

			@Override
			public void onBeautifyPenStyleParameterBeautifyStyleIDChanged(int styleID) {				
			}

			@Override
			public void onBeautifyPenStyleParameterFillStyleChanged(int fillStyle) {				
			}
		};

		mSCanvas.setSettingStrokeChangeListener(mSettingStrokeChangeListener);

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

		//------------------------------------------------
		// File Processing 
		//------------------------------------------------
		mSCanvas.setFileProcessListener(new FileProcessListener() {
			@Override
			public void onChangeProgress(int nProgress) {
				//Log.i(TAG, "Progress = " + nProgress);
			}

			@Override
			public void onLoadComplete(boolean bLoadResult) {			 
				if(bLoadResult){
					// Show Application Identifier
					String appID = mSCanvas.getAppID();
					Toast.makeText(SPen_Example_SAMMEditor.this, "Load AMS File("+ appID + ") Success!", Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(SPen_Example_SAMMEditor.this, "Load AMS File Fail!", Toast.LENGTH_LONG).show();				
				}
			}
		});

		//------------------------------------------------
		// SettingView Listener : Optional
		//------------------------------------------------				
		mSCanvas.setSettingViewShowListener(new SettingViewShowListener() {
			@Override
			public void onEraserSettingViewShow(boolean bVisible) {
				if(SHOW_LOG){		
					if(bVisible) Log.i(TAG, "Eraser setting view is shown");
					else		 Log.i(TAG, "Eraser setting view is closed");
				}
			}
			@Override
			public void onPenSettingViewShow(boolean bVisible) {
				if(SHOW_LOG){		
					if(bVisible) Log.i(TAG, "Pen setting view is shown");
					else		 Log.i(TAG, "Pen setting view is closed");
				}
			}
			@Override
			public void onTextSettingViewShow(boolean bVisible) {
				if(SHOW_LOG){		
					if(bVisible) Log.i(TAG, "Text setting view is shown");
					else		 Log.i(TAG, "Text setting view is closed");
				}
			}
			@Override
			public void onFillingSettingViewShow(boolean bVisible) {
				if(SHOW_LOG){		
					if(bVisible) Log.i(TAG, "Text setting view is shown");
					else		 Log.i(TAG, "Text setting view is closed");
				}
			}
		});


		//--------------------------------------------
		// Set S pen Touch Listener
		//--------------------------------------------
		mSCanvas.setSPenTouchListener(new SPenTouchListener(){

			@Override
			public boolean onTouchFinger(View view, MotionEvent event) {
				return false;
			}

			@Override
			public boolean onTouchPen(View view, MotionEvent event) {
				return false;
			}

			@Override
			public boolean onTouchPenEraser(View view, MotionEvent event) {
				return false;
			}

			@Override
			public void onTouchButtonDown(View view, MotionEvent event) {				
			}

			@Override
			public void onTouchButtonUp(View view, MotionEvent event) {
				showObjectPopUpMenu((int)event.getX(), (int)event.getY());
			}		

		});


		//--------------------------------------------
		// Set S pen HoverListener
		//--------------------------------------------
		mSCanvas.setSPenHoverListener(new SPenHoverListener(){

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

				//--------------------------------------------------------------
				// Show popup menu if the object is selected or if sobject exist in clipboard
				//--------------------------------------------------------------
				if(mSCanvas.isSObjectSelected() || mSCanvas.isClipboardSObjectListExist()){
					showObjectPopUpMenu((int)event.getX(), (int)event.getY());
				}
				else {	
					if(!mSCanvas.isVideoViewExist()){
						if(mSideButtonStyle == SIDE_BUTTON_CHANGE_SETTING){

							boolean bIncludeDefinedSetting = true;
							boolean bIncludeCustomSetting = true;
							boolean bIncludeEraserSetting = true;
							SettingStrokeInfo settingInfo = mSCanvas.getSettingViewNextStrokeInfo(bIncludeDefinedSetting, bIncludeCustomSetting, bIncludeEraserSetting);

							if(settingInfo!=null) {
								mSCanvas.setSettingViewStrokeInfo(settingInfo);
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
						} // end of if(mSideButtonStyle == SIDE_BUTTON_CHANGE_SETTING){
						// Show SettingView(Toggle SettingView)
						else if(mSideButtonStyle == SIDE_BUTTON_SHOW_SETTING_VIEW){
							doHoverButtonUp((int)event.getX(), (int)event.getY());
						}
					}
				} // end of else // if(mSCanvas.isSObjectSelected() || mSCanvas.isClipboardSObjectListExist()){
			} // end of onHoverButtonUp
		});


		// Update UI
		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);

		// create basic save/road file path
		File sdcard_path = Environment.getExternalStorageDirectory();
		File default_path =  new File(sdcard_path, DEFAULT_SAVE_PATH);
		if(!default_path.exists()){
			if(!default_path.mkdirs()){
				Log.e(TAG, "Default Save Path Creation Error");
				return ;
			}
		}

		// attach file path
		File spen_attach_path =  new File(sdcard_path, DEFAULT_ATTACH_PATH);
		if(!spen_attach_path.exists()){
			if(!spen_attach_path.mkdirs()){
				Log.e(TAG, "Default Attach Path Creation Error");
				return ;
			}
		}

		mTempAMSFolderPath = default_path.getAbsolutePath();
		mTempAMSAttachFolderPath = spen_attach_path.getAbsolutePath();

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
		if(mSCanvas.isVideoViewExist()){
			mSCanvas.closeSAMMVideoView();
			updateModeState();
		}		
		
		if(!newConfig.locale.getLanguage().equals(currentLanguage)){			
			// Recreate SettingView to text string as locale 
			mSCanvas.recreateSettingView();
			currentLanguage = newConfig.locale.getLanguage();
		}				
		
		super.onConfigurationChanged(newConfig);
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
			boolean bMovingMode = mSCanvas.isMovingMode();
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
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
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
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
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
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
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
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
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
			boolean bMovingMode = mSCanvas.isMovingMode();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){					
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
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
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
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
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
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
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

	// insert object (image, video)
	private OnClickListener mInsertBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mInsertBtn)) {	
				String items[] = {"Image", "Video"};
				AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
				ad.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));	// Android Resource
				ad.setTitle(getResources().getString(R.string.app_name))				
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case INSERT_IMAGE:
							callGalleryForInputImage(REQUEST_CODE_INSERT_IMAGE_OBJECT);
							break;						
						case INSERT_VIDEO:
							insertVideoObjectSelection();
							break;
						}						
						dialog.dismiss();
					}
				})
				.show();
			}
		}
	};

	private void insertVideoObjectSelection(){

		String items[] = {"Video file", "Video URL Link"};
		AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
		ad.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));	// Android Resource
		ad.setTitle(getResources().getString(R.string.app_name))				
		.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case 0:	// Video file
					Intent intent = new Intent(SPen_Example_SAMMEditor.this, SPen_Example_VideoDemoFileList.class);
					startActivityForResult(intent, REQUEST_CODE_INSERT_VIDEO_OBJECT);					
					break;
				case 1: 	// Video URL Link
					LayoutInflater factory = LayoutInflater.from(SPen_Example_SAMMEditor.this);
					final View textEntryView = factory.inflate(R.layout.alert_dialog_get_hypertext, null);
					TextView textTitle = (TextView)textEntryView.findViewById(R.id.textTitle);
					textTitle.setText("Enter video url here(e.g.http://www.youtube.com/watch?v=VIDEOID");


					// Set the default value
					String videoURL = "http://www.youtube.com/watch?v=oC6OVqkcB7I";	
					// String videoURL = "http://sports.news.naver.com/videoCenter/index.nhn?uCategory=wfootball&id=36020";						
					EditText et = (EditText)textEntryView.findViewById(R.id.text);
					et.setText(videoURL);


					AlertDialog dlg = new AlertDialog.Builder(SPen_Example_SAMMEditor.this)
					.setTitle("Video URL")
					.setView(textEntryView)
					.setPositiveButton("Done", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							EditText et = (EditText)textEntryView.findViewById(R.id.text);
							String videoURL = et.getText().toString();
							addVideoURLObject(videoURL);
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							/* User clicked cancel so do some stuff */
						}
					})
					.create();
					dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
					dlg.show();


					break;
				}						
				dialog.dismiss();
			}
		})
		.show();
	}


	private boolean addVideoFileObject(String strVideoFile){
		// Large size to use (consume large heap memory)
		// Bitmap bmpThumbnail = ThumbnailUtils.createVideoThumbnail(strVideoFile , Thumbnails.FULL_SCREEN_KIND);
		// Proper size to use
		Bitmap bmpThumbnail = ThumbnailUtils.createVideoThumbnail(strVideoFile , Thumbnails.MINI_KIND);
		//		Bitmap bmpThumbnail=ThumbnailUtils.extractThumbnail(bmpThumbnail1, 100, 80);		
		if(bmpThumbnail == null) {
			Toast.makeText(this, "Extract video thumbnail Fail!", Toast.LENGTH_LONG).show();
			return false;
		}

		RectF rectF = getVideoObjectDefaultRect(bmpThumbnail, false);
		SObjectVideo sVideoObject = new SObjectVideo();
		sVideoObject.setRect(rectF);		
		if(bmpThumbnail.isMutable())
			sVideoObject.setThumbnailImageBitmap(bmpThumbnail.copy(Bitmap.Config.ARGB_8888, false));
		else
			sVideoObject.setThumbnailImageBitmap(bmpThumbnail);

		sVideoObject.setStyle(SObjectVideo.SAMM_VIDEOSTYLE_NORMAL);
		sVideoObject.setVideoPath(strVideoFile);

		return addSAMMVideo(sVideoObject);
	}

	private boolean addVideoURLObject(String strVideoURL){
		// Temporary
		Bitmap bmpThumbnail = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.video_link);
		RectF rectF = getVideoObjectDefaultRect(bmpThumbnail, true);
		SObjectVideo sVideoObject = new SObjectVideo();
		sVideoObject.setRect(rectF);
		sVideoObject.setThumbnailImageBitmap(bmpThumbnail);
		sVideoObject.setStyle(SObjectVideo.SAMM_VIDEOSTYLE_URL);
		sVideoObject.setVideoURL(strVideoURL);

		return addSAMMVideo(sVideoObject);
	}

	private boolean addSAMMVideo(SObjectVideo sVideoObject){
		if(mSCanvas.insertSAMMVideo(sVideoObject, true)){
			Toast.makeText(this, "Insert video Success!", Toast.LENGTH_SHORT).show();
			return true;
		}
		else{
			Toast.makeText(this, "Insert video Fail!", Toast.LENGTH_LONG).show();
			return false;
		}	
	}


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

	// play 
	private OnClickListener playBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mPlayBtn)) {
				if(mbPreviewBtnClick)
					return;
				mbPreviewBtnClick = true;	
				previewAnimation();	
			}
		}
	};

	private OnClickListener inputAuthorImage = new OnClickListener() {

		@Override
		public void onClick(View v) {
			callGalleryForInputImage(REQUEST_CODE_INPUT_AUTHOR_IMAGE);
		}
	};

	// Update tool button
	private void updateModeState(){
		SPenSDKUtils.updateModeState(mSCanvas, null, null, mPenBtn, mEraserBtn, mTextBtn, mFillingBtn, mInsertBtn, mColorPickerBtn, mPlayBtn, mSelectionModeBtn);
	}	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Check result error
		if(requestCode != REQUEST_CODE_OTHER_OPTION){
			if(resultCode!=RESULT_OK)
				return;		
			if(data == null)
				return;
		}

		if(requestCode==REQUEST_CODE_FILE_SELECT){
			Bundle bundle = data.getExtras();
			if(bundle == null)
				return;
			String strFileName = bundle.getString(ToolListActivity.EXTRA_SELECTED_FILE);
			loadSAMMFile(strFileName);						
		}
		else if(requestCode==REQUEST_CODE_VOICE_RECORD){
			if(data.getBooleanExtra("VOICERECORD", false)){
				if(mSCanvas.setBGAudioAsRecordedVoice()){
					Toast.makeText(this, "Set Background audio voice recording Success!", Toast.LENGTH_SHORT).show();	
				}
				else{
					Toast.makeText(this, "Set Background audio voice recording Fail!", Toast.LENGTH_LONG).show();    				
				}
			}   
		}
		else if(requestCode==REQUEST_CODE_SET_BACKGROUND_AUDIO){
			String strBackgroundAudioFileName = data.getStringExtra("BackgroundAudioFileName");				
			if(mSCanvas.setBGAudioFile(strBackgroundAudioFileName)){
				Toast.makeText(this, "Set Background audio file("+ strBackgroundAudioFileName +") Success!", Toast.LENGTH_SHORT).show();	
			}
			else{
				Toast.makeText(this, "Set Background audio file("+ strBackgroundAudioFileName +") Fail!", Toast.LENGTH_LONG).show();    				
			}	    						 
		}
		else if(requestCode==REQUEST_CODE_ATTACH_SELECT){
			Bundle bundle = data.getExtras();
			if(bundle == null)
				return;
			String strFileName = bundle.getString(ToolListActivity.EXTRA_SELECTED_FILE);

			SDataAttachFile attachData = new SDataAttachFile();
			attachData.setFileData(strFileName, "SPen Example Selected File");
			if(mSCanvas.attachFile(attachData)){
				Toast.makeText(this, "Attach file("+ strFileName +") Success!", Toast.LENGTH_SHORT).show();	
			}
			else{
				Toast.makeText(this, "Attach file("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();    				
			}
		}
		else if(requestCode == REQUEST_CODE_INSERT_IMAGE_OBJECT) {    			
			Uri imageFileUri = data.getData();
			if(imageFileUri == null)
				return;
			String imagePath = SPenSDKUtils.getRealPathFromURI(this, imageFileUri);

			// Check Valid Image File
			if(!SPenSDKUtils.isValidImagePath(imagePath))
			{
				Toast.makeText(this, "Invalid image path or web image", Toast.LENGTH_LONG).show();	
				return;
			}

			// canvas option setting
			SOptionSCanvas canvasOption = mSCanvas.getOption();					
			if(canvasOption == null)
				return;

			if(canvasOption.mSAMMOption == null)
				return;

			canvasOption.mSAMMOption.setContentsQuality(PreferencesOfSAMMOption.getPreferenceSaveImageQuality(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);

			RectF rectF = getDefaultRect(imagePath);
			int nContentsQualityOption = canvasOption.mSAMMOption.getContentsQuality();
			SObjectImage sImageObject = new SObjectImage(nContentsQualityOption);
			sImageObject.setRect(rectF);
			sImageObject.setImagePath(imagePath);

			if(mSCanvas.insertSAMMImage(sImageObject, true)){
				Toast.makeText(this, "Insert image file("+ imagePath +") Success!", Toast.LENGTH_SHORT).show();	
			}
			else{
				Toast.makeText(this, "Insert image file("+ imagePath +") Fail!", Toast.LENGTH_LONG).show();    				
			}
		}
		else if(requestCode == REQUEST_CODE_INSERT_VIDEO_OBJECT) {
			// Temporary
			String strVideoFile = data.getStringExtra("videofilename");
			if(addVideoFileObject(strVideoFile)) {
				Toast.makeText(this, "Insert video file("+ strVideoFile +") Success!", Toast.LENGTH_LONG).show();
			}else {
				Toast.makeText(this, "Insert video file("+ strVideoFile +") Fail!", Toast.LENGTH_LONG).show();
			}
		}
		else if(requestCode == REQUEST_CODE_SELECT_IMAGE_BACKGROUND) {    			
			Uri imageFileUri = data.getData();
			if(imageFileUri == null)
				return;
			String strBackgroundImagePath = SPenSDKUtils.getRealPathFromURI(this, imageFileUri);

			// Check Valid Image File
			if(!SPenSDKUtils.isValidImagePath(strBackgroundImagePath))
			{
				Toast.makeText(this, "Invalid image path or web image", Toast.LENGTH_LONG).show();	
				return;
			}

			// Set SCanvas
			if(!mSCanvas.setBGImagePath(strBackgroundImagePath)){
				Toast.makeText(mContext, "Fail to set Background Image Path.", Toast.LENGTH_LONG).show();
			}
		}
		else if(requestCode == REQUEST_CODE_SELECT_IMAGE_FOREGROUND) {    			
			Uri imageFileUri = data.getData();
			if(imageFileUri == null)
				return;
			String strBackgroundImagePath = SPenSDKUtils.getRealPathFromURI(this, imageFileUri);

			// Check Valid Image File
			if(!SPenSDKUtils.isValidImagePath(strBackgroundImagePath))
			{
				Toast.makeText(this, "Invalid image path or web image", Toast.LENGTH_LONG).show();	
				return;
			}

			Bitmap loadBitmap = SPenSDKUtils.getSafeResizingBitmap(strBackgroundImagePath, mSCanvas.getWidth(), mSCanvas.getHeight(), true);

			// canvas option setting
			SOptionSCanvas canvasOption = mSCanvas.getOption();					
			if(canvasOption == null)
				return;
			canvasOption.mSAMMOption.setConvertCanvasSizeOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasSize(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);

			// Set SCanvas
			if(!mSCanvas.setClearImageBitmap(loadBitmap)){
				Toast.makeText(mContext, "Fail to set Background Image Path.", Toast.LENGTH_LONG).show();
			}
		}
		else if(requestCode == REQUEST_CODE_SET_PAGE_MEMO) {
			String tmpStr = data.getStringExtra(ToolTextDialogInput.TEXT_DIALOG_INPUT);						
			SDataPageMemo pageMemo = new SDataPageMemo();
			pageMemo.setText(tmpStr);
			if(mSCanvas.setPageMemo(pageMemo, 0)){
				Toast.makeText(this, "Set Page Memo Success!", Toast.LENGTH_SHORT).show();	
			}
			else{
				Toast.makeText(this, "Set Page Memo Fail!", Toast.LENGTH_LONG).show();    				
			}
		}
		else if(requestCode==REQUEST_CODE_TOTAL_INFO_SHOW){
			String tmpStr = data.getStringExtra(ToolFileTotalInfoShow.EXTRA_SAMM_FILE_INFO);
			if(tmpStr != null) {
				File saveFile = new File(tmpStr);
				if(saveFile.exists())
				{
					if(!saveFile.delete())
					{
						Log.e(TAG, "Fail to delete SaveFile!");
						return;
					}
				}
			}
		}    		
		else if(requestCode==REQUEST_CODE_PRIVIEW_BUTTON_CLICK) {    			
			mbPreviewBtnClick = data.getBooleanExtra(SPen_Example_AnimationViewer.EXTRA_PLAY_BUTTON_CLICK, false);
		}
		else if(requestCode==REQUEST_CODE_INPUT_AUTHOR_IMAGE){
			Uri imageFileUri = data.getData();
			if(imageFileUri == null)
				return;
			mAuthorImageTempPath = SPenSDKUtils.getRealPathFromURI(this, imageFileUri);		
			mAuthorImageView.setImageURI(imageFileUri);
			mAuthorImageView.invalidate();
		}
		else if(requestCode==REQUEST_CODE_OTHER_OPTION){
			applyOtherOption();
		}
	}


	RectF getDefaultRect(String strImagePath){
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

	RectF getVideoObjectDefaultRect(Bitmap videoThumbnail, boolean bVideoLink){
		if(videoThumbnail==null)
			return null;
		// Rect Region : Consider image real size		
		int nImageWidth = videoThumbnail.getWidth();
		int nImageHeight = videoThumbnail.getHeight();
		int nScreenWidth = mSCanvas.getWidth();
		int nScreenHeight = mSCanvas.getHeight();    			
		int nBoxRadius;
		if(bVideoLink)
			nBoxRadius = (nScreenWidth>nScreenHeight) ? nScreenHeight/8 : nScreenWidth/8;
		else
			nBoxRadius = (nScreenWidth>nScreenHeight) ? nScreenHeight/3 : nScreenWidth/3;
		int nCenterX = nScreenWidth/2;
		int nCenterY = nScreenHeight/2;
		if(nImageWidth > nImageHeight)
			return new RectF(nCenterX-nBoxRadius,nCenterY-(nBoxRadius*nImageHeight/nImageWidth),nCenterX+nBoxRadius,nCenterY+(nBoxRadius*nImageHeight/nImageWidth));
		else
			return new RectF(nCenterX-(nBoxRadius*nImageWidth/nImageHeight),nCenterY-nBoxRadius,nCenterX+(nBoxRadius*nImageWidth/nImageHeight),nCenterY+nBoxRadius);
	}

	void deleteSelectedSObject(){
		if(!mSCanvas.deleteSelectedSObject()){
			Toast.makeText(mContext, "Fail to delete object list.", Toast.LENGTH_LONG).show();
		}
	}

	void rotateSelectedObject(){
		//-------------------------------
		// layout setting
		//-------------------------------
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.alert_dialog_get_rotation_angle, null);
		TextView majorVersion = (TextView)textEntryView.findViewById(R.id.rotationangle);
		majorVersion.setText("Enter Rotation Angle here");

		// Setting 
		int curRotateAngle = 0;
		EditText rotationangle_edit = (EditText)textEntryView.findViewById(R.id.rotationangle_edit);
		if(rotationangle_edit!=null) rotationangle_edit.setText(Integer.toString(curRotateAngle));

		AlertDialog dlg = new AlertDialog.Builder(this)
		.setTitle("Set Rotation Angle")
		.setView(textEntryView)
		.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText rotationangle_edit = (EditText)textEntryView.findViewById(R.id.rotationangle_edit);

				// Update Rotation Angle
				int rotationAngle;
				try {
					rotationAngle = Integer.parseInt(rotationangle_edit.getText().toString());
				}catch(NumberFormatException e) {
					Toast.makeText(mContext, "Can not parse rotation angle.", Toast.LENGTH_LONG).show();
					return;
				}
				if(rotationAngle >= 0 && rotationAngle < 360){
					if(!mSCanvas.rotateSelectedSObject((float)rotationAngle)){
						Toast.makeText(mContext, "Fail to rotate object.", Toast.LENGTH_LONG).show();
					}
					else{
						Toast.makeText(mContext, "Object rotated.", Toast.LENGTH_SHORT).show();
					}
				}
				else{
					inputQuestion();
				}
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				/* User clicked cancel so do some stuff */
			}
		})
		.create();
		dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		dlg.show();

	}

	void copySelectedObject(){
		boolean bResetClipboard = true;
		if(!mSCanvas.copySelectedSObjectList(bResetClipboard)){
			Toast.makeText(mContext, "Fail to copy selected object list.", Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(mContext, "Object copied.", Toast.LENGTH_SHORT).show();
		}
	}

	void cutSelectedObject(){
		boolean bResetClipboard = true;
		if(!mSCanvas.cutSelectedSObjectList(bResetClipboard)){
			Toast.makeText(mContext, "Fail to cut selected object list.", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(mContext, "Object cut.", Toast.LENGTH_SHORT).show();
		}
	}

	void clearClipboardObject(){
		mSCanvas.clearClipboardSObjectList();
		Toast.makeText(mContext, "Clipboard cleared.", Toast.LENGTH_SHORT).show();
	}

	void pasteClipboardObject(int nEventPositionX, int nEventPositionY){
		boolean bSelectObject = true;
		// mapping to the matrix
		PointF mapPoint = mSCanvas.mapSCanvasPoint(new PointF(nEventPositionX, nEventPositionY)) ;
		int nMappedEventX = (int)mapPoint.x;
		int nMappedEventY =(int)mapPoint.y;
		if(!mSCanvas.pasteClipboardSObjectList(bSelectObject, nMappedEventX, nMappedEventY)){
			Toast.makeText(mContext, "Fail to paste clipboard object list.", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(mContext, "Object pasted.", Toast.LENGTH_SHORT).show();
		}
	}

	void previewAnimation(){
		// canvas option setting		
		SOptionSCanvas canvasOption = new SOptionSCanvas();					
		// compact size for fast save
		canvasOption.mSAMMOption.setSaveImageSize(SOptionSAMM.SAMM_SAVE_OPTION_COMPACT_SIZE);
		// Minimum Quality for fast save
		canvasOption.mSAMMOption.setContentsQuality(SOptionSAMM.SAMM_CONTENTS_QUALITY_MINIMUM);
		// Create new image file to save
		canvasOption.mSAMMOption.setCreateNewImageFile(true);
		canvasOption.mSAMMOption.setEncodeForegroundImage(false);
		canvasOption.mSAMMOption.setEncodeThumbnailImage(false);
		canvasOption.mSAMMOption.setEncodeObjectData(true);
		canvasOption.mSAMMOption.setEncodeVideoFileDataOption(false);
		// option setting
		mSCanvas.setOption(canvasOption);

		// temporarily save SAMMData
		String sDataKey = mSCanvas.saveSAMMData();	

		Intent intent = new Intent(this, SPen_Example_AnimationViewer.class);
		intent.putExtra(SPen_Example_AnimationViewer.EXTRA_VIEW_FILE_PATH, sDataKey);
		intent.putExtra(SPen_Example_AnimationViewer.EXTRA_PLAY_CANVAS_WIDTH, mCanvasWidth);
		intent.putExtra(SPen_Example_AnimationViewer.EXTRA_PLAY_CANVAS_HEIGHT, mCanvasHeight);
		intent.putExtra(SPen_Example_AnimationViewer.EXTRA_CONTENTS_ORIENTATION, mbContentsOrientationHorizontal);
		intent.putExtra(SPen_Example_AnimationViewer.EXTRA_SINGLE_SELECTION_LAYER_MODE, mbSingleSelectionFixedLayerMode);
		startActivityForResult(intent, REQUEST_CODE_PRIVIEW_BUTTON_CLICK);
	}


	void voiceRecording(){
		// temporarily save SAMMData
		String sDataKey = mSCanvas.saveSAMMData();	

		Intent intent = new Intent(this, SPen_Example_VoiceRecorder.class);
		intent.putExtra(SPen_Example_VoiceRecorder.EXTRA_VIEW_FILE_PATH, sDataKey);
		intent.putExtra(SPen_Example_VoiceRecorder.EXTRA_PLAY_CANVAS_WIDTH, mCanvasWidth);
		intent.putExtra(SPen_Example_VoiceRecorder.EXTRA_PLAY_CANVAS_HEIGHT, mCanvasHeight);
		intent.putExtra(SPen_Example_VoiceRecorder.EXTRA_SINGLE_SELECTION_LAYER_MODE, mbSingleSelectionFixedLayerMode);
		startActivityForResult(intent, REQUEST_CODE_VOICE_RECORD);
	}



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



	boolean saveSAMMFile(String strFileName, boolean bShowSuccessLog){		
		if(mSCanvas.saveSAMMFile(strFileName)){
			if(bShowSuccessLog){
				Toast.makeText(this, "Save AMS File("+ strFileName +") Success!", Toast.LENGTH_LONG).show();
			}
			return true;
		}
		else{
			Toast.makeText(this, "Save AMS File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
			return false;
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu){			
		SubMenu fileMenu = menu.addSubMenu("File");
		fileMenu.add(MENU_FILE_GROUP, MENU_FILE_NEW, 1, "New");
		fileMenu.add(MENU_FILE_GROUP, MENU_FILE_LOAD, 2, "Load (SAMM file)");
		fileMenu.add(MENU_FILE_GROUP, MENU_FILE_SAVE_AS, 3, "Save (SAMM file)");
		fileMenu.add(MENU_FILE_GROUP, MENU_FILE_CHECK_CANVAS_EMPTY, 4, "Check Empty Canvas");
		fileMenu.add(MENU_FILE_GROUP, MENU_FILE_PLAY_NORMAL, 5, "Animation");

		SubMenu editMenu = menu.addSubMenu("Edit");
		editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_DELETE_OBJECT, 10, "Delete Object");
		editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_ROTATE_DEGREE, 20, "Rotate Object");
		editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_COPY, 30, "Copy Object");
		editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_CUT, 40, "Cut Object");
		editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_PASTE, 50, "Paste Object");
		editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_CLEAR_CLIPBOARD, 60, "Clear Clipboard");
		if(!mbSingleSelectionFixedLayerMode){
			editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_OBJECT_GROUPING_GROUP, 70, "Group");
			editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_OBJECT_GROUPING_UNGROUP, 80, "Ungroup");
			editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_OBJECT_DEPTH_CHANGE, 90, "Object Depth Change");
		}

		SubMenu dataMenu = menu.addSubMenu("Meta Data");		
		dataMenu.add(MENU_DATA_META_GROUP, MENU_DATA_SET_TITLE, 1, "Set Title");
		dataMenu.add(MENU_DATA_META_GROUP, MENU_DATA_ATTACH_FILE_ADD, 2, "Attach File");
		dataMenu.add(MENU_DATA_META_GROUP, MENU_DATA_ATTACH_FILE_SHOW, 3, "Show Attached File");
		dataMenu.add(MENU_DATA_META_GROUP, MENU_DATA_EXTRA_DATA, 4, "Set Extra data");
		dataMenu.add(MENU_DATA_META_GROUP, MENU_DATA_ADD_TAG, 5, "Add TAG");
		dataMenu.add(MENU_DATA_META_GROUP, MENU_DATA_REMOVE_TAG, 6, "Remove TAG");
		dataMenu.add(MENU_DATA_META_GROUP, MENU_DATA_SET_PREFERENCE, 7, "Set Preference");
		dataMenu.add(MENU_DATA_META_GROUP, MENU_DATA_SET_AUTHOR, 8, "Set Author");
		dataMenu.add(MENU_DATA_META_GROUP, MENU_DATA_SET_HYPERTEXT, 9, "Set HyperText");
		dataMenu.add(MENU_DATA_META_GROUP, MENU_DATA_SET_GEOTAG, 10, "Set GeoTag");

		SubMenu dataMenu2 = menu.addSubMenu("Background Data");
		dataMenu2.add(MENU_DATA_BACKGROUND_GROUP, MENU_DATA_BACKGROUND_SET_PAGE_MEMO, 1, "Set Page Memo");
		dataMenu2.add(MENU_DATA_BACKGROUND_GROUP, MENU_DATA_BACKGROUND_SET_COLOR, 2, "Set BG Color");
		dataMenu2.add(MENU_DATA_BACKGROUND_GROUP, MENU_DATA_BACKGROUND_SET_IMAGE, 3, "Set BG Image");
		dataMenu2.add(MENU_DATA_BACKGROUND_GROUP, MENU_DATA_BACKGROUND_SET_IMAGE_EFFECT, 4, "Set BG Image Effect");
		dataMenu2.add(MENU_DATA_BACKGROUND_GROUP, MENU_DATA_BACKGROUND_CLEAR, 5, "Clear BG");
		dataMenu2.add(MENU_DATA_BACKGROUND_GROUP, MENU_DATA_BACKGROUND_SET_INITIAL_FG, 6, "Set Initial FG Image");
		dataMenu2.add(MENU_DATA_BACKGROUND_GROUP, MENU_DATA_BACKGROUND_CLEAR_INITIAL_FG, 7, "Clear Initial FG Image");		
		dataMenu2.add(MENU_DATA_BACKGROUND_GROUP, MENU_DATA_BACKGROUND_SET_VOICE_RECORDING, 8, "BG Voice Recording");
		dataMenu2.add(MENU_DATA_BACKGROUND_GROUP, MENU_DATA_BACKGROUND_SET_AUDIO_FILE, 9, "Set BG Audio File");	
		dataMenu2.add(MENU_DATA_BACKGROUND_GROUP, MENU_DATA_BACKGROUND_CLEAR_AUDIO, 10, "Clear BG Audio");		

		SubMenu insertMenu = menu.addSubMenu("Insert");
		insertMenu.add(MENU_INSERT_GROUP, MENU_INSERT_STROKE, 5, "Insert Stroke Sample");
		insertMenu.add(MENU_INSERT_GROUP, MENU_INSERT_TEXT, 6, "Insert Text Sample");
		insertMenu.add(MENU_INSERT_GROUP, MENU_INSERT_IMAGE, 7, "Insert Image Sample");
		insertMenu.add(MENU_INSERT_GROUP, MENU_INSERT_FILLING, 8, "Insert Filling Sample");
		insertMenu.add(MENU_INSERT_GROUP, MENU_INSERT_BEAUTIFY_STROKE, 9, "Insert Beautify Stroke Sample");

		SubMenu moreMenu = menu.addSubMenu("More");
		moreMenu.add(MENU_MORE_GROUP, MENU_MORE_APP_ID, 1, "Application ID");
		moreMenu.add(MENU_MORE_GROUP, MENU_MORE_SHOW_TOTAL_INFO, 2, "Show Total Info");
		moreMenu.add(MENU_MORE_GROUP, MENU_MORE_SAMM_OPTION_SETTING, 3, "SAMM Option");
		moreMenu.add(MENU_MORE_GROUP, MENU_MORE_ANIMATION_OPTION_SETTING, 4, "Animation Option");
		moreMenu.add(MENU_MORE_GROUP, MENU_MORE_OTHER_OPTION, 5, "Other Option");


		return super.onCreateOptionsMenu(menu);
	} 


	@Override
	public boolean onMenuOpened(int featureId, Menu menu){
		super.onMenuOpened(featureId, menu);

		if (menu == null) 
			return true;

		MenuItem menuItemDeleteObject = menu.findItem(MENU_EDIT_DELETE_OBJECT);
		int nSelectedObjectListType = mSCanvas.getSelectedSObjectType();
		if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_NONE){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(false);			
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_STROKE){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);			
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_IMAGE){			
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);			
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_TEXT){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_FILLING){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);			
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_VIDEO){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_GROUP){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_MIXED){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);			
		}
		else{
			// ??
		}

		MenuItem menuItemImageRotateDegree = menu.findItem(MENU_EDIT_ROTATE_DEGREE);
		if(menuItemImageRotateDegree!=null) menuItemImageRotateDegree.setEnabled(mSCanvas.isSelectedObjectRotatable());

		boolean bSObjectSelected = mSCanvas.isSObjectSelected();

		MenuItem menuItemCopy = menu.findItem(MENU_EDIT_COPY);
		MenuItem menuItemCut = menu.findItem(MENU_EDIT_CUT);
		MenuItem menuItemPaste = menu.findItem(MENU_EDIT_PASTE);
		MenuItem menuItemClearClipboard = menu.findItem(MENU_EDIT_CLEAR_CLIPBOARD);

		if(menuItemCopy!=null) menuItemCopy.setEnabled(bSObjectSelected);			
		if(menuItemCut!=null) menuItemCut.setEnabled(bSObjectSelected);
		if(menuItemPaste!=null) menuItemPaste.setEnabled(mSCanvas.isClipboardSObjectListExist());
		if(menuItemClearClipboard!=null) menuItemClearClipboard.setEnabled(mSCanvas.isClipboardSObjectListExist());

		if(!mbSingleSelectionFixedLayerMode){
			MenuItem menuItemGroup = menu.findItem(MENU_EDIT_OBJECT_GROUPING_GROUP);
			MenuItem menuItemUngroup = menu.findItem(MENU_EDIT_OBJECT_GROUPING_UNGROUP);

			if(menuItemGroup!=null ) menuItemGroup.setEnabled(mSCanvas.isSelectedObjectGroupable());
			if(menuItemUngroup!=null) menuItemUngroup.setEnabled(mSCanvas.isSelectedObjectUngroupable());

			MenuItem menuItemObjectDepthChange = menu.findItem(MENU_EDIT_OBJECT_DEPTH_CHANGE);
			if(menuItemObjectDepthChange!=null) menuItemObjectDepthChange.setEnabled(bSObjectSelected);			
		}
		
		// Stop video
		if(mSCanvas.isVideoViewExist()){
			mSCanvas.closeSAMMVideoView();
			updateModeState();
		}

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()) {		
		//================================================
		// File Menu
		//================================================
		case MENU_FILE_NEW:
		{
			//Confirm New
			AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
			// ad.setIcon(R.drawable.alert_dialog_icon);
			ad.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));	// Android Resource
			ad.setTitle(getResources().getString(R.string.app_name))
			.setMessage("All Data will be initialized")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();		
					mSCanvas.clearSCanvasView();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();		
				}
			})
			.show();
		}
		break;
		case MENU_FILE_LOAD:
		{
			Intent intent = new Intent(this, ToolListActivity.class);
			String [] exts = new String [] { "jpg", "png", "ams" }; // file extension 			
			intent.putExtra(ToolListActivity.EXTRA_LIST_PATH, mTempAMSFolderPath);
			intent.putExtra(ToolListActivity.EXTRA_FILE_EXT_ARRAY, exts);
			intent.putExtra(ToolListActivity.EXTRA_SEARCH_ONLY_SAMM_FILE, true);
			startActivityForResult(intent, REQUEST_CODE_FILE_SELECT);
		}
		break;
		case MENU_FILE_SAVE_AS:
		{
			//-------------------------------
			// layout setting
			//-------------------------------
			//check canvas drawing empty
			if(mSCanvas.isCanvasDrawingEmpty(true)){
				Toast.makeText(mContext, "There is no Canvas Drawing", Toast.LENGTH_LONG).show();
				return true;
			}

			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_text, null);
			TextView textTitle = (TextView)textEntryView.findViewById(R.id.textTitle);
			textTitle.setText("Enter filename to save (default: *.png)");
			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Save As")
			.setView(textEntryView)
			.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText et = (EditText)textEntryView.findViewById(R.id.text);
					String strFileName = et.getText().toString();

					// check file name length, invalid characters, overwrite, extension, etc.
					if(strFileName==null)
						return; 

					if(strFileName.length()<=0){
						Toast.makeText(mContext, "Enter file name to save", Toast.LENGTH_LONG).show();
						return;
					}
					if(!SPenSDKUtils.isValidSaveName(strFileName)) {						
						Toast.makeText(mContext, "Invalid character to save! Save file name : "+ strFileName, Toast.LENGTH_LONG).show();
						return;
					}

					int nExtIndex = strFileName.lastIndexOf(".");	
					if(nExtIndex==-1)	
						strFileName += DEFAULT_FILE_EXT;
					else{
						String strExt = strFileName.substring(nExtIndex + 1);
						if(strExt==null)
							strFileName += DEFAULT_FILE_EXT;
						else{
							if(strExt.compareToIgnoreCase("png")!=0 && strExt.compareToIgnoreCase("jpg")!=0){
								strFileName += DEFAULT_FILE_EXT;
							}							
						}							
					}				

					String saveFileName = mTempAMSFolderPath + "/" + strFileName;
					checkSameSaveFileName(saveFileName);	
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();
		}
		break;		
		case MENU_FILE_CHECK_CANVAS_EMPTY:
		{
			boolean bEmpty = mSCanvas.isCanvasDrawingEmpty(true);
			if(bEmpty) Toast.makeText(mContext, "Canvas is empty", Toast.LENGTH_SHORT).show();
			else Toast.makeText(mContext, "Canvas is NOT empty", Toast.LENGTH_SHORT).show();
		}
		break;
		case MENU_FILE_PLAY_NORMAL:
		{
			previewAnimation();
		}
		break;	
		//================================================
		// Edit Menu
		//================================================
		// Delete Object
		case MENU_EDIT_DELETE_OBJECT:
		{
			deleteSelectedSObject();
		}
		break;		
		// stroke, text, image, filling object can be rotated
		case MENU_EDIT_ROTATE_DEGREE:
		{
			rotateSelectedObject();
		}
		break;	
		// Copy 
		case MENU_EDIT_COPY:
		{	
			copySelectedObject();
		}
		break;
		// Cut 
		case MENU_EDIT_CUT:
		{		
			cutSelectedObject();
		}
		break;
		// Paste
		case MENU_EDIT_PASTE:
		{		
			pasteClipboardObject(-1, -1);			
		}
		break;
		// Clear Clipboard
		case MENU_EDIT_CLEAR_CLIPBOARD:
		{		
			clearClipboardObject();			
		}
		break;		
		//================================================
		// Basic Data Menu
		//================================================
		case MENU_DATA_SET_TITLE:
		{	
			//-------------------------------
			// layout setting
			//-------------------------------
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_text, null);
			TextView textTitle = (TextView)textEntryView.findViewById(R.id.textTitle);
			textTitle.setText("Enter title text here");

			String strTitle = mSCanvas.getTitle();
			if(strTitle!=null){
				EditText et = (EditText)textEntryView.findViewById(R.id.text);
				et.setText(strTitle);
			}
			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Set Title")
			.setView(textEntryView)
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText et = (EditText)textEntryView.findViewById(R.id.text);
					if(!mSCanvas.setTitle(et.getText().toString()))
						Toast.makeText(mContext, "Fail to set Title.", Toast.LENGTH_LONG).show();
					else
						Toast.makeText(mContext, "Title was set as \"" + mSCanvas.getTitle() + "\"", Toast.LENGTH_LONG).show();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();
		}
		break;
		case MENU_DATA_ATTACH_FILE_ADD:
		{
			Intent intent = new Intent(this, ToolListActivity.class);
			intent.putExtra(ToolListActivity.EXTRA_LIST_PATH, mTempAMSAttachFolderPath);
			intent.putExtra(ToolListActivity.EXTRA_SEARCH_ONLY_SAMM_FILE, false);
			startActivityForResult(intent, REQUEST_CODE_ATTACH_SELECT);
		}	
		break;
		case MENU_DATA_ATTACH_FILE_SHOW:
		{
			showAttachedFiles();
		}
		break;
		case MENU_DATA_EXTRA_DATA:
		{			
			//-------------------------------
			// layout setting
			//-------------------------------
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_text, null);
			TextView textTitle = (TextView)textEntryView.findViewById(R.id.textTitle);
			textTitle.setText("Enter Extra data string here");
			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Set Extra Data")
			.setView(textEntryView)
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText et = (EditText)textEntryView.findViewById(R.id.text);
					if(mSCanvas.putExtra(ToolFileTotalInfoShow.EXTRA_SCANVAS_PUTEXTRA, et.getText().toString())){
						String setExtra = mSCanvas.getStringExtra(ToolFileTotalInfoShow.EXTRA_SCANVAS_PUTEXTRA, null);
						Toast.makeText(mContext, "Success to put Extra data : " + setExtra , Toast.LENGTH_LONG).show();
					}	
					else
						Toast.makeText(mContext, "Fail to put Extra data.", Toast.LENGTH_LONG).show();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();	
		}
		break;

		case MENU_DATA_ADD_TAG:
		{			
			// set layout in dialog
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_text, null);
			TextView textTitle = (TextView)textEntryView.findViewById(R.id.textTitle);
			textTitle.setText("Enter TAG text here");

			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Add TAG")
			.setView(textEntryView)
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText et = (EditText)textEntryView.findViewById(R.id.text);
					if(mSCanvas.addTag(et.getText().toString())) {						
						String[] tagArray = mSCanvas.getTags();
						if(tagArray == null)
							return;

						StringBuffer res = new StringBuffer();
						for(String tag : tagArray){
							if(res.length()==0) 
								res.append(tag);
							else{
								res.append("; " + tag);							
							}
						}
						String strTags = res.toString();
						res.delete(0, res.length());				

						if(strTags!=null)
							Toast.makeText(mContext, "Current Tags = \"" + strTags + "\"", Toast.LENGTH_LONG).show();
						else
							Toast.makeText(mContext, "Current Tags is null", Toast.LENGTH_LONG).show();
					}
					else
						Toast.makeText(mContext, "Fail to add TAG.", Toast.LENGTH_LONG).show();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();	
		}
		break;
		case MENU_DATA_REMOVE_TAG:
		{
			// set layout in dialog
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_text, null);
			TextView textTitle = (TextView)textEntryView.findViewById(R.id.textTitle);
			textTitle.setText("Enter TAG text here");

			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Remove TAG")
			.setView(textEntryView)
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText et = (EditText)textEntryView.findViewById(R.id.text);
					if(mSCanvas.removeTag(et.getText().toString())) {						
						String[] tagArray = mSCanvas.getTags();
						if(tagArray == null) {
							Toast.makeText(mContext, "Current Tags = N/A", Toast.LENGTH_LONG).show();
							return;
						}

						StringBuffer res = new StringBuffer();
						for(String tag : tagArray){
							if(res.length()==0) 
								res.append(tag);
							else{
								res.append("; " + tag);							
							}
						}
						String strTags = res.toString();
						res.delete(0, res.length());	

						if(strTags!=null)
							Toast.makeText(mContext, "Current Tags = \"" + strTags + "\"", Toast.LENGTH_LONG).show();
						else
							Toast.makeText(mContext, "Current Tags = N/A", Toast.LENGTH_LONG).show();
					}
					else
						Toast.makeText(mContext, "Fail to remove TAG.", Toast.LENGTH_LONG).show();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();
		}
		break;
		case MENU_DATA_SET_PREFERENCE:
		{
			showSetPreferenceMenu();			
		}
		break;
		case MENU_DATA_SET_AUTHOR:
		{
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_author, null);
			TextView textTitleName = (TextView)textEntryView.findViewById(R.id.textTitleName);
			textTitleName.setText("Enter Author Name here");
			TextView textTitlePhone = (TextView)textEntryView.findViewById(R.id.textTitlePhone);
			textTitlePhone.setText("Enter Author Phone Number here");
			TextView textTitleEmail = (TextView)textEntryView.findViewById(R.id.textTitleEmail);
			textTitleEmail.setText("Enter Author E-mail here");
			TextView textTitleauthorImage = (TextView)textEntryView.findViewById(R.id.textTitleImage);
			textTitleauthorImage.setText("Enter Author Image");
			Button authorImage = (Button)textEntryView.findViewById(R.id.authorImage);				
			authorImage.setOnClickListener(inputAuthorImage);

			String name = mSCanvas.getAuthorName();
			if(name!=null){
				EditText et = (EditText)textEntryView.findViewById(R.id.textName);
				et.setText(name);
			}

			String phone = mSCanvas.getAuthorPhoneNum();
			if(phone!=null){
				EditText et = (EditText)textEntryView.findViewById(R.id.textPhone);
				et.setText(phone);
			}

			String email = mSCanvas.getAuthorEmail();
			if(email!=null){
				EditText et = (EditText)textEntryView.findViewById(R.id.textEmail);
				et.setText(email);
			}

			if(mAuthorImagePath == null){
				mAuthorImageView = (ImageView)textEntryView.findViewById(R.id.imagePath);
				mAuthorImageView.setImageBitmap(mSCanvas.getAuthorImage());
			}
			else{
				mAuthorImageView = (ImageView)textEntryView.findViewById(R.id.imagePath);
				mAuthorImageView.setImageBitmap(SPenSDKUtils.getSafeResizingBitmap(mAuthorImagePath, 100, 100, true));
			}

			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Set Author")
			.setView(textEntryView)
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {	
					EditText authorName = (EditText)textEntryView.findViewById(R.id.textName);
					String strAuthorName = authorName.getText().toString();
					EditText authorPhone = (EditText)textEntryView.findViewById(R.id.textPhone);
					String strAuthorPhone = authorPhone.getText().toString();
					EditText authorEmail = (EditText)textEntryView.findViewById(R.id.textEmail);
					String strAuthorEmail = authorEmail.getText().toString();

					Bitmap authorBitmap = null;
					if(mAuthorImageTempPath != null)
						authorBitmap = SPenSDKUtils.getSafeResizingBitmap(mAuthorImageTempPath, 100, 100, true);

					mSCanvas.setAuthor(strAuthorName, strAuthorPhone, strAuthorEmail, authorBitmap);

					mAuthorImageView = (ImageView)textEntryView.findViewById(R.id.imagePath);
					mAuthorImageView.setImageBitmap(mSCanvas.getAuthorImage());		
					mAuthorImagePath = mAuthorImageTempPath;//.toString();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();				
		}
		break;
		case MENU_DATA_SET_HYPERTEXT:
		{
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_hypertext, null);
			TextView textTitle = (TextView)textEntryView.findViewById(R.id.textTitle);
			textTitle.setText("Enter Hypertext here");

			String hyper = mSCanvas.getHypertext();
			if(hyper!=null){
				EditText et = (EditText)textEntryView.findViewById(R.id.text);
				et.setText(hyper);
			}

			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Set Hypertext")
			.setView(textEntryView)
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText et = (EditText)textEntryView.findViewById(R.id.text);
					String hyperText = et.getText().toString();
					mSCanvas.setHypertext(hyperText);
					Toast.makeText(mContext, "HyperText = "+ hyperText, Toast.LENGTH_SHORT).show();					
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();	
		}
		break;
		case MENU_DATA_SET_GEOTAG:
		{
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_geotag, null);

			TextView textTitleLatitude = (TextView)textEntryView.findViewById(R.id.textTitleLatitude);
			textTitleLatitude.setText("Enter Latitude here");						
			TextView textTitleLongitude = (TextView)textEntryView.findViewById(R.id.textTitleLongitude);
			textTitleLongitude.setText("Enter Longitude here");

			int geoLatitude = mSCanvas.getGeoTagLatitude();
			EditText latitudeEdit = (EditText)textEntryView.findViewById(R.id.textLatitude);
			latitudeEdit.setText(Integer.toString(geoLatitude));

			int geoLongitude = mSCanvas.getGeoTagLongitude();
			EditText longitudeEdit = (EditText)textEntryView.findViewById(R.id.textLongitude);
			longitudeEdit.setText(Integer.toString(geoLongitude));

			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Set GeoTag")
			.setView(textEntryView)
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText editLatitude = (EditText)textEntryView.findViewById(R.id.textLatitude);
					EditText editLongitude = (EditText)textEntryView.findViewById(R.id.textLongitude);					

					int latitude;
					int longitude;

					try {
						latitude = Integer.parseInt(editLatitude.getText().toString());
						longitude = Integer.parseInt(editLongitude.getText().toString());
					}catch(NumberFormatException e) {
						Toast.makeText(mContext, "Can not parse GeoTag Value.", Toast.LENGTH_LONG).show();
						return;
					}					
					mSCanvas.setGeoTag(latitude, longitude);										
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();	
		}
		break;


		//================================================
		// Background Data Menu
		//================================================
		case MENU_DATA_BACKGROUND_SET_PAGE_MEMO:
		{	
			String tempStr = null;
			SDataPageMemo txtMemo = mSCanvas.getPageMemo(0);
			if(txtMemo != null) {
				tempStr = txtMemo.getText();
			}			
			Intent intent = new Intent(this, ToolTextDialogInput.class);
			intent.putExtra(ToolTextDialogInput.TEXT_DIALOG_INPUT, tempStr);		
			startActivityForResult(intent, REQUEST_CODE_SET_PAGE_MEMO);
		}
		break;
		case MENU_DATA_BACKGROUND_SET_COLOR:
		{
			showColorSelectMenu();
		}
		break;
		case MENU_DATA_BACKGROUND_SET_IMAGE:
		{
			// call gallery
			callGalleryForInputImage(REQUEST_CODE_SELECT_IMAGE_BACKGROUND);
		}
		break;		
		case MENU_DATA_BACKGROUND_SET_IMAGE_EFFECT:
		{
			showBackgroundImageEffectSelectMenu();			
		}
		break;	
		case MENU_DATA_BACKGROUND_CLEAR:
		{
			// Setting Color
			int nSetColor = 0x00000000;
			mSCanvas.setBGColor(nSetColor);
		}
		break;	
		case MENU_DATA_BACKGROUND_SET_INITIAL_FG:
		{			
			callGalleryForInputImage(REQUEST_CODE_SELECT_IMAGE_FOREGROUND);
		}
		break;	
		case MENU_DATA_BACKGROUND_CLEAR_INITIAL_FG:
		{
			mSCanvas.setClearImageBitmap(null);
		}
		break;	
		case MENU_DATA_BACKGROUND_SET_VOICE_RECORDING:
		{
			//------------------------------------
			// Check Emulator 
			//------------------------------------
			if(Build.MODEL.compareToIgnoreCase("google_sdk") == 0 || android.os.Build.MODEL.compareToIgnoreCase("sdk")==0){
				// Toast.makeText(this, "Emulator does not support audio recording", Toast.LENGTH_LONG).show();
				//Confirm New
				AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
				// ad.setIcon(R.drawable.alert_dialog_icon);
				ad.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));	// Android Resource
				ad.setTitle(getResources().getString(R.string.app_name))
				.setMessage("Emulator may not support audio recording")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// finish dialog
						dialog.dismiss();		
						voiceRecording();
					}
				})
				.show();
			}
			else{
				voiceRecording();
			}
		}
		break;	
		case MENU_DATA_BACKGROUND_SET_AUDIO_FILE:
		{
			Intent intent = new Intent(this, ToolAudioListView.class);		
			startActivityForResult(intent, REQUEST_CODE_SET_BACKGROUND_AUDIO);
		}
		break;	
		case MENU_DATA_BACKGROUND_CLEAR_AUDIO:
		{
			mSCanvas.clearBGAudio();
		}
		break;	
		//================================================
		// Insert Menu
		//================================================
		case MENU_INSERT_STROKE:
		{
			// the pen stroke creation
			SObjectStroke sStrokeObject = new SObjectStroke();
			sStrokeObject.setColor(0xffff00ff);
			sStrokeObject.setStyle(SObjectStroke.SAMM_STROKE_STYLE_PENCIL);
			sStrokeObject.setSize(30);	

			int nPointNum = 100;
			PointF[] nPenStrokePoints = new PointF[nPointNum];	
			float[] nPenPressures = new float [nPointNum];			
			for(int i = 0; i < nPointNum; i++){        	
				nPenStrokePoints[i] = new PointF(); 				
				nPenStrokePoints[i].x = i*3;
				nPenStrokePoints[i].y = i*3;		
				nPenPressures[i] = 1.0f;
			}			

			sStrokeObject.setPoints(nPenStrokePoints);
			sStrokeObject.setPressures(nPenPressures);
			sStrokeObject.setMetaData(SObjectStroke.SAMM_METASTATE_PEN);

			mSCanvas.insertSAMMStroke(sStrokeObject);
			updateModeState();
		}
		break;
		case MENU_INSERT_TEXT:
		{
			// the pen stroke creation
			SObjectText sTextData = new SObjectText();
			sTextData.setColor(0xffff00ff);
			sTextData.setStyle(SObjectText.SAMM_TEXT_STYLE_BOLD);
			sTextData.setSize(10f);
			sTextData.setText("text insert test");

			RectF textRect = new RectF();
			textRect.set(100f, 300f, 300f, 400f);		
			sTextData.setRect(textRect);	

			// not selected
			mSCanvas.insertSAMMText(sTextData, false);
			updateModeState();
		}
		break;
		case MENU_INSERT_IMAGE:
		{
			// canvas option setting
			SOptionSCanvas canvasOption = mSCanvas.getOption();					
			if(canvasOption == null)
				return false;

			if(canvasOption.mSAMMOption == null)
				return false;

			canvasOption.mSAMMOption.setContentsQuality(PreferencesOfSAMMOption.getPreferenceSaveImageQuality(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);

			// the pen stroke creation
			int nContentsQualityOption = canvasOption.mSAMMOption.getContentsQuality();
			SObjectImage sImageObject = new SObjectImage(nContentsQualityOption);
			Bitmap imageBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);	

			// Set style : normal 
			sImageObject.setStyle(SObjectImage.SAMM_IMAGESTYLE_NORMAL);
			// Set image bitmap
			sImageObject.setImageBitmap(imageBitmap);

			// Set rect 
			RectF imageRect = new RectF();
			imageRect.set(20f, 300f, 120f, 400f);
			sImageObject.setRect(imageRect);

			// not selected
			mSCanvas.insertSAMMImage(sImageObject, false);			
			updateModeState();
		}
		break;
		case MENU_INSERT_FILLING:
		{
			// the pen stroke creation
			SObjectFilling sFillingObject = new SObjectFilling();
			sFillingObject.setColor(0xff00ffff);
			sFillingObject.setStyle(SObjectFilling.SAMM_FILLING_STYLE_COLOR);
			sFillingObject.setFillPoint(new PointF(100,100));

			// not selected
			mSCanvas.insertSAMMFilling(sFillingObject);			
			updateModeState();
		}
		break;
		case MENU_INSERT_BEAUTIFY_STROKE:
		{
			// the pen stroke creation
			SObjectStroke sStrokeObject = new SObjectStroke();
			sStrokeObject.setColor(0xffff00ff);
			sStrokeObject.setStyle(SObjectStroke.SAMM_STROKE_STYLE_BEAUTIFY);
			sStrokeObject.setSize(10);	

			int nPointNum = 100;
			int nStartX = 300;
			int nStartY = 500;
			PointF[] nPenStrokePoints = new PointF[nPointNum];	
			float[] nPenPressures = new float [nPointNum];			
			for(int i = 0; i < nPointNum; i++){        	
				nPenStrokePoints[i] = new PointF(); 				
				nPenStrokePoints[i].x = nStartX + i*3;
				nPenStrokePoints[i].y = nStartY + i*3;		
				nPenPressures[i] = 1.0f;
			}			

			sStrokeObject.setPoints(nPenStrokePoints);
			sStrokeObject.setPressures(nPenPressures);
			sStrokeObject.setMetaData(SObjectStroke.SAMM_METASTATE_PEN);

			sStrokeObject.setBeautifyLineFillStyleIndex(SObjectStroke.BEAUTIFY_LINE_STYLE_EMBOSS);
			sStrokeObject.setBeautifySlantIndex(SObjectStroke.BEAUTIFY_SLANT_DIR_RIGHT);
			sStrokeObject.setBeautifyStyleParamValue(SObjectStroke.BEAUTIFY_STYLE_PARAM_INDEX_CS, 3);
			sStrokeObject.setBeautifyStyleParamValue(SObjectStroke.BEAUTIFY_STYLE_PARAM_INDEX_BM, 4);
			sStrokeObject.setBeautifyStyleParamValue(SObjectStroke.BEAUTIFY_STYLE_PARAM_INDEX_QB, 7);
			sStrokeObject.setBeautifyStyleParamValue(SObjectStroke.BEAUTIFY_STYLE_PARAM_INDEX_KD, 10);
			sStrokeObject.setBeautifyStyleParamValue(SObjectStroke.BEAUTIFY_STYLE_PARAM_INDEX_YY, 30);
			sStrokeObject.setBeautifyStyleParamValue(SObjectStroke.BEAUTIFY_STYLE_PARAM_INDEX_FB, 20);
			sStrokeObject.setBeautifyStyleParamValue(SObjectStroke.BEAUTIFY_STYLE_PARAM_INDEX_STR, SObjectStroke.BEAUTIFY_SLANT_DIR_RIGHT);
			sStrokeObject.setBeautifyID(SObjectStroke.BEAUTIFY_STYLE_ID_HUANG);

			mSCanvas.insertSAMMStroke(sStrokeObject);
			updateModeState();
		}
		break;

		//================================================
		// More Menu
		//================================================
		case MENU_MORE_APP_ID:
		{			
			//-------------------------------
			// layout setting
			//-------------------------------
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_app_id, null);
			TextView appID = (TextView)textEntryView.findViewById(R.id.appid);
			appID.setText("Enter APP ID here");
			TextView majorVersion = (TextView)textEntryView.findViewById(R.id.superversion);
			majorVersion.setText("Enter Major Version here");
			TextView minorVersion = (TextView)textEntryView.findViewById(R.id.subversion);
			minorVersion.setText("Enter Minor Version here");
			TextView patchVersion = (TextView)textEntryView.findViewById(R.id.patchversion);
			patchVersion.setText("Enter Patch Version here");

			// Setting 
			String curAppIDName = mSCanvas.getAppIDName();				
			int curAppIDVerMajor = mSCanvas.getAppIDVerMajor();
			int curAppIDVerMinor = mSCanvas.getAppIDVerMinor();			
			String curAppIDVerPatch = mSCanvas.getAppIDVerPatchName();			
			EditText appid_edit = (EditText)textEntryView.findViewById(R.id.appid_edit);
			if(appid_edit!=null) appid_edit.setText(curAppIDName);
			EditText majorVersion_edit = (EditText)textEntryView.findViewById(R.id.superversion_edit);
			if(majorVersion_edit!=null) majorVersion_edit.setText(Integer.toString(curAppIDVerMajor));
			EditText minorVersion_edit = (EditText)textEntryView.findViewById(R.id.subversion_edit);
			if(minorVersion_edit!=null) minorVersion_edit.setText(Integer.toString(curAppIDVerMinor));
			EditText patchVersion_edit = (EditText)textEntryView.findViewById(R.id.patchversion_edit);
			if(patchVersion_edit!=null) patchVersion_edit.setText(curAppIDVerPatch);

			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Set Application ID")
			.setView(textEntryView)
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText appid_edit = (EditText)textEntryView.findViewById(R.id.appid_edit);
					EditText majorVersion_edit = (EditText)textEntryView.findViewById(R.id.superversion_edit);
					EditText minorVersion_edit = (EditText)textEntryView.findViewById(R.id.subversion_edit);
					EditText patchVersion_edit = (EditText)textEntryView.findViewById(R.id.patchversion_edit);

					// Update Application Identifier
					APPLICATION_ID_NAME = appid_edit.getText().toString();				
					try {
						APPLICATION_ID_VERSION_MAJOR = Integer.parseInt(majorVersion_edit.getText().toString());
						APPLICATION_ID_VERSION_MINOR = Integer.parseInt(minorVersion_edit.getText().toString());
					}catch(NumberFormatException e) {
						Toast.makeText(mContext, "Can not parse Application Version.", Toast.LENGTH_LONG).show();
						return;
					}
					APPLICATION_ID_VERSION_PATCHNAME =  patchVersion_edit.getText().toString();
					// Set Application Identifier
					if(!mSCanvas.setAppID(APPLICATION_ID_NAME, APPLICATION_ID_VERSION_MAJOR, APPLICATION_ID_VERSION_MINOR,APPLICATION_ID_VERSION_PATCHNAME)) {
						Toast.makeText(mContext, "Fail to set Application ID.", Toast.LENGTH_LONG).show();
						return;
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();	
		}
		break;
		case MENU_MORE_SHOW_TOTAL_INFO:
		{
			String strFileName = "tmpSAMMFile" + DEFAULT_FILE_EXT;
			// canvas option setting		
			SOptionSCanvas canvasOption = new SOptionSCanvas();					
			// compact size for fast save
			canvasOption.mSAMMOption.setSaveImageSize(SOptionSAMM.SAMM_SAVE_OPTION_COMPACT_SIZE);
			// valid only to save jpg
			// canvasOption.mSAMMOption.setJPGImageQuality(100);

			// Minimum Quality for fast save
			canvasOption.mSAMMOption.setContentsQuality(SOptionSAMM.SAMM_CONTENTS_QUALITY_MINIMUM);
			// save with background setting 
			canvasOption.mSAMMOption.setSaveOnlyForegroundImage(PreferencesOfSAMMOption.getPreferenceSaveOnlyForegroundImage(mContext));
			// Create new image file to save
			canvasOption.mSAMMOption.setCreateNewImageFile(PreferencesOfSAMMOption.getPreferenceSaveCreateNewImageFile(mContext));
			canvasOption.mSAMMOption.setEncodeForegroundImage(false);
			canvasOption.mSAMMOption.setEncodeThumbnailImage(false);
			canvasOption.mSAMMOption.setEncodeObjectData(true);
			canvasOption.mSAMMOption.setEncodeVideoFileDataOption(false);

			// option setting
			mSCanvas.setOption(canvasOption);	

			if(!saveSAMMFile(mTempAMSFolderPath + "/" + strFileName, false))
				return false;
			Intent intent = new Intent(this, ToolFileTotalInfoShow.class);
			intent.putExtra(ToolFileTotalInfoShow.EXTRA_SAMM_FILE_INFO, mTempAMSFolderPath + "/" + strFileName);
			intent.putExtra(ToolFileTotalInfoShow.EXTRA_SCANVAS_WIDTH, mSCanvas.getWidth());
			intent.putExtra(ToolFileTotalInfoShow.EXTRA_SCANVAS_HEIGHT, mSCanvas.getHeight());
			startActivityForResult(intent, REQUEST_CODE_TOTAL_INFO_SHOW);
		}	
		break;
		case MENU_MORE_SAMM_OPTION_SETTING:
		{
			Intent intent = new Intent(this, PreferencesOfSAMMOption.class);				
			startActivity(intent);			
		}
		break;
		case MENU_MORE_ANIMATION_OPTION_SETTING:
		{
			Intent intent = new Intent(this, PreferencesOfAnimationOption.class);				
			startActivity(intent);			
		}
		break;	
		case MENU_MORE_OTHER_OPTION:
		{
			Intent intent = new Intent(this, PreferencesOfOtherOption.class);
			startActivityForResult(intent, REQUEST_CODE_OTHER_OPTION);	
		}
		break;
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
		case MENU_EDIT_OBJECT_DEPTH_CHANGE:
		{
			applyDepthChange();
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

	private void checkSameSaveFileName(final String saveFileName) {	

		File fSaveFile = new File(saveFileName);
		if(fSaveFile.exists())
		{
			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Same file name exists! Overwrite?")		
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {				
					// canvas option setting
					SOptionSCanvas canvasOption = new SOptionSCanvas();
					// medium size : to reduce saving time 
					canvasOption.mSAMMOption.setSaveImageSize(PreferencesOfSAMMOption.getPreferenceSaveImageSize(mContext));
					// canvasOption.mSaveOption.setSaveImageSize(SOptionSAMM.SAMM_SAVE_OPTION_MEDIUM_SIZE);
					// valid only to save jpg
					// canvasOption.mSAMMOption.setJPGImageQuality(100);
					// Cropping option 
					canvasOption.mSAMMOption.setSaveImageLeftCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageHorizontalCrop(mContext));
					canvasOption.mSAMMOption.setSaveImageRightCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageHorizontalCrop(mContext));
					canvasOption.mSAMMOption.setSaveImageTopCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageVerticalCrop(mContext));
					canvasOption.mSAMMOption.setSaveImageBottomCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageVerticalCrop(mContext));
					canvasOption.mSAMMOption.setSaveContentsCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveContentsCrop(mContext));
					// content quality minimum 
					canvasOption.mSAMMOption.setContentsQuality(PreferencesOfSAMMOption.getPreferenceSaveImageQuality(mContext));
					// canvasOption.mSAMMOption.setContentsQuality(SOptionSAMM.SAMM_CONTENTS_QUALITY_MINIMUM);
					// with background(image, color) set
					canvasOption.mSAMMOption.setSaveOnlyForegroundImage(PreferencesOfSAMMOption.getPreferenceSaveOnlyForegroundImage(mContext));
					// canvasOption.mSAMMOption.setSaveOnlyForegroundImage(false);	// with background(image, color) set 
					// canvasOption.mSAMMOption.setSaveOnlyForegroundImage(true);	// no background
					// Create new image file to save
					canvasOption.mSAMMOption.setCreateNewImageFile(PreferencesOfSAMMOption.getPreferenceSaveCreateNewImageFile(mContext));
					canvasOption.mSAMMOption.setEncodeForegroundImage(PreferencesOfSAMMOption.getPreferenceEncodeForegroundImageFile(mContext));
					canvasOption.mSAMMOption.setEncodeThumbnailImage(PreferencesOfSAMMOption.getPreferenceEncodeThumbnailImageFile(mContext));
					canvasOption.mSAMMOption.setEncodeObjectData(PreferencesOfSAMMOption.getPreferenceEncodeObjectDataFile(mContext));
					canvasOption.mSAMMOption.setEncodeVideoFileDataOption(PreferencesOfSAMMOption.getPreferenceEncodeVideoFileData(mContext));
					// option setting
					mSCanvas.setOption(canvasOption);					
					saveSAMMFile(saveFileName, true);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.show();
		}
		else {
			// canvas option setting
			SOptionSCanvas canvasOption = new SOptionSCanvas();
			// Cropping option 
			canvasOption.mSAMMOption.setSaveImageLeftCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageHorizontalCrop(mContext));
			canvasOption.mSAMMOption.setSaveImageRightCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageHorizontalCrop(mContext));
			canvasOption.mSAMMOption.setSaveImageTopCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageVerticalCrop(mContext));
			canvasOption.mSAMMOption.setSaveImageBottomCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageVerticalCrop(mContext));
			canvasOption.mSAMMOption.setSaveContentsCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveContentsCrop(mContext));
			// medium size : to reduce saving time 
			canvasOption.mSAMMOption.setSaveImageSize(PreferencesOfSAMMOption.getPreferenceSaveImageSize(mContext));
			// canvasOption.mSAMMOption.setSaveImageSize(SOptionSAMM.SAMM_SAVE_OPTION_MEDIUM_SIZE);
			// valid only to save jpg
			// canvasOption.mSAMMOption.setJPGImageQuality(100);
			// content quality minimum 
			canvasOption.mSAMMOption.setContentsQuality(PreferencesOfSAMMOption.getPreferenceSaveImageQuality(mContext));
			// canvasOption.mSAMMOption.setContentsQuality(SOptionSAMM.SAMM_CONTENTS_QUALITY_MINIMUM);
			// save with background setting
			canvasOption.mSAMMOption.setSaveOnlyForegroundImage(PreferencesOfSAMMOption.getPreferenceSaveOnlyForegroundImage(mContext));	// with background(image, color) set
			// canvasOption.mSAMMOption.setSaveOnlyForegroundImage(false);	// with background(image, color) set 
			// canvasOption.mSAMMOption.setSaveOnlyForegroundImage(true);	// no background
			canvasOption.mSAMMOption.setCreateNewImageFile(PreferencesOfSAMMOption.getPreferenceSaveCreateNewImageFile(mContext));	// with background(image, color) set
			canvasOption.mSAMMOption.setEncodeForegroundImage(PreferencesOfSAMMOption.getPreferenceEncodeForegroundImageFile(mContext));
			canvasOption.mSAMMOption.setEncodeThumbnailImage(PreferencesOfSAMMOption.getPreferenceEncodeThumbnailImageFile(mContext));
			canvasOption.mSAMMOption.setEncodeObjectData(PreferencesOfSAMMOption.getPreferenceEncodeObjectDataFile(mContext));
			canvasOption.mSAMMOption.setDecodePriorityFGData(PreferencesOfSAMMOption.getPreferenceDecodePriorityFGData(mContext));
			canvasOption.mSAMMOption.setEncodeVideoFileDataOption(PreferencesOfSAMMOption.getPreferenceEncodeVideoFileData(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);					
			saveSAMMFile(saveFileName, true);			
		}
	}

	// Side Button during hover
	private void doHoverButtonUp(int nEventPositionX, int nEventPositionY){

		//--------------------------------------------------------------
		// Close setting view if the setting view is visible
		//--------------------------------------------------------------
		if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN)){
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
			return;
		}
		else if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER)){
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
			return;
		}
		else if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT)){
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);
			return;
		}
		else if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING)){
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, false);
			return;
		}


		//--------------------------------------------------------------
		// Show popup menu if the object is selected or if sobject exist in clipboard
		//--------------------------------------------------------------
		if(mSCanvas.isSObjectSelected() || mSCanvas.isClipboardSObjectListExist()){
			showObjectPopUpMenu(nEventPositionX, nEventPositionY);
			return;
		}

		//--------------------------------------------------------------
		// Show Setting view
		//--------------------------------------------------------------
		if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);			
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);
		}
		else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, true);
		}
		else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);			
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, true);
		}
		else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);			
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, true);
		}
	}


	private void showObjectPopUpMenu(int nEventPositionX, int nEventPositionY){

		int nSelectObjectType = mSCanvas.getSelectedSObjectType();
		final boolean bClipboardObjectExist = mSCanvas.isClipboardSObjectListExist();
		int nMenuArray;
		final int xPos = nEventPositionX;
		final int yPos = nEventPositionY;

		if(nSelectObjectType==SObject.SOBJECT_LIST_TYPE_IMAGE){

			if(bClipboardObjectExist) nMenuArray = R.array.popup_menu_image_with_paste;
			else nMenuArray = R.array.popup_menu_image;

			new AlertDialog.Builder(this)
			.setTitle("Select Image Menu")
			.setItems(nMenuArray, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Delete Image
					if(which==0){
						deleteSelectedSObject();
					}
					// Rotate Image
					else if(which==1){
						rotateSelectedObject();
					}
					// Copy Image
					else if(which==2){
						copySelectedObject();
					}
					// Cut Image
					else if(which==3){
						cutSelectedObject();
					}
					// Paste object in clipboard
					else if(which==4){
						pasteClipboardObject(xPos, yPos);
					}
					// Clear object in clipboard
					else if(which==5){
						clearClipboardObject();
					}
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		}
		else if(nSelectObjectType==SObject.SOBJECT_LIST_TYPE_TEXT){

			if(bClipboardObjectExist) nMenuArray = R.array.popup_menu_text_with_paste;
			else nMenuArray = R.array.popup_menu_text;

			new AlertDialog.Builder(this)
			.setTitle("Select Text Menu")
			.setItems(nMenuArray, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Delete Text
					if(which==0){
						deleteSelectedSObject();
					}
					// Copy Text
					else if(which==1){
						copySelectedObject();
					}
					// Cut Text
					else if(which==2){
						cutSelectedObject();
					}
					// Paste object in clipboard
					else if(which==3){
						pasteClipboardObject(xPos, yPos);
					}
					// Clear object in clipboard
					else if(which==4){
						clearClipboardObject();
					}
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		}
		else {

			if(bClipboardObjectExist){
				nMenuArray = R.array.popup_menu_else_with_paste;

				new AlertDialog.Builder(this)
				.setTitle("Select Pop-Up Menu")
				.setItems(nMenuArray, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Paste object in clipboard
						if(which==0){
							pasteClipboardObject(xPos, yPos);
						}
						// Clear object in clipboard
						else if(which==1){
							clearClipboardObject();
						}
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();
			}
		}
	}

	private void showColorSelectMenu(){
		new AlertDialog.Builder(this)
		.setTitle("Select Color")
		.setItems(R.array.ams_strarr_color, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int nSetColor = 0;
				switch(which) {
				case 0:	// Transparent
					nSetColor = 0x00000000;
					break;
				case 1:	// White
					nSetColor = 0xFFFFFFFF;
					break;
				case 2:	// Black
					nSetColor = 0xFF000000;
					break;
				case 3: // Custom
				{
					// set background color
					int nCurBackgroundColor = mSCanvas.getBGColor();
					mBGColorPickerListener = new OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							mSCanvas.setBGColor(color);
						}
					};
					new ToolColorPickerDialog(mContext, mBGColorPickerListener, nCurBackgroundColor).show();
					return;
				}				
				default:	
					break;
				}

				// Setting Color
				mSCanvas.setBGColor(nSetColor);
			}
		})
		.show();
	}

	// Conversion array to map menu index to Image Filter Index
	private int[] imageOperationByIndex = {SPenImageFilterConstants.FILTER_GRAY, SPenImageFilterConstants.FILTER_SEPIA,	SPenImageFilterConstants.FILTER_NEGATIVE, 
			SPenImageFilterConstants.FILTER_BRIGHT, SPenImageFilterConstants.FILTER_DARK, SPenImageFilterConstants.FILTER_VINTAGE, SPenImageFilterConstants.FILTER_OLDPHOTO, 
			SPenImageFilterConstants.FILTER_FADEDCOLOR, SPenImageFilterConstants.FILTER_VIGNETTE, SPenImageFilterConstants.FILTER_VIVID, SPenImageFilterConstants.FILTER_COLORIZE, 
			SPenImageFilterConstants.FILTER_BLUR, SPenImageFilterConstants.FILTER_PENCILSKETCH, SPenImageFilterConstants.FILTER_FUSAIN, SPenImageFilterConstants.FILTER_PENSKETCH, 
			SPenImageFilterConstants.FILTER_PASTELSKETCH, SPenImageFilterConstants.FILTER_COLORSKETCH, SPenImageFilterConstants.FILTER_PENCILPASTELSKETCH, SPenImageFilterConstants.FILTER_PENCILCOLORSKETCH, 
			SPenImageFilterConstants.FILTER_RETRO, SPenImageFilterConstants.FILTER_SUNSHINE, SPenImageFilterConstants.FILTER_DOWNLIGHT, SPenImageFilterConstants.FILTER_BLUEWASH,
			SPenImageFilterConstants.FILTER_NOSTALGIA, SPenImageFilterConstants.FILTER_YELLOWGLOW, SPenImageFilterConstants.FILTER_SOFTGLOW, SPenImageFilterConstants.FILTER_MOSAIC, 
			SPenImageFilterConstants.FILTER_POPART, SPenImageFilterConstants.FILTER_MAGICPEN, SPenImageFilterConstants.FILTER_OILPAINT, SPenImageFilterConstants.FILTER_POSTERIZE, 
			SPenImageFilterConstants.FILTER_CARTOONIZE,SPenImageFilterConstants.FILTER_CLASSIC};

	private int imageOperationLevel =  SPenImageFilterConstants.FILTER_LEVEL_MEDIUM;

	private void showBackgroundImageEffectSelectMenu(){		
		new AlertDialog.Builder(this)
		.setTitle("Select Background Image Effect")
		.setItems(R.array.backgroundimageoperation, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {			
				if(!mSCanvas.filterBGImage(imageOperationByIndex[which], imageOperationLevel))
					Toast.makeText(SPen_Example_SAMMEditor.this, "To apply backGround image effect, set background image firstly or select valid image effect!!!", Toast.LENGTH_LONG).show();
			}
		})
		.show();
	}

	private void inputQuestion(){
		new AlertDialog.Builder(this)
		.setTitle("Warning!!")
		.setMessage("Input Correct Rotation Angle(0~359)")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		})

		.show();	
	}




	//==================================================
	// Class for the item of attached file. 
	//==================================================
	public static class AlertItem{
		private String text;     
		private Drawable icon;  

		public AlertItem() {}
		public AlertItem(String text, Drawable icon) { 
			setItem(text,icon);     
		}
		public void setItem(String text, Drawable icon) { 
			this.text = text;         
			this.icon = icon;     
		}
	}


	AlertDialog.Builder mAlertBuilder = null;
	AlertItem [] mAlertItems = null;

	private void showAttachedFiles(){    	    	
		// Make Attach File String
		int nAttachFileNum = mSCanvas.getAttachedFileNum();
		if(nAttachFileNum<=0) {
			Toast.makeText(mContext, "There is no attach file", Toast.LENGTH_SHORT).show();
			return;
		}

		// Make Items for the alert dialog
		mAlertItems = new AlertItem[nAttachFileNum];
		for(int i=0; i<nAttachFileNum; i++){
			SDataAttachFile attachData = mSCanvas.getAttachedFileData(i);
			if(attachData == null) {
				Toast.makeText(mContext, "Attach Data(" + i + ") is invalid", Toast.LENGTH_SHORT).show();
				return;
			}
			String strPath = attachData.getFilePath();
			String strDescription = attachData.getFileDescription();
			String strInfo = "["+(i+1)+"] " + strDescription+"\n"+strPath;
			mAlertItems[i] = new AlertItem();
			mAlertItems[i].text = strInfo;
			mAlertItems[i].icon = new BitmapDrawable(getResources(), attachData.getFileIconBitmap());	
			if(mAlertItems[i].icon==null){
				mAlertItems[i].icon = getResources().getDrawable(R.drawable.list_icon_attach);
			}
		}

		mAlertBuilder = new AlertDialog.Builder(this);
		mAlertBuilder.setTitle("Attached Files");
		mAlertBuilder.setIcon(getResources().getDrawable(R.drawable.list_icon_attach));

		// Attached file AlertDialog
		ListAdapter adapter = new ArrayAdapter<AlertItem>(
				mContext,     
				android.R.layout.select_dialog_item,     
				android.R.id.text1,     
				mAlertItems){  

			public View getView(int position, View convertView, ViewGroup parent){
				// User super class to create the View
				View v = super.getView(position, convertView, parent);    	

				TextView tv = (TextView)v.findViewById(android.R.id.text1);
				if(mAlertItems[position]!=null)
				{
					// Set Text
					tv.setText(mAlertItems[position].text);
					// Put the image on the TextView
					tv.setCompoundDrawablesWithIntrinsicBounds(mAlertItems[position].icon, null, null, null);
				}
				//Add margin between image and text (support various screen densities)
				int dp10 = (int) (10 * mContext.getResources().getDisplayMetrics().density + 0.5f);            
				tv.setCompoundDrawablePadding(dp10);              
				return v;					
			}
		}; 


		mAlertBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {        
			public void onClick(DialogInterface dialog, int which) {

				final int nAttachIndex = which;
				String [] attachFileAction = {"View Attached File", "Detach File" };
				new AlertDialog.Builder(mContext)
				.setTitle("Attach File")
				.setItems(attachFileAction, new DialogInterface.OnClickListener() {			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SDataAttachFile attachSelected = mSCanvas.getAttachedFileData(nAttachIndex);
						if(attachSelected==null){
							Toast.makeText(mContext, "Fail to get attached file data", Toast.LENGTH_LONG).show();
							return;
						}
						String strPath = attachSelected.getFilePath();
						String strDescription = attachSelected.getFileDescription();

						// View attached file
						if(which==0){
							String strInfo = strDescription+"\n"+strPath;
							Toast.makeText(mContext, "View attached file : " + strInfo, Toast.LENGTH_LONG).show();							
							if(!attachSelected.viewAttachedFile(mContext, mTempAMSAttachFolderPath)){
								Toast.makeText(mContext, "View attached file fail", Toast.LENGTH_LONG).show();
							}
						}
						// Detach file
						else{
							if(!mSCanvas.detachFile(nAttachIndex)){
								Toast.makeText(mContext, "Fail to detach the selected file.", Toast.LENGTH_LONG).show();
							}
							else{
								Toast.makeText(mContext, "Selected file was detached.", Toast.LENGTH_SHORT).show();
							}
						}
						dialog.dismiss();
					}
				})
				.show();
			}     
		});

		// Create Alert Dialog and show
		AlertDialog mAlertDialog = mAlertBuilder.create();		
		mAlertDialog.show();
	}



	private void showSetPreferenceMenu(){	    
		mPreferenceCheckedItem = mSCanvas.getCheckPreference();
		AlertDialog dlg = new AlertDialog.Builder(this)		
		.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert))	// Android Resource
		.setTitle("Set Preference")
		.setSingleChoiceItems(R.array.set_preference, mPreferenceCheckedItem, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				/* User clicked on a radio button do some stuff */
				mPreferenceCheckedItem = whichButton;
			}
		})
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				/* User clicked Yes so do some stuff */
				if(!mSCanvas.setCheckPreference(mPreferenceCheckedItem))
					Toast.makeText(SPen_Example_SAMMEditor.this, "Fail to set preference normal!", Toast.LENGTH_LONG).show();
				else
					if(mPreferenceCheckedItem == 0)
						Toast.makeText(SPen_Example_SAMMEditor.this, "Current Preference = Normal", Toast.LENGTH_LONG).show();
					else if(mPreferenceCheckedItem == 1)
						Toast.makeText(SPen_Example_SAMMEditor.this, "Current Preference = Favorite", Toast.LENGTH_LONG).show();
					else if(mPreferenceCheckedItem == 2)
						Toast.makeText(SPen_Example_SAMMEditor.this, "Current Preference = Custom", Toast.LENGTH_LONG).show();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				/* User clicked No so do some stuff */
			}
		})
		.create();
		dlg.show();
	}


	private void applyDepthChange(){
		new AlertDialog.Builder(this)
		.setTitle("Depth Change Operaction")
		.setItems(R.array.object_depth_change_operation, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				switch (which) {
				case OBJECT_DEPTH_CHANGE_FORWARD:			
					mSCanvas.bringObjectsForward();
					break;
				case OBJECT_DEPTH_CHANGE_BACKWARD:			
					mSCanvas.sendObjectsBackward();
					break;
				case OBJECT_DEPTH_CHANGE_FRONT:				
					mSCanvas.bringObjectsFront();
					break;
				case OBJECT_DEPTH_CHANGE_BACK:			
					mSCanvas.sendObjectsBack();
					break;				
				}				
				dialog.dismiss();
			}
		})
		.show();
	}


	private void applyOtherOption(){
		int hoverPointerShowOption = PreferencesOfOtherOption.getPreferenceHoverPointerShowOption(mContext);
		switch(hoverPointerShowOption){
		case HOVER_SHOW_ALWAYS_ONHOVER:
			mSCanvas.setSCanvasHoverPointerShowOption(SCanvasConstants.SCANVAS_HOVERPOINTER_SHOW_OPTION_ALWAYS_ON_HOVER);
			break;
		case HOVER_SHOW_ONCE_ONHOVER:
			mSCanvas.setSCanvasHoverPointerShowOption(SCanvasConstants.SCANVAS_HOVERPOINTER_SHOW_OPTION_ONCE_ON_HOVER);
			break;	
		}		

		int hoverPointerStyle = PreferencesOfOtherOption.getPreferenceHoverPointerStyle(mContext);
		switch (hoverPointerStyle) {
		case HOVER_POINTER_DEFAULT:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_NONE);
			break;
		case HOVER_POINTER_SIMPLE_ICON:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SIMPLE_CUSTOM);
			mSCanvas.setSCanvasHoverPointerSimpleIcon(SPenEventLibrary.HOVERING_SPENICON_MOVE);			
			break;
		case HOVER_POINTER_SIMPLE_DRAWABLE:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SIMPLE_CUSTOM);
			mSCanvas.setSCanvasHoverPointerSimpleDrawable(getResources().getDrawable(R.drawable.tool_ic_pen));
			break;
		case HOVER_POINTER_SPEN:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SPENSDK);
			break;
		case HOVER_POINTER_SNOTE:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SNOTE);
			break;				
		}

		mSideButtonStyle = PreferencesOfOtherOption.getPreferencePenSideButtonStyle(mContext);			
		mSCanvas.setKeyboardPredictiveTextDisable(!PreferencesOfOtherOption.getPreferencePredictiveText(mContext));		
		mSCanvas.setSettingViewPinUpState(PreferencesOfOtherOption.getPreferenceSettingviewPinup(mContext));
		mSCanvas.setFingerControlPenDrawing(PreferencesOfOtherOption.getPreferencePenOnlyMode(mContext));
		mSCanvas.setStrokeLongClickSelectOption(PreferencesOfOtherOption.getPreferenceStrokeLongclick(mContext));
		mSCanvas.setTextLongClickSelectOption(PreferencesOfOtherOption.getPreferenceTextLongclick(mContext));
		mSCanvas.setEnableHoverScroll(PreferencesOfOtherOption.getPreferenceHoverScroll(mContext));
		mSCanvas.maintainScaleOnResize(PreferencesOfOtherOption.getPreferenceMaintainScaleOnResize(mContext));
		mSCanvas.maintainSettingPenColor(PreferencesOfOtherOption.getPreferenceMaintainPenColor(mContext));
		mSCanvas.supportBeautifyStrokeSetting(PreferencesOfOtherOption.getPreferenceSupportBeautifyStrokeSetting(mContext));
		mSCanvas.setEnableBoundaryTouchScroll(PreferencesOfOtherOption.getPreferenceBoundaryTouchScroll(mContext));
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
