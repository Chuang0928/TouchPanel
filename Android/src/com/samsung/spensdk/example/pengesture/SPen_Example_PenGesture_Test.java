package com.samsung.spensdk.example.pengesture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.samm.common.SObject;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.spen.lib.gesture.SPenGestureInfo;
import com.samsung.spen.lib.gesture.SPenGestureLibrary;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.applistener.SObjectSelectListener;
import com.samsung.spensdk.applistener.SObjectUpdateListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

public class SPen_Example_PenGesture_Test extends Activity {

	private final String TAG = "PenGesture";

	// ==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	// ==============================
	private final String APPLICATION_ID_NAME = "SDK Sample Application";
	private final int APPLICATION_ID_VERSION_MAJOR = 1;
	private final int APPLICATION_ID_VERSION_MINOR = 0;
	private final String APPLICATION_ID_VERSION_PATCHNAME = "Debug";

	// ==============================
	// Variables
	// ==============================
	Context mContext = null;

	private FrameLayout mLayoutContainer;
	private RelativeLayout mCanvasContainer;
	private SCanvasView mSCanvas;
	private SCanvasView mSCanvasOverlay;

	private ImageView mGestureModeBtn;
	private ImageView mPenBtn;
	private ImageView mEraserBtn;
	private ImageView mUndoBtn;
	private ImageView mRedoBtn;

	private boolean mSPenGestureMode = true;		
	private Toast mToast;

	public SPenGestureLibrary mSPenGestureLibrary;	
	public int mCurGestureNumber;		// number of registered gesture
	public ArrayList<SPenGestureInfo> mGestureInfo;  // recognition result
	public PointF[][] mCurrentPoints = null;

	private Timer mWaitingTimer = null;
	private Handler mGestureActionHandler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.example_pengesture_test);

		mContext = this;

		// ------------------------------------
		// UI Setting
		// ------------------------------------
		mGestureModeBtn= (ImageView) findViewById(R.id.gestureBtn);
		mGestureModeBtn.setOnClickListener(mGestureModeClickListener);

		mPenBtn = (ImageView) findViewById(R.id.penBtn);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);

		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);


		// ------------------------------------
		// Create SCanvasView
		// ------------------------------------
		mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);

		mSCanvas = new SCanvasView(mContext);
		mCanvasContainer.addView(mSCanvas);

		mSCanvasOverlay = new SCanvasView(mContext);
		mCanvasContainer.addView(mSCanvasOverlay);


		// ------------------------------------
		// SettingView Setting
		// ------------------------------------
		// Resource Map for Layout & Locale		
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, true, true);
		// Talk & Description Setting by Locale
		SPenSDKUtils.addTalkbackAndDescriptionStringResourceMap(settingResourceMapInt);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, true, true);

		// Create Setting View
		// New Example Code : create SettingView by SCanvasView
		mSCanvas.createSettingView(mLayoutContainer, settingResourceMapInt, settingResourceMapString);
		// mSCanvasOverlay.createSettingView(mLayoutContainer, settingResourceMapInt, settingResourceMapString);

		// ====================================================================================
		//
		// Set Callback Listener(Interface)
		//
		// ====================================================================================
		// ------------------------------------------------
		// SCanvas Listener
		// ------------------------------------------------
		mSCanvas.setSCanvasInitializeListener(new SCanvasInitializeListener() {
			@Override
			public void onInitialized() {
				// --------------------------------------------
				// Start SCanvasView/CanvasView Task Here
				// --------------------------------------------
				// Application Identifier Setting
				if (!mSCanvas.setAppID(APPLICATION_ID_NAME, APPLICATION_ID_VERSION_MAJOR, APPLICATION_ID_VERSION_MINOR, APPLICATION_ID_VERSION_PATCHNAME))
					Toast.makeText(mContext, "Fail to set App ID.", Toast.LENGTH_LONG).show();

				// Set Title
				if (!mSCanvas.setTitle("SPen-SDK Test"))
					Toast.makeText(mContext, "Fail to set Title.", Toast.LENGTH_LONG).show();

				// Set Pen Only Mode with Finger Control				
				mSCanvas.setFingerControlPenDrawing(true);

				// Support only stroke object
				mSCanvas.setCanvasSupportPenOnly(true);

				// No scroll view
				mSCanvas.setScrollBarVisible(false);
				
				// Update 
				updateOverlayView();
			}
		});

		// ------------------------------------------------
		// History Change Listener
		// ------------------------------------------------
		mSCanvas.setHistoryUpdateListener(new HistoryUpdateListener() {
			@Override
			public void onHistoryChanged(boolean undoable, boolean redoable) {
				mUndoBtn.setEnabled(undoable);
				mRedoBtn.setEnabled(redoable);
			}
		});

		// ------------------------------------------------
		// SObject Listener
		// ------------------------------------------------
		mSCanvas.setSObjectSelectListener(new SObjectSelectListener() {
			// =============================================
			// Called when the select or deselect object.
			// =============================================
			@Override
			public void onSObjectSelected(SObject sObject, boolean bSelected) {
				updateModeState();
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

		//========================================================================
		//
		// SCanvas Overlay Listener
		//
		//========================================================================		
		mSCanvasOverlay.setSCanvasInitializeListener(new SCanvasInitializeListener() {
			@Override
			public void onInitialized() {
				// Set Pen Only Mode with Finger Control				
				mSCanvasOverlay.setFingerControlPenDrawing(true);
				mSCanvasOverlay.setBGColor(0x22FFFF00);
				// Support only stroke object
				mSCanvasOverlay.setCanvasSupportPenOnly(true);
				// No scroll view
				mSCanvasOverlay.setScrollBarVisible(false);				

				prepareGestureStrokeInfo();
			}
		});

		mSCanvasOverlay.setSObjectUpdateListener(new SObjectUpdateListener() {
			@Override
			public void onSObjectInserted(SObject sObject, boolean byUndo, boolean byRedo) {
			}			
			@Override
			public void onSObjectInserted(SObject sObject, boolean byUndo, boolean byRedo, boolean byChangeGroupState) {
			}
			@Override
			public void onSObjectDeleted(SObject sObject, boolean byUndo, boolean byRedo, boolean bFreeMemory) {
			}			
			@Override
			public void onSObjectDeleted(SObject sObject, boolean byUndo, boolean byRedo, boolean byChangeGroupState, boolean bFreeMemory) {
			}
			@Override
			public void onSObjectSelected(SObject sObject, boolean bSelected) {
			}
			@Override
			public void onSObjectChanged(SObject sObject, boolean byUndo, boolean byRedo) {
			}
			@Override
			public void onSObjectClearAll(boolean bFreeMemory) {				
			}

			// Start Timer
			@Override
			public boolean onSObjectStrokeInserting(SObjectStroke sObjectStroke) {
				if (mSPenGestureMode) {

					// Start Waiting Timer (delete if it exists) 
					if(mWaitingTimer != null) {
						mWaitingTimer.cancel();
						mWaitingTimer = null;
					}
					mWaitingTimer = new Timer();
					mWaitingTimer.schedule(new TimerTask(){					
						@Override
						public void run() { 
							try {
								mGestureActionHandler.sendEmptyMessage(0);
							} catch (Exception e) {e.printStackTrace(); }
						}												
					},  1000);
				}
				return false;		
			}		
		});				

		// Initialization
		mSPenGestureLibrary = new SPenGestureLibrary(SPen_Example_PenGesture_Test.this);
		mSPenGestureLibrary.openSPenGestureEngine();
		mSPenGestureMode = true;

		mGestureActionHandler  = new Handler(){
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);

				Log.w(TAG, "Timer run()");

				//int nObjectNum = mSCanvasOverlay.getSAMMObjectNum();
				LinkedList<SObject> SObjectList = mSCanvasOverlay.getSObjectList(true);
				if(SObjectList==null){
					Log.e(TAG, "Sobject List is null");
					mSCanvasOverlay.clearScreen();
					return;
				}
				if(SObjectList.size()<=0){
					Log.e(TAG, "There is no valid SObject");
					mSCanvasOverlay.clearScreen();
					return;
				}

				int nStrokeNum = SObjectList.size(); 
				if(nStrokeNum<=0){
					Log.e(TAG, "There is no valid SObject");
					mSCanvas.clearScreen();
					return;
				}

				mCurrentPoints = new PointF[nStrokeNum][];
				int nCount=0;
				for(SObject sObject: SObjectList){
					mCurrentPoints[nCount] = ((SObjectStroke)sObject).getPoints();
					nCount++;
				}

				// Step 1. Detect Gesture
				// Get Gesture result
				mGestureInfo = mSPenGestureLibrary.recognizeSPenGesture(mCurrentPoints);

				if (mGestureInfo == null) {
					SToastS("error");
					mSCanvasOverlay.clearScreen();
					return;
				}
				if(mGestureInfo.size()<=0){
					SToastS("There is no result");
					mSCanvasOverlay.clearScreen();
					return;
				}

				String gestureAction = mGestureInfo.get(0).mName;
				int score = mGestureInfo.get(0).mScore;
				String result = "Gesture: "+gestureAction + ", Score: " + score;
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
				Log.w(TAG, result);

				// Finally, Clear Screen
				mSCanvasOverlay.clearScreen();				
			}
		};

		updateOverlayView();

		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);

		// Caution:
		// Do NOT load file or start animation here because we don't know canvas
		// size here.
		// Start such SCanvasView Task at onInitialized() of
		// SCanvasInitializeListener
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
		if (!mSCanvasOverlay.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
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
			// If the mode is not changed, open the setting view. If the mode is
			// same, close the setting view.
			if (nBtnID == mPenBtn.getId()) {
				if (mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN) {
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
				} else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
					updateModeState();
				}
			} else if (nBtnID == mEraserBtn.getId()) {
				if (mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER) {
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				} else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
					updateModeState();
				}
			} 
		}
	};

	OnClickListener mGestureModeClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mSPenGestureMode = !mSPenGestureMode;
			updateOverlayView();
		}
	};

	OnClickListener mSettingClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SPen_Example_PenGesture_Test.this, SPen_Example_PenGestureSetting.class);
			startActivity(intent);
		}
	};

	private void updateOverlayView(){
		if (mSPenGestureMode == true) {
			mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
			mSCanvas.closeSettingView();
			mSCanvasOverlay.setVisibility(View.VISIBLE);
			// mSCanvas scroll invisible
			mSCanvas.setScrollBarVisible(false);			
		} else {
			updateModeState();
			mSCanvasOverlay.setVisibility(View.GONE);
			// mSCanvas scroll visible
			mSCanvas.setScrollBarVisible(true);			
		}
		updateModeState();
	}

	private void prepareGestureStrokeInfo() {		
		// Gesture Stroke Info
		SettingStrokeInfo GesturePen = new SettingStrokeInfo();
		GesturePen.setStrokeStyle(SObjectStroke.SAMM_STROKE_STYLE_CRAYON);
		GesturePen.setStrokeWidth(20);
		GesturePen.setStrokeColor(0xFFFF0000);
		GesturePen.setStrokeAlpha(100);
		// mSCanvasOverlay.setSettingViewStrokeInfo(GesturePen);
		mSCanvasOverlay.setSettingStrokeInfo(GesturePen);
	}


	// Update tool button
	private void updateModeState() {
		int nCurMode = mSCanvas.getCanvasMode();
		mGestureModeBtn.setSelected(mSPenGestureMode);
		mPenBtn.setSelected(nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
		mEraserBtn.setSelected(nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
	}

	public void SToastS(String i_String) {
		if (mToast == null) {
			mToast = Toast.makeText(mContext, i_String, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(i_String);
		}
		mToast.show();
	}	
}
