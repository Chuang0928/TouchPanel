package com.samsung.spensdk.example.hoverpointer;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.samm.common.SObjectStroke;
import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spen.settings.SettingFillingInfo;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spen.settings.SettingTextInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.ColorPickerColorChangeListener;
import com.samsung.spensdk.applistener.CustomHoverPointerSettingListener;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;


public class SPen_Example_HoverPointer extends Activity {

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
	// Menu
	//==============================
	private final int MENU_HOVER_STYLE_GROUP = 1000;
	private final int MENU_HOVER_SHOW_GROUP = 1001;
	private final int MENU_SIDEBUTTON_GROUP = 1002;

	private final int HOVER_DEFAULT = 0;
	private final int HOVER_SIMPLE_ICON = 1;
	private final int HOVER_SIMPLE_DRAWABLE = 2;
	private final int HOVER_SPENSDK = 3;
	private final int HOVER_SNOTE = 4;

	private final int HOVER_SHOW_ALWAYS_ONHOVER = 0;
	private final int HOVER_SHOW_ONCE_ONHOVER = 1;

	private final int SIDEBUTTON_CHANGE_PEN= 0;
	private final int SIDEBUTTON_SHOW_SETTING = 1;	


	private int mHoverButtonAction = 0; // 0:Change Setting :Show SettingView
	private int mHoverPointerStyle = HOVER_SNOTE;
	private int mHoverPointerShowOption = HOVER_SHOW_ONCE_ONHOVER;
	private int mSideButtonStyle = SIDEBUTTON_CHANGE_PEN;

	//==============================
	// Variables
	//==============================
	Context mContext = null;

	private FrameLayout		mLayoutContainer;
	private RelativeLayout	mCanvasContainer;
	private SCanvasView		mSCanvas;

	private ImageView		mMoveBtn;
	private ImageView		mPenBtn;
	private ImageView		mEraserBtn;
	private ImageView		mTextBtn;
	private ImageView		mFillingBtn;
	private ImageView		mColorPickerBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.editor_hoverpointer);

		mContext = this;

		//------------------------------------
		// UI Setting
		//------------------------------------
		mMoveBtn = (ImageView) findViewById(R.id.moveBtn);
		mMoveBtn.setOnClickListener(moveBtnClickListener);

		mPenBtn = (ImageView) findViewById(R.id.penBtn);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);
		mTextBtn = (ImageView) findViewById(R.id.textBtn);
		mTextBtn.setOnClickListener(mBtnClickListener);
		mFillingBtn = (ImageView) findViewById(R.id.fillingBtn);
		mFillingBtn.setOnClickListener(mBtnClickListener);
		mColorPickerBtn = (ImageView) findViewById(R.id.colorPickerBtn);
		mColorPickerBtn.setOnClickListener(mColorPickerListener);

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

				// Set Initial Setting View Size
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);

				// Set Pen Only Mode with Finger Control
				mSCanvas.setFingerControlPenDrawing(true);

				// Set Initial Pen setting
				SettingStrokeInfo settingInfo = mSCanvas.getSettingViewStrokeInfo();
				if(settingInfo!=null) {
					settingInfo.setStrokeWidth(20);
					settingInfo.setStrokeColor(Color.RED);		
					mSCanvas.setSettingViewStrokeInfo(settingInfo);	
				}
				Toast toast = Toast.makeText(mContext, "Set the initial color of the pen as RED to show red hover pointer", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				// Set Hover pointer as S Pen SDK Style
				// mHoverPointerStyle = HOVER_SPENSDK;
				// mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SPENSDK);				
				// Set Hover pointer as S Note Style
				mHoverPointerStyle = HOVER_SNOTE;
				mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SNOTE);

				// Set Hover pointer show option as "always" 
				// mHoverPointerShowOption = HOVER_SHOW_ALWAYS_ONHOVER;
				// mSCanvas.setSCanvasHoverPointerShowOption(SCanvasConstants.SCANVAS_HOVERPOINTER_SHOW_OPTION_ALWAYS_ON_HOVER);
				// Set Hover pointer show option as "once"
				mHoverPointerShowOption = HOVER_SHOW_ONCE_ONHOVER;
				mSCanvas.setSCanvasHoverPointerShowOption(SCanvasConstants.SCANVAS_HOVERPOINTER_SHOW_OPTION_ONCE_ON_HOVER);

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
		// Custom Hover Pointer Listener 
		//------------------------------------------------
		// You can change each hover pointer as your taste.
		mSCanvas.setCustomHoverPointerListener(new CustomHoverPointerSettingListener() {

			@Override
			public Drawable onHoverPointerForStroke(SettingStrokeInfo strokeInfo, Drawable defaultDrawable) {
				return defaultDrawable;
			}

			@Override
			public Drawable onHoverPointerForText(SettingTextInfo textInfo, Drawable defaultDrawable) {
				return defaultDrawable;
			}

			@Override
			public Drawable onHoverPointerForFilling(SettingFillingInfo fillingInfo, Drawable defaultDrawable) {
				return defaultDrawable;
			}

			@Override
			public Drawable onHoverPointerForPicker(Drawable defaultDrawable) {
				return defaultDrawable;
			}

			@Override
			public Drawable onHoverPointerDefault(Drawable defaultDrawable) {
				return defaultDrawable;
			}

			@Override
			public Drawable onHoverPointerForMove(Drawable defaultDrawable) {
				return defaultDrawable;
			}
		});

		//--------------------------------------------
		// [Hover Listener & Custom Hover Icon]
		// Set S pen HoverListener & Custom Hover Icon
		//--------------------------------------------
		mSCanvas.setSPenHoverListener(new SPenHoverListener(){

			boolean isPenButtonDown = false;
			@Override
			public boolean onHover(View view, MotionEvent event) {				
				return false;
			}

			@Override
			public void onHoverButtonDown(View view, MotionEvent event) {
				//Log.e(TAG, "HOVER_TEST(UI): Down" );
				isPenButtonDown= true;
			}

			@Override
			public void onHoverButtonUp(View view, MotionEvent event) {
				//Log.e(TAG, "HOVER_TEST(UI): UP" );
				if(isPenButtonDown==false)	// ignore button up event if button was not pressed on hover
					return;
				isPenButtonDown = false;

				// Change Setting
				if(mHoverButtonAction==0){
					// S Note Guide : Do not show current setting
					boolean bIncludeDefinedSetting = (mSCanvas.getSettingViewPresetNum()>0 ? false: true);
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
									mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);							
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
									mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);							
									mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);
								}
								updateModeState();
							}							
						}						
					}
				}
				// Show SettingView(Toggle SettingView)
				else if(mHoverButtonAction==1){
					if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
						mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
					}
					else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
						mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
					}
					else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
						mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);						
					}
					else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
						mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING);
					}
				}
			}
		});



		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);

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

	private OnClickListener moveBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mMoveBtn)) {
				mSCanvas.setMovingMode(!mSCanvas.isMovingMode(), true); // true to enable one-touch panning, false to disable one touch panning
			}
		}
	};

	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int nBtnID = v.getId();
			boolean bMovingMode = mSCanvas.isMovingMode();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
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
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
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
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
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
			else if(nBtnID == mFillingBtn.getId()){
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, false);										
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to fill color", Toast.LENGTH_SHORT).show();
				}
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
		SPenSDKUtils.updateModeState(mSCanvas, mMoveBtn, null, mPenBtn, mEraserBtn, mTextBtn, mFillingBtn, null, mColorPickerBtn, null);
	}	


	@Override
	public boolean onCreateOptionsMenu(Menu menu){	

		menu.add(MENU_HOVER_STYLE_GROUP, MENU_HOVER_STYLE_GROUP, 0, "Hover Pointer Style");
		menu.add(MENU_HOVER_SHOW_GROUP, MENU_HOVER_SHOW_GROUP, 1, "Hover Pointer Show Option");
		menu.add(MENU_SIDEBUTTON_GROUP, MENU_SIDEBUTTON_GROUP, 2, "Pen Side Button");

		return super.onCreateOptionsMenu(menu);
	} 


	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()){
		case MENU_HOVER_STYLE_GROUP:
			hoverPointerStyleDlg();	
			break;
		case MENU_HOVER_SHOW_GROUP:
			hoverPointerShowOptionDlg();
			break;
		case MENU_SIDEBUTTON_GROUP:
			sideButtonStyleDlg();
			break;
		}
		return true;
	}

	private void hoverPointerStyleDlg(){
		new AlertDialog.Builder(this)
		.setTitle("Hover Pointer Style")
		.setSingleChoiceItems(R.array.hover_pointer_style, mHoverPointerStyle, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mHoverPointerStyle = which;

				switch(mHoverPointerStyle){
				case HOVER_DEFAULT:
					mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_NONE);
					break;
				case HOVER_SIMPLE_ICON:
					mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SIMPLE_CUSTOM);
					mSCanvas.setSCanvasHoverPointerSimpleIcon(SPenEventLibrary.HOVERING_SPENICON_MOVE);			
					break;
				case HOVER_SIMPLE_DRAWABLE:
					mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SIMPLE_CUSTOM);
					mSCanvas.setSCanvasHoverPointerSimpleDrawable(getResources().getDrawable(R.drawable.tool_ic_pen));
					break;
				case HOVER_SPENSDK:
					mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SPENSDK);
					break;
				case HOVER_SNOTE:
					mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SNOTE);
					break;
				}
				dialog.dismiss();
			}
		})
		.show();	
	}

	private void hoverPointerShowOptionDlg(){
		new AlertDialog.Builder(this)
		.setTitle("Hover Pointer Show Option")
		.setSingleChoiceItems(R.array.hover_pointer_show_option, mHoverPointerShowOption, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mHoverPointerShowOption = which;

				switch(mHoverPointerShowOption){
				case HOVER_SHOW_ALWAYS_ONHOVER:
					mSCanvas.setSCanvasHoverPointerShowOption(SCanvasConstants.SCANVAS_HOVERPOINTER_SHOW_OPTION_ALWAYS_ON_HOVER);
					break;
				case HOVER_SHOW_ONCE_ONHOVER:
					mSCanvas.setSCanvasHoverPointerShowOption(SCanvasConstants.SCANVAS_HOVERPOINTER_SHOW_OPTION_ONCE_ON_HOVER);
					break;	
				}
				dialog.dismiss();
			}
		})
		.show();
	}

	private void sideButtonStyleDlg(){
		new AlertDialog.Builder(this)
		.setTitle("Pen Side Button")
		.setSingleChoiceItems(R.array.side_button_style, mSideButtonStyle, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mSideButtonStyle = which;

				switch(mSideButtonStyle){
				case SIDEBUTTON_CHANGE_PEN:
					mHoverButtonAction = 0;
					break;
				case SIDEBUTTON_SHOW_SETTING:
					mHoverButtonAction = 1;
					break;
				}
				dialog.dismiss();
			}
		})
		.show();
	}
}
