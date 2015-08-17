package com.samsung.spensdk.example.pengesture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.samm.common.SObject;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.spen.lib.gesture.SPenGestureInfo;
import com.samsung.spen.lib.gesture.SPenGestureLibrary;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SObjectUpdateListener;
import com.samsung.spensdk.example.R;

public class SPen_Example_PenGestureSetting extends Activity implements OnClickListener {

	private final String TAG = "SPenSDK Sample";


	public SCanvasView mSCanvas;

	public SPenGestureLibrary mSPenGestureLibrary;	
	public int mCurGestureNumber;		// number of registered gesture
	public ArrayList<SPenGestureInfo> mGestureInfo;  // recognition result
	public PointF[][] mCurrentPoints = null;

	// Timer thread
	private Timer mWaitingTimer = null;
	private Handler mGestureActionHandler  = null;


	// UI Components
	public ArrayList<GestureListItem> mGestureListItem;
	public ListView mGestureList;
	public GestureListAdapter mGestureAdapter;
	public ArrayList<ResultListItem> mResultListItem;
	public ListView mResultList;
	public ResultListAdapter mResultAdapter;

	public Toast mToast;
	public EditText mEditTextGestureFilePath;
	public EditText mEditTextGestureName;
	public ImageView mImageViewCurrntGesture;

	InputMethodManager mImm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.example_pengesture);
		mCurGestureNumber = 0;

		mSCanvas = (SCanvasView) findViewById(R.id.canvas_view);

		mSPenGestureLibrary = new SPenGestureLibrary(SPen_Example_PenGestureSetting.this);

		mSPenGestureLibrary.openSPenGestureEngine();

		// ====================================================================================
		//
		// Set Callback Listener(Interface)
		//
		// ====================================================================================
		SCanvasInitializeListener scanvasInitializeListener = new SCanvasInitializeListener() {
			@Override
			public void onInitialized() {

				// --------------------------------------------
				// Start SCanvasView/CanvasView Task Here
				// --------------------------------------------
				SettingStrokeInfo GesturePen = new SettingStrokeInfo();
				GesturePen.setStrokeStyle(SObjectStroke.SAMM_STROKE_STYLE_SOLID);
				GesturePen.setStrokeColor(0xff68E23A);
				mSCanvas.setSettingViewStrokeInfo(GesturePen);
			}
		};

		SObjectUpdateListener mSObjectListenerUI = new SObjectUpdateListener() {

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
			public boolean onSObjectStrokeInserting(SObjectStroke sObjectStroke) {

				//	mCurrentPoints[0] = sObjectStroke.getPoints();
				//	KeyboardHide();
				//
				//	if(mCurrentPoints != null){
				//		mGestureInfo = mSPenGestureLibrary.recognizeSPenGesture(mCurrentPoints);
				//	}
				//
				//	new Thread() {
				//		@Override
				//		public void run() { 		     
				//			Message msg = handler.obtainMessage();
				//			handler.sendMessage(msg);
				//		}
				//	}.start();		


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

				return false;
			}			

			@Override
			public void onSObjectClearAll(boolean bFreeMemory) {
				// TODO Auto-generated method stub

			}

		};

		mGestureActionHandler = new Handler(){
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);

				Log.w(TAG, "Timer run()");

				LinkedList<SObject> SObjectList = mSCanvas.getSObjectList(true);
				if(SObjectList==null){
					Log.e(TAG, "Sobject List is null");
					mSCanvas.clearScreen();
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
				if (mCurrentPoints.length > SPenGestureLibrary.GESTURE_MAX_GESTURE_STROKE){
					// Max gesture stroke number is 10
					SToastS("too many storke, less than 10 available.");
					mSCanvas.clearScreen();
					return;
				}
				
				mGestureInfo = mSPenGestureLibrary.recognizeSPenGesture(mCurrentPoints);
				// mGestureInfo.size is result number

				if (mGestureInfo == null) {
					SToastS("error");
					mSCanvas.clearScreen();
					return;
				}
				if(mGestureInfo.size()<=0){
					SToastS("There is no result");
					mSCanvas.clearScreen();
					return;
				}

				mImageViewCurrntGesture.setImageBitmap(mSPenGestureLibrary.tempMakeBitmapFromPoint(mSPenGestureLibrary.normalizePosition(mCurrentPoints)));

				SToastS("Gesture : " + mGestureInfo.get(0).mName);

				mResultListItem.clear();

				if (mGestureInfo.size() <= 10) {
					for (int i = 0; i < mGestureInfo.size(); i++) {
						mResultListItem.add(new ResultListItem(mGestureInfo.get(i).mIndex, mGestureInfo.get(i).mImage, mGestureInfo.get(i).mName, mGestureInfo.get(i).mScore));
					}
				} else {
					for (int i = 0; i < 10; i++) {
						mResultListItem.add(new ResultListItem(mGestureInfo.get(i).mIndex, mGestureInfo.get(i).mImage, mGestureInfo.get(i).mName, mGestureInfo.get(i).mScore));
					}
				}
				mResultAdapter.notifyDataSetChanged();

				// Finally, Clear Screen
				mSCanvas.clearScreen();				
			}
		};

		mSCanvas.setSCanvasInitializeListener(scanvasInitializeListener);
		mSCanvas.setSObjectUpdateListener(mSObjectListenerUI);

		mEditTextGestureFilePath = (EditText) findViewById(R.id.gesture_edittext_filepath);
		mEditTextGestureFilePath.setImeOptions(EditorInfo.IME_ACTION_DONE);
		mEditTextGestureName = (EditText) findViewById(R.id.gesture_edittext_gesture_name);
		mEditTextGestureName.setImeOptions(EditorInfo.IME_ACTION_DONE);
		mImageViewCurrntGesture = (ImageView) findViewById(R.id.gesture_image_input_gesture);
		findViewById(R.id.gesture_button_register).setOnClickListener(this);
		findViewById(R.id.gesture_button_reset_gesture_data).setOnClickListener(this);
		findViewById(R.id.gesture_button_load_user_data).setOnClickListener(this);
		findViewById(R.id.gesture_button_save_user_data).setOnClickListener(this);
		findViewById(R.id.gesture_button_test_activity).setOnClickListener(this);

		// list
		mGestureListItem = new ArrayList<GestureListItem>();
		mGestureAdapter = new GestureListAdapter(this);
		mGestureList = (ListView) findViewById(R.id.gesture_listview);
		mGestureList.setAdapter(mGestureAdapter);
		mGestureList.setItemsCanFocus(false);
		mGestureList.setTextFilterEnabled(true);
		mGestureList.setFocusable(true);
		mGestureList.setSelected(true);
		mGestureList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				AlertDialog.Builder ad = new AlertDialog.Builder(SPen_Example_PenGestureSetting.this);
				ad.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));

				if (arg2 + 1 > mCurGestureNumber) {
					ad.setTitle(getResources().getString(R.string.app_name)).setMessage("Delete All Gesture").setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// finish dialog
							dialog.dismiss();
							if (!mSPenGestureLibrary.deleteAllSPenGesture()) {
								SToastS("error");
							}
							ListUpdate();

						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
				}

			}
		});
		mGestureList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {

				AlertDialog.Builder ad = new AlertDialog.Builder(SPen_Example_PenGestureSetting.this);
				ad.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));

				if (arg2 + 1 > mCurGestureNumber) {
					ad.setTitle(getResources().getString(R.string.app_name)).setMessage("Delete All Gesture").setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// finish dialog
							dialog.dismiss();
							if (!mSPenGestureLibrary.deleteAllSPenGesture()) {
								SToastS("error");
							}
							ListUpdate();

						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
				} else {
					ad.setTitle(getResources().getString(R.string.app_name)).setMessage("Delete Gesture").setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// finish dialog
							dialog.dismiss();

							if (!mSPenGestureLibrary.deleteSPenGesture(arg2)) {
								SToastS("error");
							}
							ListUpdate();

						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
				}

				ad = null;

				return false;
			}

		});
		mResultListItem = new ArrayList<ResultListItem>();
		mResultAdapter = new ResultListAdapter(this);
		mResultList = (ListView) findViewById(R.id.result_listview);
		mResultList.setAdapter(mResultAdapter);
		mResultList.setItemsCanFocus(false);
		mResultList.setTextFilterEnabled(true);

		mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		KeyboardHide();
		ListUpdate();

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
	}


	public void SToastS(String i_String) {
		if (mToast == null) {
			mToast = Toast.makeText(this, i_String, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(i_String);
		}
		mToast.show();
	}

	public void ListUpdate() {

		ArrayList<SPenGestureInfo> CurrentGestures = mSPenGestureLibrary.getRegisteredSPenGesture();
		mCurGestureNumber = CurrentGestures.size();
		mGestureListItem.clear();

		for (int i = 0; i < CurrentGestures.size(); i++) {
			mGestureListItem.add(new GestureListItem(CurrentGestures.get(i).mName, CurrentGestures.get(i).mImage));
		}

		if (CurrentGestures.size() > 0) {
			mGestureListItem.add(new GestureListItem("Delete All Gesture", null));
		}
		mGestureAdapter.notifyDataSetChanged();
	}

	public void KeyboardHide() {
		mImm.hideSoftInputFromWindow(mEditTextGestureFilePath.getWindowToken(), 0);
		mImm.hideSoftInputFromWindow(mEditTextGestureName.getWindowToken(), 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gesture_button_register:
			KeyboardHide();
			if (mEditTextGestureName.getText().toString().compareToIgnoreCase("") == 0) {
				SToastS("Input name");
				mEditTextGestureName.setText("");
				break;
			}
			if (mCurrentPoints == null) {
				SToastS("Draw gesture");
				mEditTextGestureName.setText("");
				break;
			}
			if (!mSPenGestureLibrary.registerSPenGesture(mEditTextGestureName.getText().toString(), mCurrentPoints)) {
				SToastS("error");
				mEditTextGestureName.setText("");
				break;
			}
			ListUpdate();
			mGestureList.setSelection(mCurGestureNumber);
			mEditTextGestureName.setText("");
			break;
		case R.id.gesture_button_reset_gesture_data:
			KeyboardHide();
			if (!mSPenGestureLibrary.loadDefaultSPenGestureData()) {
				SToastS("error");
				break;
			}
			ListUpdate();
			break;
		case R.id.gesture_button_load_user_data:
			KeyboardHide();
			if (!mSPenGestureLibrary.loadUserSPenGestureData(mEditTextGestureFilePath.getText().toString())) {
				SToastS("File load error");
				break;
			}
			ListUpdate();
			SToastS("File load success");
			break;
		case R.id.gesture_button_save_user_data:
			KeyboardHide();
			if (!mSPenGestureLibrary.saveUserSPenGestureData(mEditTextGestureFilePath.getText().toString())) {
				SToastS("File save error");
				break;
			}
			ListUpdate();
			SToastS("File save success");
			break;
		case R.id.gesture_button_test_activity:
			KeyboardHide();
			// Intent intent = new Intent(SPen_Example_PenGestureSetting.this, SPen_Example_PenGesture_Test.class);
			Intent intent = new Intent(SPen_Example_PenGestureSetting.this, SPen_Example_PenGesture_Test.class);
			startActivity(intent);
			break;
		}
	}


	class GestureListItem {
		GestureListItem(String iGestureName, Bitmap iGuetureBitmap) {
			GestureName = iGestureName;
			GuetureBitmap = iGuetureBitmap;
		}

		String GestureName;
		Bitmap GuetureBitmap;
	}

	class GestureListAdapter extends BaseAdapter {
		LayoutInflater Inflater;

		public GestureListAdapter(Context context) {
			Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mGestureListItem.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = Inflater.inflate(R.layout.example_pengesture_list_gesture, parent, false);
			}

			if (position + 1 > mCurGestureNumber) {

				TextView Index = (TextView) convertView.findViewById(R.id.gesture_list_gesture_textview_index);
				Index.setVisibility(View.GONE);

				ImageView Image = (ImageView) convertView.findViewById(R.id.gesture_list_gesture_imageview);
				Image.setVisibility(View.GONE);

				TextView Name = (TextView) convertView.findViewById(R.id.gesture_list_gesture_textview_name);
				Name.setTextColor(0xffffc8c8);
				Name.setText(mGestureListItem.get(position).GestureName);
				Name.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));

			} else {

				TextView Index = (TextView) convertView.findViewById(R.id.gesture_list_gesture_textview_index);
				Index.setVisibility(View.VISIBLE);
				Index.setText("" + position);

				ImageView Image = (ImageView) convertView.findViewById(R.id.gesture_list_gesture_imageview);
				Image.setVisibility(View.VISIBLE);
				Image.setImageBitmap(mGestureListItem.get(position).GuetureBitmap);

				TextView Name = (TextView) convertView.findViewById(R.id.gesture_list_gesture_textview_name);
				Name.setTextColor(0xffffFFFF);
				Name.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				Name.setText(mGestureListItem.get(position).GestureName);
			}

			return convertView;
		}

	}

	class ResultListItem {
		ResultListItem(int iResultIndex, Bitmap iResultBitmap, String iResultName, int iResultScore) {
			ResultIndex = iResultIndex;
			ResultBitmap = iResultBitmap;
			ResultName = iResultName;
			ResultScore = iResultScore;
		}

		int ResultIndex;
		Bitmap ResultBitmap;
		String ResultName;
		int ResultScore;
	}

	class ResultListAdapter extends BaseAdapter {
		LayoutInflater Inflater;

		public ResultListAdapter(Context context) {
			Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mResultListItem.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = Inflater.inflate(R.layout.example_pengesture_list_result, parent, false);
			}

			TextView Index = (TextView) convertView.findViewById(R.id.gesture_list_result_textview_index);
			Index.setText("" + mResultListItem.get(position).ResultIndex);

			ImageView Image = (ImageView) convertView.findViewById(R.id.gesture_list_result_imageview);
			Image.setImageBitmap(mResultListItem.get(position).ResultBitmap);

			TextView Name = (TextView) convertView.findViewById(R.id.gesture_list_result_textview_name);
			Name.setText(mResultListItem.get(position).ResultName);

			TextView Score = (TextView) convertView.findViewById(R.id.gesture_list_result_textview_score);
			Score.setText("" + mResultListItem.get(position).ResultScore);

			return convertView;
		}

	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));
		ad.setTitle(getResources().getString(R.string.app_name)).setMessage("Exit this program").setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// finish dialog
				dialog.dismiss();
				finish();
				mSPenGestureLibrary.closeSPenGestureEngine();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		}).show();
		ad = null;
	}

}
