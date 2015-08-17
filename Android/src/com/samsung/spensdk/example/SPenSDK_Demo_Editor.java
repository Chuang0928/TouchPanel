package com.samsung.spensdk.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.example.basiceditor.SPen_Example_BasicEditor;
import com.samsung.spensdk.example.basicui.SPen_Example_BasicUI;
import com.samsung.spensdk.example.bgfg.SPen_Example_BackgroundForeground;
import com.samsung.spensdk.example.canvassize.SPen_Example_CanvasSize;
import com.samsung.spensdk.example.canvassize.SPen_Example_CanvasSizeStatic;
import com.samsung.spensdk.example.colorpickerfilling.SPen_Example_ColorPickerFilling;
import com.samsung.spensdk.example.groupingeditor.SPen_Example_GroupingEditor;
import com.samsung.spensdk.example.hoverpointer.SPen_Example_HoverPointer;
import com.samsung.spensdk.example.insertstamp.SPen_Example_InsertStamp;
import com.samsung.spensdk.example.sammeditor.SPen_Example_SAMMEditor;
import com.samsung.spensdk.example.settingview.SPen_Example_SettingView;
import com.samsung.spensdk.example.settingview_custom.SPen_Example_SettingViewCustom;
import com.samsung.spensdk.example.startup.SPen_Example_StartUp;
import com.samsung.spensdk.example.zoompan.SPen_Example_ZoomPan;

public class SPenSDK_Demo_Editor extends Activity {

	private ListAdapter mListAdapter = null;
	private ListView mListView = null;	

	private int mEditorGUIStyle = SCanvasConstants.SCANVAS_GUI_STYLE_NORMAL;
	private boolean mbSingleSelectionFixedLayerMode = false;

	// The item of list
	private static final int 	SDK_EDITOR_STARTUP		= 0;
	private static final int 	SDK_EDITOR_BASIC_UI		= 1;	
	private static final int 	SDK_EDITOR_ZOOMPAN 		= 2;
	private static final int 	SDK_EDITOR_CANVAS_SIZE	= 3;
	private static final int 	SDK_EDITOR_CANVAS_SIZE_STATIC	= 4;
	private static final int 	SDK_EDITOR_COLORPICKER 	= 5;
	private static final int 	SDK_EDITOR_SETTINGVIEW 	= 6;
	private static final int 	SDK_EDITOR_SETTINGVIEW_CUSTOM 	= 7;
	private static final int 	SDK_EDITOR_BGFG			= 8;
	private static final int 	SDK_EDITOR_STAMP		= 9;
	private static final int 	SDK_EDITOR_HOVERPOINTER	= 10;
	private static final int 	SDK_EDITOR_BASICEDITOR 	= 11;		
	private static final int 	SDK_EDITOR_GROUPING 	= 12;
	private static final int 	SDK_EDITOR_SAMMEDITOR  = 13;
	private static final int 	TOTAL_LIST_NUM 			= 14;	



	private final String EXAMPLE_NAMES[] = {
			"Editor : StartUp",
			"Editor : Basic UI",
			"Editor : Zoom & Pan",
			"Editor : Canvas Size",
			"Editor : Canvas Size (Static)",
			"Editor : ColorPicker & Filling",
			"Editor : SettingView",
			"Editor : Custom SettingView",
			"Editor : BG & FG",			
			"Editor : Insert Stamp",
			"Editor : Hover Pointer",
			"Editor : Basic Editor",
			"Editor : Grouping Editor",
			"Editor : SAMM Editor",
	};

	@Override 
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.spensdk_demo);

		createUI();
	}

	private void createUI() {
		TextView textTitle = (TextView)findViewById(R.id.title);
		textTitle.setText(SPenSDK_Demo.SECTION_EDITOR);
		textTitle.setTextColor(0xFFFFCCCC);

		mListAdapter = new ListAdapter(this);
		mListView = (ListView)findViewById(R.id.demo_list);
		mListView.setAdapter(mListAdapter);

		mListView.setItemsCanFocus(false);
		mListView.setTextFilterEnabled(true);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// S Pen SDK Demo programs
				if(position == SDK_EDITOR_STARTUP) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_StartUp.class);				
					startActivity(intent);					
				}
				else if(position == SDK_EDITOR_BASIC_UI) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_BasicUI.class);				
					startActivity(intent);
				}
				else if(position == SDK_EDITOR_ZOOMPAN) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_ZoomPan.class);				
					startActivity(intent);
				}
				else if(position == SDK_EDITOR_CANVAS_SIZE) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_CanvasSize.class);				
					startActivity(intent);
				}
				else if(position == SDK_EDITOR_CANVAS_SIZE_STATIC) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_CanvasSizeStatic.class);				
					startActivity(intent);
				}
				else if(position == SDK_EDITOR_COLORPICKER) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_ColorPickerFilling.class);				
					startActivity(intent);
				}
				else if(position == SDK_EDITOR_SETTINGVIEW) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_SettingView.class);				
					startActivity(intent);
				}
				else if(position == SDK_EDITOR_SETTINGVIEW_CUSTOM) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_SettingViewCustom.class);						
					startActivity(intent);
				}
				else if(position == SDK_EDITOR_BGFG) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_BackgroundForeground.class);				
					startActivity(intent);					
				}
				else if(position == SDK_EDITOR_STAMP) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_InsertStamp.class);				
					startActivity(intent);		
				}
				else if(position == SDK_EDITOR_HOVERPOINTER) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_HoverPointer.class);				
					startActivity(intent);					
				}
				else if(position == SDK_EDITOR_BASICEDITOR) {
					showEditorVersionDialog(position);						
				}
				else if(position == SDK_EDITOR_GROUPING) {
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_GroupingEditor.class);				
					startActivity(intent);
				}
				else if(position == SDK_EDITOR_SAMMEDITOR) {
					showEditorVersionDialog(position);	
				}
			}
		});
	}

	//=========================================
	// List Adapter : S Pen SDK Demo Programs   
	//=========================================
	public class ListAdapter extends BaseAdapter {

		public ListAdapter(Context context) {
		}    	

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.spensdk_demolist_item, parent, false);
			}
			// UI Item			
			TextView tvListItemText=  (TextView)convertView.findViewById(R.id.listitemText);
			tvListItemText.setTextColor(0xFFFFFFFF);

			//==================================
			// basic data display 
			//==================================
			if(position < TOTAL_LIST_NUM){
				tvListItemText.setText(EXAMPLE_NAMES[position]);

			}
			return convertView; 
		}

		public void updateDisplay() {
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return TOTAL_LIST_NUM;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	private void showEditorVersionDialog(final int kindOfEditor){
		new AlertDialog.Builder(this)
		.setTitle("Editor Version")
		.setItems(R.array.editor_version, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// S Pen SDK 2.2 (Single selection, Fixed layer Editor : Image-Text-Stroke ordering)
				if(which == 0){
					mbSingleSelectionFixedLayerMode = true;
				}
				// S Pen SDK 2.3 (Multi-selection, Flexible layer Editor : Input orderering)
				else{
					mbSingleSelectionFixedLayerMode = false;
				}

				showGUIStyleDialog(kindOfEditor);

				dialog.dismiss();
			}
		})
		.show();	
	}


	private void showGUIStyleDialog(final int kindOfEditor){
		new AlertDialog.Builder(this)
		.setTitle("GUI Style")
		.setItems(R.array.editor_gui_style, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == SCanvasConstants.SCANVAS_GUI_STYLE_NORMAL){
					mEditorGUIStyle = SCanvasConstants.SCANVAS_GUI_STYLE_NORMAL;
				}
				else{
					mEditorGUIStyle = SCanvasConstants.SCANVAS_GUI_STYLE_KIDS;
				}		

				if(kindOfEditor == SDK_EDITOR_BASICEDITOR){
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_BasicEditor.class);		
					intent.putExtra(SPen_Example_BasicEditor.KEY_EDITOR_VERSION, mbSingleSelectionFixedLayerMode);
					intent.putExtra(SPen_Example_BasicEditor.KEY_EDITOR_GUI_STYLE, mEditorGUIStyle);
					startActivity(intent);
				}
				else{
					Intent intent = new Intent(SPenSDK_Demo_Editor.this, SPen_Example_SAMMEditor.class);	
					intent.putExtra(SPen_Example_SAMMEditor.KEY_EDITOR_VERSION, mbSingleSelectionFixedLayerMode);
					intent.putExtra(SPen_Example_SAMMEditor.KEY_EDITOR_GUI_STYLE, mEditorGUIStyle);
					startActivity(intent);
				}

				dialog.dismiss();
			}
		})
		.show();	
	}
}
