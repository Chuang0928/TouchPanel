package com.samsung.spensdk.example.basicui;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.samm.common.SObjectStroke;
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
import com.samsung.spensdk.applistener.SPenTouchListener;
import com.samsung.spensdk.applistener.SettingStrokeChangeListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.spenevent.SPen_Example_SPenEvent.Client;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

public class SPen_Example_BasicUI extends Activity {

	private final String TAG = "SPenSDK Sample";

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
	private ImageView mPenBtn;
	private ImageView mEraserBtn;
	private ImageView mTextBtn;
	private ImageView mUndoBtn;
	private ImageView mRedoBtn;

	private PointF mMappedPoint = null;
	boolean bRunning = true;
	Queue<String> queue = new ConcurrentLinkedQueue<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.editor_basic_ui);

		mContext = this;

		// ------------------------------------
		// UI Setting
		// ------------------------------------
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

		// ------------------------------------
		// Create SCanvasView
		// ------------------------------------
		mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);

		// Add SCanvasView under minSDK 14(AndroidManifext.xml)
		// mSCanvas = new SCanvasView(mContext);
		// mCanvasContainer.addView(mSCanvas);

		// Add SCanvasView under minSDK 10(AndroidManifext.xml) for preventing
		// text input error
		mSCanvas = new SCanvasView(mContext);
		mSCanvas.addedByResizingContainer(mCanvasContainer);

		// ------------------------------------
		// SettingView Setting
		// ------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String, Integer> settingResourceMapInt = SPenSDKUtils
				.getSettingLayoutLocaleResourceMap(true, true, true, true);
		// Talk & Description Setting by Locale
		SPenSDKUtils
				.addTalkbackAndDescriptionStringResourceMap(settingResourceMapInt);
		// Resource Map for Custom font path
		HashMap<String, String> settingResourceMapString = SPenSDKUtils
				.getSettingLayoutStringResourceMap(true, true, true, true);
		// Create Setting View
		mSCanvas.createSettingView(mLayoutContainer, settingResourceMapInt,
				settingResourceMapString);

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
				if (!mSCanvas.setAppID(APPLICATION_ID_NAME,
						APPLICATION_ID_VERSION_MAJOR,
						APPLICATION_ID_VERSION_MINOR,
						APPLICATION_ID_VERSION_PATCHNAME))
					Toast.makeText(mContext, "Fail to set App ID.",
							Toast.LENGTH_LONG).show();

				// Set Title
				if (!mSCanvas.setTitle("SPen-SDK Test"))
					Toast.makeText(mContext, "Fail to set Title.",
							Toast.LENGTH_LONG).show();

				// Set Pen Only Mode with Finger Control
				mSCanvas.setFingerControlPenDrawing(true);

				// Update button state
				updateModeState();
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
		// SCanvas Mode Changed Listener
		// ------------------------------------------------
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

		// ------------------------------------------------
		// SettingStrokeChangeListener Listener
		// ------------------------------------------------
		mSCanvas.setSettingStrokeChangeListener(new SettingStrokeChangeListener() {

			@Override
			public void onClearAll(boolean bClearAllCompleted) {
				// if(bClearAllCompleted)
				// updateSetting("Clear All is completed");
				queue.offer(String.format("W,0"));
			}

			@Override
			public void onEraserWidthChanged(int eraserWidth) {
				// updateSetting("Eraser width is changed : " + eraserWidth);
			}

			@Override
			public void onStrokeColorChanged(int strokeColor) {
				// updateColor(strokeColor);
				queue.offer(String.format("S,%d", strokeColor));
			}

			@Override
			public void onStrokeStyleChanged(int strokeStyle) {
				// if (strokeStyle == SObjectStroke.SAMM_STROKE_STYLE_PENCIL)
				// updateSetting("Stroke Style = Pen");
				// else if (strokeStyle ==
				// SObjectStroke.SAMM_STROKE_STYLE_BRUSH)
				// updateSetting("Stroke Style = Brush");
				// else if (strokeStyle ==
				// SObjectStroke.SAMM_STROKE_STYLE_CHINESE_BRUSH)
				// updateSetting("Stroke Style = Chinese Brush");
				// else if (strokeStyle ==
				// SObjectStroke.SAMM_STROKE_STYLE_BEAUTIFY)
				// updateSetting("Stroke Style = Beautify");
				// else if (strokeStyle ==
				// SObjectStroke.SAMM_STROKE_STYLE_CRAYON)
				// updateSetting("Stroke Style = Pencil Crayon");
				// else if (strokeStyle ==
				// SObjectStroke.SAMM_STROKE_STYLE_MARKER)
				// updateSetting("Stroke Style = Marker");
				// else if (strokeStyle ==
				// SObjectStroke.SAMM_STROKE_STYLE_CHALK)
				// updateSetting("Stroke Style = Chalk");
				// else if (strokeStyle ==
				// SObjectStroke.SAMM_STROKE_STYLE_ERASER)
				// updateSetting("Stroke Style = Eraser");
			}

			@Override
			public void onStrokeWidthChanged(int strokeWidth) {
				// updateSetting("Stroke width is changed : " + strokeWidth);
			}

			@Override
			public void onStrokeAlphaChanged(int strokeAlpha) {
				// updateSetting("Alpha is changed : " + strokeAlpha);
			}

			@Override
			public void onBeautifyPenStyleParameterCursiveChanged(
					int cursiveParameter) {
				// updateSetting("Cursive is changed : " + cursiveParameter);
			}

			@Override
			public void onBeautifyPenStyleParameterDummyChanged(
					int dummyParamter) {
				// updateSetting("Dummy is changed : " + dummyParamter);
			}

			@Override
			public void onBeautifyPenStyleParameterModulationChanged(
					int modulationParamter) {
				// updateSetting("Modulation is changed : " +
				// modulationParamter);
			}

			@Override
			public void onBeautifyPenStyleParameterSustenanceChanged(
					int sustenanceParamter) {
				// updateSetting("Sustenance is changed : " +
				// sustenanceParamter);
			}

			@Override
			public void onBeautifyPenStyleParameterBeautifyStyleIDChanged(
					int styleID) {
				// updateSetting("StyleID is changed : " + styleID);
			}

			@Override
			public void onBeautifyPenStyleParameterFillStyleChanged(
					int fillStyle) {
				// updateSetting("FillStyle is changed : " + fillStyle);
			}
		});

		// --------------------------------------------
		// Set S pen Touch Listener
		// --------------------------------------------
		mSCanvas.setSPenTouchListener(new SPenTouchListener() {

			@Override
			public boolean onTouchFinger(View view, MotionEvent event) {
				updateTouchUI(event.getX(), event.getY(), event.getPressure(),
						event.getAction(), "Finger");

				// Update Current Color
				// if (mCurrentTool != TOOL_FINGER)
				// mCurrentTool = TOOL_FINGER;
				//
				// if (event.getAction() == MotionEvent.ACTION_DOWN)
				// mSCanvas.setSettingViewStrokeInfo(mStrokeInfoFinger);

				return false; // dispatch event to SCanvasView for drawing
			}

			@Override
			public boolean onTouchPen(View view, MotionEvent event) {
				updateTouchUI(event.getX(), event.getY(), event.getPressure(),
						event.getAction(), "Pen");

				// Update Current Color
				// if (mCurrentTool != TOOL_PEN)
				// mCurrentTool = TOOL_PEN;
				//
				// if (event.getAction() == MotionEvent.ACTION_DOWN)
				// mSCanvas.setSettingViewStrokeInfo(mStrokeInfoPen);

				return false; // dispatch event to SCanvasView for drawing
			}

			@Override
			public boolean onTouchPenEraser(View view, MotionEvent event) {
				updateTouchUI(event.getX(), event.getY(), event.getPressure(),
						event.getAction(), "Pen-Eraser");

				// if (mCurrentTool != TOOL_PEN_ERASER)
				// mCurrentTool = TOOL_PEN_ERASER;
				//
				// if (event.getAction() == MotionEvent.ACTION_DOWN)
				// mSCanvas.setEraserStrokeSetting(SObjectStroke.SAMM_DEFAULT_MAX_ERASERSIZE);

				return false; // dispatch event to SCanvasView for drawing
			}

			@Override
			public void onTouchButtonDown(View view, MotionEvent event) {
				// Toast.makeText(mContext, "S Pen Button Down on Touch",
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onTouchButtonUp(View view, MotionEvent event) {
				Toast.makeText(mContext, "S Pen Button Up on Touch",
						Toast.LENGTH_SHORT).show();
			}

		});

		// --------------------------------------------
		// [Custom Hover Icon Only]
		// Set Custom Hover Icon
		// --------------------------------------------
		// mSPenEventLibrary.setCustomHoveringIcon(mContext, mImageView,
		// getResources().getDrawable(R.drawable.custom_hover_icon));

		// --------------------------------------------
		// [Hover Listener Only]
		// Set SPenHoverListener
		// --------------------------------------------
		// mSPenEventLibrary.setSPenHoverListener(mImageView, new
		// SPenHoverListener(){...}

		// --------------------------------------------
		// [Hover Listener & Custom Hover Icon]
		// Set S pen HoverListener & Custom Hover Icon
		// --------------------------------------------
		mSCanvas.setSPenHoverListener(new SPenHoverListener() {
			@Override
			public boolean onHover(View view, MotionEvent event) {
				updateHoverUI(event.getX(), event.getY(), event.getPressure(),
						event.getAction(), "Hover");
				return false;
			}

			@Override
			public void onHoverButtonDown(View view, MotionEvent event) {
			}

			@Override
			public void onHoverButtonUp(View view, MotionEvent event) {
				mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
			}
		});

		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);

		new Thread(new Client()).start();

		// Caution:
		// Do NOT load file or start animation here because we don't know canvas
		// size here.
		// Start such SCanvasView Task at onInitialized() of
		// SCanvasInitializeListener
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Release SCanvasView resources
		if (!mSCanvas.closeSCanvasView())
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
					mSCanvas.setSettingViewSizeOption(
							SCanvasConstants.SCANVAS_SETTINGVIEW_PEN,
							SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
				} else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(
							SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
					updateModeState();
				}
			} else if (nBtnID == mEraserBtn.getId()) {
				if (mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER) {
					mSCanvas.setSettingViewSizeOption(
							SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER,
							SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				} else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(
							SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
					updateModeState();
				}
			} else if (nBtnID == mTextBtn.getId()) {
				if (mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_TEXT) {
					mSCanvas.setSettingViewSizeOption(
							SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT,
							SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
				} else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
					mSCanvas.showSettingView(
							SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to insert Text",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	// Update tool button
	private void updateModeState() {
		SPenSDKUtils.updateModeState(mSCanvas, null, null, mPenBtn, mEraserBtn,
				mTextBtn, null, null, null, null);
	}

	// Update Touch UI
	private void updateTouchUI(float x, float y, float pressure, int action,
			String tool) {

		mMappedPoint = mSCanvas.mapSCanvasPoint(new PointF(x, y));
		if (mMappedPoint != null) {
			// if (mMappedPoint.x != x || mMappedPoint.y != y) {
			// mX.setText("X : " + String.format("%.2f", x) + "("
			// + String.format("%.2f", mMappedPoint.x) + ")");
			// mY.setText("Y : " + String.format("%.2f", y) + "("
			// + String.format("%.2f", mMappedPoint.y) + ")");
			// } else {
			// mX.setText("X : " + String.format("%.2f", x));
			// mY.setText("Y : " + String.format("%.2f", y));
			// }

			queue.offer(String.format("D,%.2f,%.2f", x, y));
		}

		// mPressure.setText("Pressure : " + String.format("%.3f", pressure));

		if (action == MotionEvent.ACTION_UP) {
			queue.offer("C,-1.0,-1.0");
		}

		// if (action == MotionEvent.ACTION_DOWN)
		// mTouchAction.setText("DOWN");
		// else if (action == MotionEvent.ACTION_MOVE)
		// mTouchAction.setText("MOVE");
		// else if (action == MotionEvent.ACTION_UP)
		// mTouchAction.setText("UP");
		// else if (action == MotionEvent.ACTION_CANCEL)
		// mTouchAction.setText("CANCEL");
		// else
		// mTouchAction.setText("Unknow");
		// mTool.setText(tool);
	}

	// Update Hover UI
	private void updateHoverUI(float x, float y, float pressure, int action,
			String tool) {

		// For noise point On Hover, so filter it
		if (x >= 0 && y >= 0) {
			mMappedPoint = mSCanvas.mapSCanvasPoint(new PointF(x, y));
			if (mMappedPoint != null) {
				// if (mMappedPoint.x != x || mMappedPoint.y != y) {
				// mX.setText("X : " + String.format("%.2f", x) + "("
				// + String.format("%.2f", mMappedPoint.x) + ")");
				// mY.setText("Y : " + String.format("%.2f", y) + "("
				// + String.format("%.2f", mMappedPoint.y) + ")");
				// } else {
				// mX.setText("X : " + String.format("%.2f", x));
				// mY.setText("Y : " + String.format("%.2f", y));
				// }

				queue.offer(String.format("H,%.2f,%.2f", x, y));
			}
			// mPressure.setText("Pressure : " + String.format("%.3f",
			// pressure));
		}

		// if (action == MotionEvent.ACTION_HOVER_ENTER)
		// mHoverAction.setText("HOVER ENTER");
		// else if (action == MotionEvent.ACTION_HOVER_MOVE)
		// mHoverAction.setText("HOVER MOVE");
		// else if (action == MotionEvent.ACTION_HOVER_EXIT)
		// mHoverAction.setText("HOVER EXIT");
		// else
		// mHoverAction.setText("Unknow");
	}

	public class Client implements Runnable {
		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress
						.getByName("255.255.255.255");
				
				// updatetrack("Client: Start connecting\n");
				DatagramSocket socket = new DatagramSocket();
				byte[] buf;

				socket.setBroadcast(true);
				while (bRunning) {
					String myObject = queue.poll();
					if (myObject != null) {
						if (!myObject.isEmpty()) {
							buf = myObject.getBytes();
						} else {
							buf = ("Default message").getBytes();
						}

						DatagramPacket packet = new DatagramPacket(buf,
								buf.length, serverAddr, 11000);
						// updatetrack("Client: Sending '" + new String(buf)
						// + "'\n");
						socket.send(packet);
						// updatetrack("Client: Message sent\n");
						// updatetrack("Client: Succeed!\n");
					} else {
						Thread.sleep(100);
					}
				}
			} catch (Exception e) {
				// updatetrack("Client: Error!\n");
			}
		}
	}
}
