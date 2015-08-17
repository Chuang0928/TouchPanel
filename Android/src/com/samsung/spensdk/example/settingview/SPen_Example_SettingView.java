package com.samsung.spensdk.example.settingview;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.samm.common.SAMMLibConstants;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.samm.common.SObjectText;
import com.samsung.spen.settings.SettingFillingInfo;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spen.settings.SettingTextInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.ColorPickerColorChangeListener;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.applistener.SettingFillingChangeListener;
import com.samsung.spensdk.applistener.SettingPresetChangeListener;
import com.samsung.spensdk.applistener.SettingStrokeChangeListener;
import com.samsung.spensdk.applistener.SettingTextChangeListener;
import com.samsung.spensdk.applistener.SettingViewChangeListener;
import com.samsung.spensdk.applistener.SettingViewShowListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;


public class SPen_Example_SettingView extends Activity {

	private final String TAG = "SPenSDK Sample";
	private final boolean SHOW_LOG = false;

	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	private final String APPLICATION_ID_NAME = "SDK Sample Application";
	private final int APPLICATION_ID_VERSION_MAJOR = 1;
	private final int APPLICATION_ID_VERSION_MINOR = 0;
	private final String APPLICATION_ID_VERSION_PATCHNAME = "Debug";

	private final int MENU_PRESET_GROUP = 1000;	
	private final int MENU_PRESET_ADD = 1001;
	private final int MENU_PRESET_DEL = 1002;
	private final int MENU_SETTINGVIEW_GROUP = 3000;
	private final int MENU_SETTINGVIEW_SIZE = 3001;
	private final int MENU_PENSETTINGVIEW_MINI = 3002;
	private final int MENU_PENSETTINGVIEW_MEDIUM = 3003;
	private final int MENU_PENSETTINGVIEW_NORMAL = 3004;
	private final int MENU_PENSETTINGVIEW_EXT = 3005;
	private final int MENU_PENSETTINGVIEW_BEAUTIFY = 3006;

	private final int MAX_PRESET_NUM = 12;

	//==============================
	// Variables
	//==============================
	Context mContext = null;

	private RelativeLayout	mCanvasContainer;
	private RelativeLayout	mSettingViewContainer;
	private SCanvasView		mSCanvas;
	private ImageView		mPinUpBtn;
	private ImageView		mPenBtn;
	private ImageView		mEraserBtn;
	private ImageView		mTextBtn;
	private ImageView		mFillingBtn;
	private ImageView		mColorPickerBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;
	private TextView		mSettingInfo;
	private ImageView		mColorSettingInfo;
	private boolean bShowBeautifyPen = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.editor_settingview);

		mContext = this;

		//------------------------------------
		// UI Setting
		//------------------------------------
		mPinUpBtn = (ImageView) findViewById(R.id.pinupBtn);
		mPinUpBtn.setOnClickListener(mPinUpClickListener);		
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

		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);

		mSettingInfo = (TextView) findViewById(R.id.settingInfo);
		mColorSettingInfo = (ImageView) findViewById(R.id.colorsettingInfo);		

		//------------------------------------
		// Create SCanvasView
		//------------------------------------
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);
		mSettingViewContainer = (RelativeLayout) findViewById(R.id.settingview_container);

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
		mSCanvas.createSettingView(mSettingViewContainer, settingResourceMapInt, settingResourceMapString);

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

				// Update button state
				mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);
				mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);
				// If preset slot is full, then preset add button will be invisible
				mSCanvas.setPresetAddButton(true);
				mSCanvas.setFingerControlPenDrawing(true);
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
		// SettingView Show Listener : Optional
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

				if(bVisible){
					SettingStrokeInfo strokeInfo = mSCanvas.getSettingViewStrokeInfo();
					if(strokeInfo != null) {
						updateColor(strokeInfo.getStrokeColor());						
					}					
				}
			}
			@Override
			public void onTextSettingViewShow(boolean bVisible) {
				if(SHOW_LOG){		
					if(bVisible) Log.i(TAG, "Text setting view is shown");
					else		 Log.i(TAG, "Text setting view is closed");
				}

				if(bVisible){
					SettingTextInfo textInfo = mSCanvas.getSettingViewTextInfo();
					if(textInfo != null) {
						updateColor(textInfo.getTextColor());						
					}

				}
			}
			@Override
			public void onFillingSettingViewShow(boolean bVisible) {
				if(SHOW_LOG){		
					if(bVisible) Log.i(TAG, "Text setting view is shown");
					else		 Log.i(TAG, "Text setting view is closed");
				}

				if(bVisible){
					SettingFillingInfo fillingInfo = mSCanvas.getSettingViewFillingInfo();
					if(fillingInfo != null) {
						updateColor(fillingInfo.getFillingColor());						
					}
				}
			}
		});

		//------------------------------------------------
		// SettingView Change Listener : Optional
		//------------------------------------------------				
		mSCanvas.setSettingViewChangeListener(new SettingViewChangeListener() {

			@Override
			public void onPenSettingViewExpanded(boolean bExpanded) {
				if(bExpanded)
					Toast.makeText(mContext, "SettingView is expanded", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(mContext, "SettingView is contracted", Toast.LENGTH_SHORT).show();
			}
		});


		//------------------------------------------------
		// SettingStrokeChangeListener Listener 
		//------------------------------------------------				
		mSCanvas.setSettingStrokeChangeListener(new SettingStrokeChangeListener() {

			@Override
			public void onClearAll(boolean bClearAllCompleted) {
				if(bClearAllCompleted) updateSetting("Clear All is completed");
			}
			@Override
			public void onEraserWidthChanged(int eraserWidth) {
				updateSetting("Eraser width is changed : " + eraserWidth);				
			}

			@Override
			public void onStrokeColorChanged(int strokeColor) {
				updateColor(strokeColor);
			}

			@Override
			public void onStrokeStyleChanged(int strokeStyle) {
				if (strokeStyle == SObjectStroke.SAMM_STROKE_STYLE_PENCIL)
					updateSetting("Stroke Style = Pen");
				else if (strokeStyle == SObjectStroke.SAMM_STROKE_STYLE_BRUSH)
					updateSetting("Stroke Style = Brush");
				else if (strokeStyle == SObjectStroke.SAMM_STROKE_STYLE_CHINESE_BRUSH)
					updateSetting("Stroke Style = Chinese Brush");
				else if (strokeStyle == SObjectStroke.SAMM_STROKE_STYLE_BEAUTIFY)
					updateSetting("Stroke Style = Beautify");
				else if (strokeStyle == SObjectStroke.SAMM_STROKE_STYLE_CRAYON)
					updateSetting("Stroke Style = Pencil Crayon");    		
				else if (strokeStyle == SObjectStroke.SAMM_STROKE_STYLE_MARKER)
					updateSetting("Stroke Style = Marker");
				else if (strokeStyle == SObjectStroke.SAMM_STROKE_STYLE_CHALK)
					updateSetting("Stroke Style = Chalk");
				else if (strokeStyle == SObjectStroke.SAMM_STROKE_STYLE_ERASER)
					updateSetting("Stroke Style = Eraser");	
			}

			@Override
			public void onStrokeWidthChanged(int strokeWidth) {
				updateSetting("Stroke width is changed : " + strokeWidth);				
			}

			@Override
			public void onStrokeAlphaChanged(int strokeAlpha) {
				updateSetting("Alpha is changed : " + strokeAlpha);				
			}

			@Override
			public void onBeautifyPenStyleParameterCursiveChanged(int cursiveParameter) {
				updateSetting("Cursive is changed : " + cursiveParameter);		
			}

			@Override
			public void onBeautifyPenStyleParameterDummyChanged(int dummyParamter) {		
				updateSetting("Dummy is changed : " + dummyParamter);	
			}

			@Override
			public void onBeautifyPenStyleParameterModulationChanged(int modulationParamter) {	
				updateSetting("Modulation is changed : " + modulationParamter);	
			}

			@Override
			public void onBeautifyPenStyleParameterSustenanceChanged(int sustenanceParamter) {
				updateSetting("Sustenance is changed : " + sustenanceParamter);	
			}

			@Override
			public void onBeautifyPenStyleParameterBeautifyStyleIDChanged(int styleID) {
				updateSetting("StyleID is changed : " + styleID);	
			}
			@Override
			public void onBeautifyPenStyleParameterFillStyleChanged(int fillStyle) {
				updateSetting("FillStyle is changed : " + fillStyle);					
			}
		});

		//------------------------------------------------
		// OnSettingTextChangeListener Listener 
		//------------------------------------------------				
		mSCanvas.setSettingTextChangeListener(new SettingTextChangeListener(){

			@Override
			public void onTextColorChanged(int textColor) {
				updateColor(textColor);
			}

			@Override
			public void onTextFontChanged(String fontName) {
				updateSetting("Font is changed : " + fontName);	
			}

			@Override
			public void onTextSizeChanged(int textSize) {
				updateSetting("Text size is changed : " + textSize);	
			}

			@Override
			public void onTextStyleChanged(int textStyle) {
				StringBuilder textStyleString = new StringBuilder();
				boolean bDefault = (textStyle == SObjectText.SAMM_TEXT_STYLE_NONE);
				if(bDefault) textStyleString.append("Default ");
				boolean bBold = ((textStyle & SObjectText.SAMM_TEXT_STYLE_BOLD)!=0);
				if(bBold) textStyleString.append("Bold ");			
				boolean bItalic = ((textStyle & SObjectText.SAMM_TEXT_STYLE_ITALIC)!=0);
				if(bItalic) textStyleString.append("Italic ");
				boolean bUnderline = ((textStyle & SObjectText.SAMM_TEXT_STYLE_UNDERLINE)!=0);
				if(bUnderline) textStyleString.append("Underline ");
				updateSetting("Text style is changed : " + textStyleString);	
			}

			@Override
			public void onTextAlignmentChanged(int textHorizAlignment) {
				switch(textHorizAlignment){
				case SAMMLibConstants.SAMM_ALIGN_NORMAL:
					updateSetting("Text alignment is changed as Left alignment");
					break;
				case SAMMLibConstants.SAMM_ALIGN_CENTER:
					updateSetting("Text alignment is changed as Center alignment");
					break;
				case SAMMLibConstants.SAMM_ALIGN_OPPOSITE:
					updateSetting("Text alignment is changed as Right alignment");
					break;
				}
			}
		});

		//------------------------------------------------
		// SettingFillingChangeListener Listener 
		//------------------------------------------------				
		mSCanvas.setSettingFillingChangeListener(new SettingFillingChangeListener(){
			@Override
			public void onFillingColorChanged(int fillingColor) {
				updateColor(fillingColor);
			}
		});

		//------------------------------------------------
		// SettingPresetDeleteBtnClickListener Listener 
		//------------------------------------------------				
		mSCanvas.setSettingPresetChangeListener(new SettingPresetChangeListener() {		
			@Override
			public void onDeleteBtnClick(int nPresetIndex) {
				Toast.makeText(mContext, nPresetIndex + "'th preset is deleted!", Toast.LENGTH_SHORT).show();
				mSCanvas.deleteSettingViewPresetInfo(nPresetIndex, false);				
			}		

			@Override
			public void onSelectBtnClick(int nPresetIndex) {
				Toast.makeText(mContext, nPresetIndex + "'th preset is seleted!", Toast.LENGTH_SHORT).show();				
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
	public boolean onCreateOptionsMenu(Menu menu){
		SubMenu fileMenu = menu.addSubMenu("Preset Operation");
		fileMenu.add(MENU_PRESET_GROUP, MENU_PRESET_ADD, 1, "Add Preset");
		fileMenu.add(MENU_PRESET_GROUP, MENU_PRESET_DEL, 2, "Delete Preset");				
		
		SubMenu settingViewMenu = menu.addSubMenu("SettingView");
		settingViewMenu.add(MENU_SETTINGVIEW_GROUP, MENU_SETTINGVIEW_SIZE, 10, "SettingView Size");
		settingViewMenu.add(MENU_SETTINGVIEW_GROUP, MENU_PENSETTINGVIEW_MINI, 20, "Mini Pen SettingView");
		settingViewMenu.add(MENU_SETTINGVIEW_GROUP, MENU_PENSETTINGVIEW_MEDIUM, 30, "Medium Pen SettingView");
		settingViewMenu.add(MENU_SETTINGVIEW_GROUP, MENU_PENSETTINGVIEW_NORMAL, 40, "Normal Pen SettingView");
		settingViewMenu.add(MENU_SETTINGVIEW_GROUP, MENU_PENSETTINGVIEW_EXT, 50, "Extension Pen SettingView");
		// initial set
		settingViewMenu.add(MENU_SETTINGVIEW_GROUP, MENU_PENSETTINGVIEW_BEAUTIFY, 60, "Hide Beautify Pen Tab");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu){
		super.onMenuOpened(featureId, menu);	

		if (menu == null) 
			return true;
		
		MenuItem menuItemPresetAdd = menu.findItem(MENU_PRESET_ADD);
		MenuItem menuItemPresetDelete = menu.findItem(MENU_PRESET_DEL);		
		if(menuItemPresetAdd!=null ) menuItemPresetAdd.setEnabled(!(mSCanvas.getSettingViewPresetNum() == MAX_PRESET_NUM));
		if(menuItemPresetDelete!=null ) menuItemPresetDelete.setEnabled(!(mSCanvas.getSettingViewPresetNum() == 0));		
		
		MenuItem menuItemPenSettingViewMini = menu.findItem(MENU_PENSETTINGVIEW_MINI);
		MenuItem menuItemPenSettingViewMedium = menu.findItem(MENU_PENSETTINGVIEW_MEDIUM);	
		MenuItem menuItemPenSettingViewNormal = menu.findItem(MENU_PENSETTINGVIEW_NORMAL);
		MenuItem menuItemPenSettingViewExt = menu.findItem(MENU_PENSETTINGVIEW_EXT);	
		MenuItem menuItemPenSettingViewBeautify = menu.findItem(MENU_PENSETTINGVIEW_BEAUTIFY);		
		
		if(menuItemPenSettingViewMini!=null ) menuItemPenSettingViewMini.setEnabled(mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
		if(menuItemPenSettingViewMedium!=null ) menuItemPenSettingViewMedium.setEnabled(mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN);		
		if(menuItemPenSettingViewNormal!=null ) menuItemPenSettingViewNormal.setEnabled(mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
		if(menuItemPenSettingViewExt!=null ) menuItemPenSettingViewExt.setEnabled(mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN);		
		if(menuItemPenSettingViewBeautify!=null ) {
			menuItemPenSettingViewBeautify.setEnabled(mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
			if(bShowBeautifyPen)
				menuItemPenSettingViewBeautify.setTitle("Show Beautify Pen Tab");
			else			
				menuItemPenSettingViewBeautify.setTitle("Hide Beautify Pen Tab");	
		}
			
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()){
		case MENU_PRESET_ADD:
			mSCanvas.addSettingViewPresetInfo(mSCanvas.getSettingViewStrokeInfo(), true);
			break;
		case MENU_PRESET_DEL:
			mSCanvas.deleteSettingViewPresetInfo(0, true);
			break;
		case MENU_SETTINGVIEW_SIZE:
			int nWidth = mSCanvas.getSettingViewWidth();
			int nHeight = mSCanvas.getSettingViewHeight();
			Toast.makeText(mContext, "Current Width=" + nWidth + ", Height=" + nHeight, Toast.LENGTH_SHORT).show();
			break;
		case MENU_PENSETTINGVIEW_MINI:
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);		
			break;			
		case MENU_PENSETTINGVIEW_MEDIUM:
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MEDIUM);
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);		
			break;
		case MENU_PENSETTINGVIEW_NORMAL:
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);		
			break;			
		case MENU_PENSETTINGVIEW_EXT:
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);		
			break;		
		case MENU_PENSETTINGVIEW_BEAUTIFY:			
			mSCanvas.supportBeautifyStrokeSetting(bShowBeautifyPen);
			// toggle
			bShowBeautifyPen = !bShowBeautifyPen;
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);	
			break;		
		}
		return true;
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
			else if(nBtnID == mFillingBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
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

			Toast.makeText(mContext, "Long Click to show mini-setting view", Toast.LENGTH_SHORT).show();
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


	// color picker mode
	private OnClickListener mPinUpClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mPinUpBtn)) {
				// Toggle
				boolean bIsPinUpState = !mSCanvas.isSettingViewPinUpState(); 
				mSCanvas.setSettingViewPinUpState(bIsPinUpState);
				updateModeState();
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
		mPinUpBtn.setSelected(mSCanvas.isSettingViewPinUpState());
		SPenSDKUtils.updateModeState(mSCanvas, null, null, mPenBtn, mEraserBtn, mTextBtn, mFillingBtn, null, mColorPickerBtn, null);
	}	

	private void updateSetting(String strInfo){
		mSettingInfo.setText(strInfo);
	}

	private void updateColor(int nColor){
		mSettingInfo.setBackgroundDrawable(new ColorDrawable(nColor));
		mColorSettingInfo.setAlpha(Color.alpha(nColor));
		// mSettingInfo.setAlpha(Color.alpha(nColor));
	}	
}
